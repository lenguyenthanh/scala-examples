//> using scala 3.nightly
//> using dep org.typelevel::cats-mtl:1.5.0-93-e9c0d37-SNAPSHOT
//> using dep org.typelevel::cats-core:2.13.0
//> using options -explain
// //> using options -Xprint:typer

import cats.*
import cats.data.EitherT
import cats.mtl.Handle.*
import cats.mtl.{Handle, Raise}
import cats.mtl.syntax.all.*
import cats.syntax.all.*

type Error = Error.type
object Error

type F[A] = EitherT[Id, Throwable, A]

def f1[F[_]: Monad](using Raise[F, Error]): F[String] =
  "hello".pure[F] *> Error.raise[F, String].as("nope")

// def f2[F[_]: Monad]: Raise[F, Error] ?=> F[String] =
//   "hello".pure[F] *> Error.raise[F, String].as("nope")
//
// def test1: F[String] =
//   allow[Error]:
//     f1[F]
//   .rescue:
//     case Error => "error".pure[F]
//
// def test2 =
//   allowF[F, Error] { implicit handle =>
//     f1[F]
//   }.rescue:
//     case Error => "error".pure[F]
//
// def test3 =
//   test1 *> test2
//
// todo better error message for allow without rescue

// def test4: F[String] =
def test4[F[_]: MonadThrow](using Handle[F, Error]): F[String] =
  allow[Error]:
    f1[F]
  .rescue:
    case Error => "rescue error".pure[F]

// @main def main(): Unit = {
//   println(test4)
//   // println(test1.value)
//   // println(test2.value)
//   // println(test3.value)
// }
