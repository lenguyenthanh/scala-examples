//> using scala 3.5.0
//> using dep org.typelevel::cats-effect:3.5.4
// //> using dep org.typelevel::cats-effect::3.6-206020c-20240908T062035Z-SNAPSHOT

import java.util.concurrent.atomic.AtomicInteger
import cats.effect.*
import scala.concurrent.duration.*

object Main extends IOApp.Simple:
  def run =
    inplace(IO.println("hello world"))(_ *> IO.println("canceled")) *>
    IO.println("hello world")

  def inplace(task: IO[Unit])(registerCancel: IO[Unit] => IO[Unit]): IO[Unit] = {
    Concurrent[IO].deferred[Unit].flatMap { d =>
      (registerCancel(d.get) *> task).guarantee(d.complete(()).void)
    }
  }
