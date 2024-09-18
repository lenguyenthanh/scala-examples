//> using scala 3.5.0
// //> using dep org.typelevel::cats-effect:3.5.4
//> using dep org.typelevel::cats-effect::3.6-206020c-20240908T062035Z-SNAPSHOT

import java.util.concurrent.atomic.AtomicInteger
import cats.effect.*
import scala.concurrent.duration.*

object Main extends IOApp.Simple {

  def run1 =
    // IO.canceled >> IO.println("Hello, World!")) // print nothing
    // IO.uncancelable(_ => IO.canceled >> IO.println("Hello, World!")) // print Hello, World!
    for
      fib <- (IO.uncancelable(_ => IO.sleep(1.second)) >> IO.println(
        "Hello, World!"
      )).start // print Hello, World!
      _ <- fib.cancel
      _ <- fib.joinWithNever
    yield ()

  def run3 =
    for {
      fib <- (IO.println("hell") >> IO.never.onCancel(IO.println("I'm canceled")) >> IO.println("world")).start// >> IO.println("hello")).start
      _ <- IO.sleep(1.second)
      _   <- fib.cancel >> IO.println("fib canceled")
      _   <- fib.joinWith(IO.println("start") >> IO.canceled >> IO.println("end") >> IO.never)
      // _   <- fib.joinWith(IO.println("start") >> IO.println("end") >> IO.never)
    } yield ()

  def run2: IO[Unit] =
    IO(new AtomicInteger).flatMap { ctr =>
      val test = IO.deferred[Unit].flatMap { latch =>
        val t = latch.complete(()).uncancelable *> IO.async_[Unit] { cb =>
          ctr.getAndIncrement()
          cb(Right(()))
        }
        t.start.flatMap { fib =>
          latch.get *> fib.cancel *> fib.joinWithUnit
        }
      }
      val N = 100
      test.replicateA_(N).flatMap { _ =>
        IO(ctr.get()).flatMap { count =>
          IO.println(count == N)
        }
      }
    }

  def run: IO[Unit] =
    run3
}
