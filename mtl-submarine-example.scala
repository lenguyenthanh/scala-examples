// //> using scala 2.13.16
//> using scala 3.7.1
//> using dep org.typelevel::cats-effect:3.6.3
//> using dep org.typelevel::cats-mtl:1.5.0-97-fb9f86f-SNAPSHOT
//> using dep org.typelevel::cats-core:2.13.0

import cats.*
import cats.syntax.all.*
import cats.mtl.*
import cats.mtl.implicits.*
import cats.mtl.Handle.*

object Submarine:
  type F[A] = Either[Throwable, A]

  enum DomainError:
    case Failed
    case Derped

  def foo(using h: Raise[F, DomainError]): F[String] = Either.right("foo")
  def bar(using h: Handle[F, DomainError]): F[String] = h.raise(DomainError.Failed)

  def submarine: F[String] =
    allow:
      foo *> bar
    .rescue:
      case DomainError.Failed => Either.right("Handled Failed")
      case DomainError.Derped => Either.right("Handled Derped")

/* scala 2
import cats._
import cats.syntax.all._
import cats.mtl._
import cats.mtl.implicits._
import cats.mtl.Handle._

object Submarine {
  type F[A] = Either[Throwable, A]

  sealed trait DomainError
  object DomainError {
    case object Failed extends DomainError
    case object Derped extends DomainError
  }

  def foo(implicit h: Raise[F, DomainError]): F[String] = Either.right("foo")
  def bar(implicit h: Handle[F, DomainError]): F[String] = h.raise(DomainError.Failed)

  def submarine: F[String] =
    allowF[F, DomainError] { implicit h =>
      foo *> bar
    } rescue {
      case DomainError.Failed => Either.right("Handled Failed")
      case DomainError.Derped => Either.right("Handled Derped")
    }
}
// */
