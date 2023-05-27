//> using scala 3.3.0
//> using toolkit typelevel:latest

import cats.effect.{ IO, IOApp, Sync, Temporal }
import cats.syntax.all.*
import concurrent.duration.*

object GenTemporalExample extends IOApp.Simple:

  val run: IO[Unit] =
    for
      _ <- IO.println("waiting 1 second")
      x <- wait(IO("hello world"))
      _ <- IO.println(x)
    yield ()

  def wait[F[_]: Sync: Temporal](f: F[String]): F[String] =
    Temporal[F].sleep(1.second) >> f
