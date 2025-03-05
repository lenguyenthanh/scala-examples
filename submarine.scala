//> using scala 3.nightly
//> using dep org.typelevel::cats-mtl::1.5.0
//> using dep org.typelevel::cats-effect::3.6.0-RC2
// //> using options -Xprint:typer

import cats.*
import cats.syntax.all.*
import cats.mtl.implicits.*
import cats.effect.*
import cats.mtl.*
import cats.data.*

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

  // def test2[F[_]: ApplicativeThrow] =
  type F[A] = EitherT[Eval, Throwable, A]
  // type F[A] = Either[Throwable, A]
  def test2 =
    begin[Error1]:
      begin[Error2]:
        Error1.Third.raise[F, String].as("nope")
      .rescue:
        case Error2.Fourth => Applicative[F].pure("Fourth2")
    .rescue:
      case Error1.First => Applicative[F].pure("first1")
      case Error1.Second => Applicative[F].pure("second1")
      case Error1.Third => Applicative[F].pure("third1")

// object Test:
//   enum Error1:
//     case First
//
//   enum Error2:
//     case Second
//
//   def test[F[_]: ApplicativeThrow] =
//     begin[Error1]:
//       // Error1.First.raise.as("nope")
//       begin[Error2]:
//         Error1.First.raise.as("nope")
//       .rescue:
//         case Error2.Second => "second".pure[F]
//     .rescue:
//       case Error1.First => "first1".pure[F]
//
//     // should return first1
//
//

