//> using platform native
//> using nativeVersion 0.4.17
//> using scala 3.3.3
//> using dep "org.typelevel::cats-effect::3.5.4"

import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple {
  def run: IO[Unit] = IO.println("Hello Cats Effect!")
}
