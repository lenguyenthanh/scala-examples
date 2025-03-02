//> using toolkit typelevel:default
//> using scala 3.nightly
//> using dep org.typelevel::cats-mtl:1.5.0
//> using options -language:higherKinds -Xkind-projector
//-Xprint:typer

import cats.effect.*
import cats.mtl.{ Handle, Raise }
import cats.syntax.all.*
import cats.mtl.syntax.all.*
import cats.*
import cats.data.NonEmptyList
import scala.util.control.NoStackTrace
import scala.reflect.ClassTag

object Alternatives:

  given [F[_]: Monad, E: Monoid] => Alternative[[X] =>> F[Either[E, X]]]:
    def pure[A](x: A): F[Either[E, A]] = x.asRight.pure[F]

    def ap[A, B](ff: F[Either[E, A => B]])(fa: F[Either[E, A]]): F[Either[E, B]] =
      (ff, fa).mapN { (f, a) =>
        (f, a) match
          case (Right(f), Right(a)) => Right(f(a))
          case (Left(e1), Left(e2)) => Left(e1 |+| e2)
          case (Left(e), _)         => Left(e)
          case (_, Left(e))         => Left(e)
      }

    def empty[A]: F[Either[E, A]] =
      Monoid[E].empty.asLeft.pure[F]

    def combineK[A](x: F[Either[E, A]], y: F[Either[E, A]]): F[Either[E, A]] =
      x.flatMap {
        case Right(a) => a.asRight.pure[F]
        case Left(e)  => y.map(_.left.map(e |+| _))
      }

  given raiseMonadAlternative: [F[_]: Monad, E: Monoid] => (R: Handle[F, E]) => Alternative[F]:
    def pure[A](x: A): F[A] = x.pure[F]
    def ap[A, B](ff: F[A => B])(fa: F[A]): F[B] =
      ff.flatMap(f => fa.map(f))
    def empty[A]: F[A] = Monoid[E].empty.raise[F, A]
    def combineK[A](x: F[A], y: F[A]): F[A] =
      x.handleWith[E] { e1 =>
        y.handleWith[E] { e2 =>
          (e1 |+| e2).raise[F, A]
        }
      }

/**
  * Algebra is a sealed trait that represents a computation that can be failed.
  * For example, a computation that fetches a token from a
  */

object HandleSemigroupK:

  given [F[_], E: Semigroup] => Handle[F, E] => SemigroupK[F]:
    def combineK[A](x: F[A], y: F[A]): F[A] =
      x.handleWith[E] { e1 =>
        y.handleWith[E] { e2 =>
          (e1 |+| e2).raise[F, A]
        }
      }

  given [F[_]: ApplicativeThrow, E <: Throwable: ClassTag] => Handle[F, E]:
    def applicative: Applicative[F] = summon[Applicative[F]]
    def raise[E2 <: E, A](e: E2): F[A] = e.raiseError
    def handleWith[A](fa: F[A])(f: E => F[A]): F[A] =
      fa.handleErrorWith:
        case e: E => f(e)
        case e     => e.raiseError

  // type ~>[-F[_], +G[_]] = [A] => F[A] => G[A]
  // class IsoK[F[_], G[_]](val to: F ~> G, val from: G ~> F)

  // given [F[_]: SemigroupK, G[_]] => (iso: IsoK[F, G]) => SemigroupK[G]:
  //   def combineK[A](x: G[A], y: G[A]): G[A] =
  //     iso.to(iso.from(x) <+> iso.from(y))

  // given symmetry: [F[_], G[_]] => (fg: IsoK[F, G]) => IsoK[G, F] =
  //   new IsoK[G, F](to = fg.from, from = fg.to)

sealed trait Algebra[F[_], A]:
  def fetch: F[A]

object Algebra:
  import HandleSemigroupK.{*, given}

  // val x : NonEmptyList[Int] = NonEmptyList.of(1, 2, 3)
  // val y : NonEmptyList[Int] = NonEmptyList.of(4, 5, 6)
  // val z = x <+> y

  sealed trait Error extends Throwable with NoStackTrace
  object Error:
    case class Single(msg: String) extends Error:
      override def toString() = msg
    case class Multiple(errors: NonEmptyList[Error]) extends Error:
      override def toString() = errors.toList.mkString("", "\n", "")

    def apply(msg: String): Error = Single(msg)

  given Semigroup[Error] with
    def combine(x: Error, y: Error): Error =
      (x, y) match
        case (Error.Single(msg1), Error.Single(msg2)) => Error.Multiple(NonEmptyList.of(Error.Single(msg1), Error.Single(msg2)))
        case (Error.Single(msg1), Error.Multiple(errors)) => Error.Multiple(errors.prepend(Error.Single(msg1)))
        case (Error.Multiple(errors), Error.Single(msg2)) => Error.Multiple(errors.append(Error.Single(msg2)))
        case (Error.Multiple(errors1), Error.Multiple(errors2)) => Error.Multiple(errors1.concatNel(errors2))

  // given [F[_]]: IsoK[F, Algebra[F, *]] =
  //   new IsoK[F, Algebra[F, *]](
  //     to = [A] => (fa: F[A]) => new Algebra[F, A]:
  //       def fetch: F[A] = fa,
  //     from = [A] => (fa: Algebra[F, A]) => fa.fetch,
  //   )

  // given x: Handle[IO, Error] = summon[Handle[IO, Error]]
  // val y = summon[SemigroupK[IO]]

  given [F[_]: Applicative]: Applicative[Algebra[F, *]] with
    def pure[A](x: A): Algebra[F, A] =
      new Algebra[F, A]:
        def fetch: F[A] = x.pure[F]

    def ap[A, B](ff: Algebra[F, A => B])(fa: Algebra[F, A]): Algebra[F, B] =
      new Algebra[F, B]:
        def fetch: F[B] =
          ff.fetch.ap(fa.fetch)

  given [F[_]: ApplicativeThrow]: Handle[Algebra[F, *], Error] with
    def applicative: Applicative[Algebra[F, *]] = summon[Applicative[Algebra[F, *]]]
    def raise[E2 <: Error, A](e: E2): Algebra[F, A] = new Algebra[F, A]:
      def fetch: F[A] = e.raiseError
    def handleWith[A](fa: Algebra[F, A])(f: Error => Algebra[F, A]): Algebra[F, A] =
      new Algebra[F, A]:
        def fetch: F[A] =
          fa.fetch.handleErrorWith {
            case e: Error => f(e).fetch
            case e        => e.raiseError
          }

  // val x = summon[SemigroupK[Algebra[IO, *]]]

  // given algebraSemigroupK: [F[_]: Monad, E: Semigroup] => Handle[F, E] => SemigroupK[[X] =>> Algebra[F, X]]:
  //   def combineK[A](x: Algebra[F, A], y: Algebra[F, A]): Algebra[F, A] =
  //     new Algebra[F, A]:
  //       def fetch: F[A] =
  //         x.fetch <+> y.fetch

  def pure[F[_]: Applicative, A](a: A): Algebra[F, A] =
    new:
      def fetch: F[A] = a.pure[F]

  def apply[F[_]: Monad, A](f: F[Either[Error, A]])(using Raise[F, Error]): Algebra[F, A] =
    new:
      def fetch: F[A] =
        f.flatMap(Raise[F, Error].fromEither)

  def raise[F[_], E, A](error: E)(using Raise[F, E]): Algebra[F, A] =
    new:
      def fetch: F[A] = error.raise

  // def liftF[F[_], A](fa: F[A]): Algebra[F, A] =
  //   new:
  //     def fetch: F[A] = fa

  type Token = String
  def local[F[_]: Concurrent]: Algebra[F, Token] = raise(Error("Local Not implemented"))
  def env[F[_]: Concurrent]: Algebra[F, Token] = raise(Error("Env Not implemented"))
  def hardcoded[F[_]: Applicative]: Algebra[F, Token] = pure("token")

object App extends IOApp.Simple:
  import HandleSemigroupK.given
  import Algebra.{*, given}

  def run: IO[Unit] =
    (local[IO] <+> env[IO]).fetch.
    attempt.flatMap {
      case Right(token) => IO.println(s"Token: $token")
      case Left(error)   => IO.println(s"Error: $error") // println
    } >>
    (local[IO] <+> hardcoded[IO]).fetch
      .map(_.some)
      .handleWith[Error](e => IO.println(s"failed to fetch token: $e").as(none[Token]))
      .flatMap (s => IO.println(s"Token: $s") )
