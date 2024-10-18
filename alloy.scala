//> using scala 3.5.1
//> using toolkit typelevel:0.1.28

import cats.effect.{ Concurrent, IO, IOApp }

object main extends IOApp.Simple:
  def run: IO[Unit] =
    IO.println("Hello, world!")
