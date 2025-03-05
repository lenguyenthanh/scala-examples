//> using scala 3.nightly
// //> using dep org.typelevel::cats-mtl::1.5.0
// //> using dep org.typelevel::cats-effect::3.6.0-RC2
// //> using options -Xprint:typer

//> using options -language:higherKinds -Xkind-projector

// import cats.*
// import cats.syntax.all.*
// import cats.mtl.implicits.*
// import cats.effect.*
// import cats.mtl.*
// import cats.data.*

trait Applicative[F[_]]:
  def pure[A](a: A): F[A]
  def map[A, B](fa: F[A])(f: A => B): F[B]
  def ap[A, B](ff: F[A => B])(fa: F[A]): F[B]

  extension [A](fa: F[A])
    def as[B](b: B): F[B] = map(fa)(_ => b)

  extension [A](a: A)
    def x: F[A] = pure(a)

trait ApplicativeThrow[F[_]] extends Applicative[F]:
  def raiseError[A](e: Throwable): F[A]
  def handleErrorWith[A](fa: F[A])(f: Throwable => F[A]): F[A]

trait Handle[F[_], E]:
  def applicative: Applicative[F]
  def raise[E2 <: E, A](e: E2): F[A]
  def handleWith[A](fa: F[A])(f: E => F[A]): F[A]

  // extension [E2 <: E](e: E2)
  //   def raise[F[_], A]: F[A]

trait HandleSyntax {
  implicit def toHandleOps[E](e: E): HandleOps[E] = new HandleOps(e)
}

final class HandleOps[E](val e: E) extends AnyVal {
  def raise[F[_], A](implicit handle: Handle[F, E]): F[A] =
    handle.raise(e)
}

object Handle extends HandleSyntax

  // extension [E](e: E)
  //   def raise[F[_]](using handle: Handle[F, ? >: E]): F[Unit] =
  //     handle.raise(e)

  // def raise[F[_], E, A](e: E)(implicit raise: Handle[F, ? >: E]): F[A] =
  //   raise.raise(e)

def begin[E]: Begin[E] = ???

class Begin[E]:
  def apply[F[_], A](f: Handle[F, E] ?=> F[A])(using ApplicativeThrow[F]): Body[F, A] = ???


  class Body[F[_], A](body: Handle[F, E] ?=> F[A])(using ApplicativeThrow[F]):
    def rescue(f: E => F[A]): F[A] = ???

object Test2:
  enum Error1:
    case First, Second, Third
  enum Error2:
    case Fourth

  import Handle.*

  given ApplicativeThrow[Either[Throwable, *]] = ???
  type F[A] = Either[Throwable, A]

  def test2 =
    val at = summon[ApplicativeThrow[F]]
    begin[Error1]:
      begin[Error2]:
        Error1.Third.raise.as("nope")
      .rescue:
        case Error2.Fourth => "forth".x
    .rescue:
      case Error1.First => "first1".x
      case Error1.Second => "second1".x
      case Error1.Third => "third1".x
