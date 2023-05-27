//> using scala 3.3.0
//> using toolkit typelevel:latest
//> using dep "org.http4s::http4s-dsl:0.23.19"
//> using dep io.circe::circe-generic:0.14.5
//> using dep org.gnieh::fs2-data-json:1.7.1
//> using dep org.gnieh::fs2-data-json-circe:1.7.1

import cats.MonadThrow
import io.circe.generic.auto.*
import fs2.{Pipe, Stream}
import cats.effect.{ IO, IOApp, Temporal }
import cats.syntax.all.*
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.client.Client
import org.http4s.*
import org.http4s.headers.*
import org.http4s.implicits.*
import scala.util.control.NoStackTrace
import scala.concurrent.duration.*

import fs2.data.json.*
import fs2.data.json.circe.*

// Stream the response body as a string, then parse it as ndjson
// This is the same as:
// curl -X POST "https://lichess.org/api/games/export/_ids" -H "accept: application/x-ndjson" -H "Content-Type: text/plain" -d "TJxUmbWK,4OtIh2oh,ILwozzRZ"

object FetchLichessGame extends IOApp.Simple:

  val gameIds = List("TJxUmbWK","4OtIh2oh","ILwozzRZ")

  val run: IO[Unit] =
      EmberClientBuilder.default[IO].build.map(Games.make)
      .use(_.fetch(gameIds).evalTap(IO.println).compile.drain)

trait Games[F[_]]:
  def fetch(ids: List[GameId]): Stream[F, Game]

object Games:

  case class GameError(message: String) extends NoStackTrace

  def make[F[_]: Temporal](client: Client[F]): Games[F] = new:

    override def fetch(ids: List[GameId]): Stream[F, Game] = client
      .stream(createRequest(ids))
      .through(handle429)
      .through(untilSome)
      .flatMap(_.bodyText)
      .through(tokens[F, String])
      .through(codec.deserialize[F, Game])

    def createRequest(ids: List[GameId]) = Request[F](
      method = Method.POST,
      uri = uri"https://lichess.org/api/games/export/_ids",
      headers = Headers(Accept(ndJson)),
    ).withEntity(ids.mkString(","))

    def untilSome[A]: Pipe[F, Option[A], A] = xs =>
      (xs ++ Stream.sleep(1.minute).as(none))
        .repeat
        .collectFirst { case (Some(x)) => x }

    def handle429: Pipe[F, Response[F], Option[Response[F]]] = _.evalMap: response =>
      if response.status == Status.TooManyRequests then none.pure[F]
      else if response.status.isSuccess then response.some.pure[F]
      else MonadThrow[F].raiseError(GameError(s"Unexpected status code: ${response.status}"))

  private val ndJson = MediaType("application", "x-ndjson", true, false, List("ndjson"))

type GameId = String

case class Clock(
    initial: Int,
    increment: Int,
    totalTime: Int
)

case class Players(
    white: Player,
    black: Player
)

case class Game(
    id: String,
    rated: Boolean,
    variant: String,
    speed: String,
    perf: String,
    createdAt: Long,
    lastMoveAt: Long,
    status: String,
    players: Players,
    winner: String,
    moves: String,
    tournament: String,
    clock: Clock
)

case class User(
    name: String,
    id: String
)

case class Player(
    user: User,
    rating: Int
)
