//> using scala 3.7.1
//> using dep co.fs2::fs2-core:3.12.0


import cats.syntax.all.*
import cats.effect.*
import fs2.Stream
import scala.concurrent.duration.*

object main extends IOApp.Simple {
  import fs2.Stream

  // Define a method to get the last element of a stream or return an error if the stream is empty
  def lastOrError[A](stream: Stream[IO, A]): IO[A] = {
    stream.compile.lastOrError
      .onError( e => IO.println(s" blabl Error occurred: ${e.getMessage}"))
  }

  // Example usage
  // val exampleStream = Stream.empty // This is an empty stream
  // val exampleStream = Stream(1, 2, 3) // This is a non-empty stream

  val f = IO.println("ffffffffffffffffffffffffff") *> IO.sleep(10.milliseconds) *> (new RuntimeException(
    "Renewing Vault token exception"
  )).raiseError[IO, Int]


  val initialRenewBackoff: FiniteDuration = 100.milliseconds
  val waitInterval: FiniteDuration = 100.milliseconds
  val x  = IO.println(s"renewing vault") *>
        IO.sleep(waitInterval) >>
        fs2.Stream.retry(f, initialRenewBackoff, identity, 8)
          .compile
          .lastOrError
          .onError( e => IO.println(s" blabl Error occurred: ${e.getMessage}"))

  def terminatingOnError(e: Throwable): F[Unit] =
    if (F.isInstanceOf[Temporal[IO] @unchecked]) {
      // If we are in an IO context, we can signal the process control
      log.error(e)("Unable to renew Vault token, terminating process") *>
        Environment.global.processControl.signal(e).asInstanceOf[F[Unit]]
    } else {
      log.error(e)("Unable to renew Vault token")
    }



  def run: IO[Unit] =
    // lastOrError(exampleStream).evalTap(IO.println) *>
    x  *>
      IO.println(s"The last element is: ")
}
