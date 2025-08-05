//> using scala 3.7.1
//> using dep org.typelevel::cats-effect:3.6.3
//> using dep org.typelevel::cats-mtl:1.5.0-94-d1382bd-20250723T144446Z-SNAPSHOT
//> using dep org.typelevel::cats-core:2.13.0
//> using options -explain
// //> using options -Xprint:typer

import cats.Functor
import cats.data.EitherT
import cats.mtl.*
import cats.mtl.Handle.*
import cats.mtl.syntax.all.*
import cats.syntax.all.*
import cats.effect.IO

object Error

type F[A] = IO[A]

def f[F[_]: Functor]: Raise[F, Error.type] ?=> F[String] =
  Error.raise[F, String].as("nope")

def test1 =
  allow[Error.type]:
    Error.raise[F, String].as("nope")
  .rescue:
    case Error => "error".pure[F]

def login: Raise[IO, Error.type] ?=> IO[String] =
  Error.raise.as("nope")

def login1: IO[Either[Error.type, String]] =
  IO.pure(Left(Error)).as("nope".asRight[Error.type])

def test2: IO[String] =
  allow[Error.type]:
    login
  .rescue:
    case Error => "error".pure[F]
