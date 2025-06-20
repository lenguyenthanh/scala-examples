//> using scala 3.3.5
//> using dep org.typelevel::cats-mtl:1.5.0-93-e9c0d37-SNAPSHOT
//> using dep org.typelevel::cats-core:2.13.0
// //> using options -explain
// //> using options -Xprint:typer

import cats.*
import cats.data.EitherT
import cats.mtl.Handle.*
import cats.mtl.Raise
import cats.mtl.syntax.all.*
import cats.syntax.all.*

object Error

type F[A] = EitherT[Eval, Throwable, A]

// def test1: F[String] =
//   allow[Error.type]:
//     Error.raise[F, String].as("nope")

  // .rescue:
  //   case Error => "error".pure[F]

def test2 =
  allowF[F, Error.type] { implicit handle =>
    Error.raise[F, String].as("nope")
  }

// def test3 =
//   (test1 *> test2)
//     .rescue:
//       case Error => "error".pure[F]



  // .rescue:
  //   case Error => "error".pure[F]
// def f[F[_]: Functor](using Raise[F, Error.type]): F[String] = Error.raise[F, String].as("nope")

// todo better error message for allow without rescue
// def test2: F[String] =
//   allow[Error.type]:
//     f[F]
//   // .rescue:
//   //   case Error => "error".pure[F]
