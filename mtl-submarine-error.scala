//> using scala 3.7.1
//> using dep org.typelevel::cats-mtl:1.5.0-94-d1382bd-20250723T144446Z-SNAPSHOT
//> using dep org.typelevel::cats-core:2.13.0
//> using options -explain
// //> using options -Xprint:typer

import cats.*
import cats.data.EitherT
import cats.mtl.Handle.*
import cats.mtl.{ Handle, Raise }
import cats.mtl.syntax.all.*
import cats.syntax.all.*
import cats.mtl.InnerWired

type Error = Error.type
object Error

// type F[A] = EitherT[Id, Throwable, A]
type F[A] = EitherT[Eval, Throwable, A]

def test: F[String] =
  allow[Error.type]:
    Error.raise[F, String].as("nope")
//   .rescue:
//     case Error => "error".pure[F]
//
// def f1[F[_]: Monad](using Raise[F, Error]): F[String] =
//   "hello".pure[F] *> Error.raise[F, String].as("nope")
//
// // def f2[F[_]: Monad]: Raise[F, Error] ?=> F[String] =
// //   "hello".pure[F] *> Error.raise[F, String].as("nope")
// //
//
// def test1 =
//   allow[Error]:
//     f1[F]
//   // .rescue:
//   //   case Error => "error".pure[F]
//
// def test2 =
//   allowF[F, Error] { implicit handle =>
//     f1[F]
//   }.rescue:
//     case Error => "error".pure[F]
//
// // def test3 =
// //   test1 *> test2
//
// // def handle1(using Handle[F, Error]): F[String] =
// //     case Error => "error".pure[F]
//
// def handleError[F[_]: Applicative]: Error => F[String] = _ => "error".pure[F]
//
// def test4 =
//   allow[Error]:
//     f1[F]
//   .rescue:
//     handleError[F]
//
// // todo better error message for allow without rescue
//
// // def test4: F[String] =
// def test4[F[_]: MonadThrow](using Handle[F, Error]): F[String] =
//   allow[Error]:
//     f1[F]
//   .rescue:
//     case Error => "error".pure[F]
//
// object Union:
//   type Error = Int | String
//   def f1[F[_]: Monad](using Raise[F, Int]): F[String] =
//     "hello".pure[F] *> 2.raise[F, String].as("nope")
//   def f2[F[_]: Monad](using Raise[F, String]): F[String] =
//     "hello".pure[F] *> "error".raise[F, String]
//
//   def test[F[_]: MonadThrow]: F[String] =
//     allow[Error]:
//       f1[F] *> f2[F]
//     .rescue:
//       case e: Int    => s"int error: $e".pure[F]
//       case e: String => s"string error: $e".pure[F]
