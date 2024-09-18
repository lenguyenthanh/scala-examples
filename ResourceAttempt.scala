//> using scala 3.5.0
// //> using dep org.typelevel::cats-effect::3.6-7168625-20240902T174829Z-SNAPSHOT
//> using dep org.typelevel::cats-effect::3.5.4

import cats.effect.*
import cats.syntax.all.*

object Bug extends IOApp.Simple:
  def run = IO.ref(0).flatMap { ref =>
    val resource = Resource.make(ref.update(_ + 1))(_ => ref.update(_ + 1))
    val error    = Resource.raiseError[IO, Unit, Throwable](new Exception)

    // val attempted: Resource[IO, Either[Throwable, Unit]] = (resource *> error).safeAttempt
    // val attempted: Resource[IO, Either[Throwable, Unit]] = resource *> error.attempt

    attempted.use { r =>
      ref.get.flatMap { i =>
        IO {
          // acquiring the resource as a whole failed, so it should be released in entirety
          assert(r.isLeft, r.toString)
          assert(i == 2, i.toString) // assertion failed: 1
        }
      }
    }
  }
