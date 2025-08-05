//> using scala 3.7.1
// //> using dep org.typelevel::cats-mtl:1.5.0-93-e9c0d37-SNAPSHOT
// //> using dep org.typelevel::cats-mtl:1.5.0-93-e9c0d37-20250720T133406Z-SNAPSHOT
// //> using dep org.typelevel::cats-mtl:1.5.0-94-11734e7-SNAPSHOT
// //> using dep org.typelevel::cats-mtl:1.5.0-94-d1382bd-20250723T144446Z-SNAPSHOT
// //> using dep org.typelevel::cats-mtl:1.5.0-95-3f7973f-20250723T195030Z-SNAPSHOT
//> using dep org.typelevel::cats-mtl:1.5.0-95-3f7973f-20250723T195855Z-SNAPSHOT
//> using dep org.typelevel::cats-core:2.13.0
//> using options -explain
// //> using options -Xprint:typer

import cats.Eval
import cats.data.EitherT
import cats.mtl.Handle.*
import cats.mtl.syntax.all.*
import cats.syntax.all.*

object Error

type F[A] = EitherT[Eval, Throwable, A]

def test =
  allow[Error.type]:
    Error.raise[F, String].as("nope")
  .rescue:
    case Error => "error".pure[F]
