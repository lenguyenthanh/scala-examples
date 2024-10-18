//> using scala 3.5.1
//> using toolkit typelevel:0.1.28
//> using deps co.fs2::fs2-scodec:3.11.0
//> using deps com.github.plokhotnyuk.jsoniter-scala::jsoniter-scala-core:2.30.12
//> using deps com.github.plokhotnyuk.jsoniter-scala::jsoniter-scala-macros:2.30.12
//> using deps org.http4s::http4s-jdk-http-client::0.9.1

import cats.effect.{ Concurrent, IO, IOApp }
import fs2.Stream
import org.http4s.client.websocket._
import org.http4s.implicits._
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.client.Client
import com.comcast.ip4s.SocketAddress
import com.comcast.ip4s.IpAddress
import fs2.io.net.Network
import cats.effect.std.Queue
import cats.syntax.all.*
import fs2.Stream
import fs2.io.net.Socket
import fs2.interop.scodec.{ StreamDecoder, StreamEncoder }
import scodec.*
import scodec.codecs.*
import cats.ApplicativeError
import com.comcast.ip4s.*
import java.net.ConnectException
import scala.concurrent.duration.*
import scala.util.Try
import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import org.http4s.jdkhttpclient.JdkWSClient

object WebSocketClient extends IOApp.Simple:
  val address = SocketAddress(IpAddress.fromString("127.0.0.1").get, port"9664")
  def run: IO[Unit] =
    client
      .start(address, "nt9")
      .compile
      .drain

object ws:
  val client = JdkWSClient.simple[IO]()

object client:

  def start(
      address: SocketAddress[IpAddress],
      desiredUsername: String
  ): Stream[IO, Unit] =
    connect(address, desiredUsername).handleErrorWith {
      case _: ConnectException =>
        val retryDelay = 5.seconds
        Stream.exec(IO.println(s"Failed to connect. Retrying in $retryDelay.")) ++
          start(address, desiredUsername)
            .delayBy(retryDelay)
      case t => Stream.raiseError(t)
    }
  private def connect(
      address: SocketAddress[IpAddress],
      userName: String
  ): Stream[IO, Unit] =
    Stream.exec(IO.println(s"Connecting to server $address")) ++
      Stream
        .resource(Network[IO].client(address))
        .flatMap { socket =>
          Stream.exec(IO.println("🎉 Connected! 🎊")) ++
            Stream
              .eval(
                MessageSocket(
                  socket,
                  Protocol.ServerCommand.codec,
                  Protocol.ClientOut.codec,
                  128
                )
              )
              .flatMap { messageSocket =>
                // Stream.exec(
                //   messageSocket.write1(Protocol.ClientOut.RequestUsername(userName))
                // ) ++
                processIncoming(messageSocket).concurrently(
                  processOutgoing(messageSocket)
                )
              }
        }

  private def processIncoming(
      messageSocket: MessageSocket[IO, Protocol.ServerCommand, Protocol.ClientOut]
  ): Stream[IO, Unit] =
    messageSocket.read.evalMap {
      case Protocol.ServerCommand.Message(username, txt) =>
        IO.println(s"$username: $txt")
      case Protocol.ServerCommand.SetUsername(username) =>
        IO.println(s"👋 Welcome $username! 👋")
      case Protocol.ServerCommand.Disconnect =>
        IO.println("Disconnected from server.")
    }

  private def processOutgoing(
      messageSocket: MessageSocket[IO, Protocol.ServerCommand, Protocol.ClientOut]
  ): Stream[IO, Unit] =
    Stream
      .emits(List(1L, 2L, 3L))
      .covary[IO]
      .map(txt => Protocol.ClientOut.Ping(Some(txt)))
      .evalMap(x =>
        IO.println(
          s"send ping ${x.asInstanceOf[Protocol.ClientOut.Ping].timestamp}"
        ) *> messageSocket.write1(x)
      )

/** Defines the messages exchanged between the client and server. */

object Protocol:

  private val username: Codec[String] =
    utf8_32.as[String]

  import com.github.plokhotnyuk.jsoniter_scala.macros.*

  /** Base trait for messages sent from the client to the server. */
  enum ClientOut(@transient tpe: String):
    @named("p") case Ping(@named("l") timestamp: Option[Long]) extends ClientOut("p")

  object ClientOut:

    import com.github.plokhotnyuk.jsoniter_scala.core._
    given JsonValueCodec[ClientOut] = JsonCodecMaker.make:
      CodecMakerConfig
        .withDiscriminatorFieldName(Some("t"))

    val codec: Codec[ClientOut] =
      Codec[String].exmap(
        str => Attempt.fromTry(Try(readFromString(str))),
        out =>
          println(writeToString(out))
          Attempt.successful(writeToString(out))
      )

  /** Base trait for messages sent from the server to the client. */
  enum ServerCommand:
    case SetUsername(name: String)
    case Message(name: String, text: String)
    case Disconnect

  object ServerCommand:
    val codec: Codec[ServerCommand] = discriminated[ServerCommand]
      .by(uint8)
      .typecase(129, username.as[SetUsername])
      .typecase(131, (username :: utf8_32).as[Message])
      .typecase(132, provide(Disconnect))

/** Socket which reads a stream of messages of type `In` and allows writing messages of type `Out`.
  */
trait MessageSocket[F[_], In, Out]:
  def read: Stream[F, In]
  def write1(out: Out): F[Unit]

object MessageSocket:

  def apply[F[_]: Concurrent, In, Out](
      socket: Socket[F],
      inDecoder: Decoder[In],
      outEncoder: Encoder[Out],
      outputBound: Int
  ): F[MessageSocket[F, In, Out]] =
    for outgoing <- Queue.bounded[F, Out](outputBound)
    yield new MessageSocket[F, In, Out] {
      def read: Stream[F, In] =
        val readSocket = socket.reads
          .through(StreamDecoder.many(inDecoder).toPipeByte[F])

        val writeOutput = Stream
          .fromQueueUnterminated(outgoing)
          .through(StreamEncoder.many(outEncoder).toPipeByte)
          .through(socket.writes)

        readSocket.concurrently(writeOutput)
      def write1(out: Out): F[Unit] = outgoing.offer(out)
    }
