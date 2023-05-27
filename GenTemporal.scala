//> using scala 3.3.0
//> using toolkit typelevel:latest

import cats.Applicative
import cats.syntax.all.*
import cats.effect.{ IO, IOApp, Sync, Temporal }
import cats.effect.syntax.all.*
import concurrent.duration.*

object GenTemporalExample extends IOApp.Simple:

  val run: IO[Unit] =
    for
      _ <- IO.println("waiting 1 second")
      x <- wait[IO](IO("hello world"))
      _ <- IO.println(x)
    yield ()

  def wait[F[_]: Async](f: F[String])(using T: Temporal[F]): F[String] =
    T.sleep(1.second) *> f
