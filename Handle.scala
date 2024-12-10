//> using scala 3.5.2
//> using dep org.typelevel::cats-mtl::1.5.0
//> using dep org.typelevel::cats-effect::3.5.5

import cats.*
import cats.effect.*
import cats.mtl.*
import scala.annotation.*

def begin[E]: Begin[E] = ???

class Begin[E]:
  def apply[F[_], A](f: Handle[F, E] ?=> F[A])(using ApplicativeThrow[F]): Body[F, E, A] = ???

class Body[F[_], E, A]:
  def rescue(f: E => F[A]): F[A] = ???

enum MyError:
  case Derp, Fart

def foo(using Raise[IO, MyError]): IO[Unit] = ???
def bar(using Handle[IO, MyError]): IO[Unit] = ???

object Main extends IOApp.Simple:
  def run =
    begin[MyError]:
      foo *> bar
    .rescue:
      case MyError.Derp => ???
      case MyError.Fart => ???
