//> using toolkit typelevel:default

import cats.*
import cats.data.*
import cats.syntax.all.*

def doStuff[F[_]: Monad, A, B, E: Semigroup](nel: NonEmptyList[A])(f: A => F[Either[E, B]]) : F[Either[E, B]] =
  nel.tail.foldLeft(f(nel.head)) { (x, y) =>
    x.flatMap {
      case Right(b) => b.asRight.pure[F]
      case Left(errs) =>
        f(y).map {
          case Right(b) => b.asRight
          case Left(err) => (errs |+| err).asLeft
        }
    }
  }

