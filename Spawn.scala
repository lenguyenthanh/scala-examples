//> using scala 3.5.0
//> using dep org.typelevel::cats-effect::3.6-206020c-20240908T062035Z-SNAPSHOT
// //> using dep org.typelevel::cats-effect::3.5.4

import cats.effect.*
import cats.syntax.all.*
import scala.concurrent.duration.*

object FunTest extends IOApp.Simple {
  override val run: IO[Unit] = for {
    _ <- IO.println("Starting")
    child = IO.println("Starting child...") >>
      IO.sleep(1.second) >>
      IO.canceled >>
      IO.sleep(1.second) >>
      IO.println("Child completed")
    childFiber     <- child.start
    dependentChild <- childFiber.joinWithNever.as("I managed to return something").start
    result <- dependentChild
      .joinWith(IO.pure("I was cancelled"))
      .timeoutTo(3.seconds, "I waited too long :(".pure[IO])
    _ <- IO.println(s"result is $result")
  } yield ()
}
