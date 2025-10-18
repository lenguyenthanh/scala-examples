//> using scala 2.13.15
//> using toolkit typelevel:0.1.29
//> using plugin org.typelevel:::kind-projector:0.13.3
//> using plugin com.olegpy::better-monadic-for:0.3.1
//> using dep org.typelevel::cats-mtl:1.6.0

import cats.{Id, Monad, Functor, MonadThrow}
import cats.data.Chain
import cats.syntax.all._
import cats.effect.{Clock, IO, IOApp, Sync}
import cats.effect.std.Console
import cats.mtl.{Raise, Handle}
import java.time.Instant
import scala.util.control.{NoStackTrace, NonFatal}

final case class OperationFailure(at: Instant)
object OperationFailure {
  def now[F[_]: Clock: Functor]: F[OperationFailure] = Clock[F].realTimeInstant.map(OperationFailure(_))
}

final case class OutOfRetries[E](attempts: Vector[E])
  extends RuntimeException(s"Retries exceeded after ${attempts.length} attempts")
    with NoStackTrace {
  override def toString: String =
    attempts.mkString(s"$getMessage:\n  ", "\n  ", "\n")
}

object Main extends IOApp.Simple {

  def withRetries[F[_] : MonadThrow, A](limit: Int)(body: F[A]): F[A] = {
    def loop(remaining: Int, errors: Chain[Throwable]): F[A] =
      if (remaining <= 0) OutOfRetries(errors.toVector).raiseError[F, A]
      else body.recoverWith { case NonFatal(e) =>
        loop(remaining - 1, errors.append(e))
      }

    loop(limit, Chain.empty)
  }

  def runF[F[_] : MonadThrow: Clock : Console]: F[Unit] =
    Handle
      .allowF[F, OperationFailure] { implicit h =>
        withRetries(2) {
          OperationFailure.now[F].flatMap(h.raise[OperationFailure, Unit])
        }
      }
      .rescue { of =>
        Console[F].println {
          s"""============ OperationFailure will never be caught here ============
             |$of""".stripMargin
        }
      }
      .recoverWith {
        case e: OutOfRetries[_] => Console[F].println {
          s"""============ Expecting OutOfRetries with nested OperationFailures ============
             |$e""".stripMargin
        }
      }

  override val run: IO[Unit] = runF[IO].recoverWith {
    case e: Throwable => Console[IO].println {
      s"""============ Unhandled failure ============
         |$e
         |""".stripMargin
    }
  }
}

