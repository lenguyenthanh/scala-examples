//> using scala 3.7.3
//> using dep "org.typelevel::cats-core:2.13.0"
//> using dep "org.typelevel::cats-mtl:1.6.0"
//> using dep "org.typelevel::cats-effect:3.6.3"
// //> using options -Xprint:typer
//> using options -explain

package submarine.leak

import cats.syntax.all.*
import cats.effect.*
import cats.mtl.{ Handle, Raise }
import cats.mtl.Handle.allow
import cats.mtl.syntax.raise.*
import cats.Monad
import cats.Traverse
import cats.Applicative

object Main extends IOApp.Simple:
  type IORaise[E, A] = Raise[IO, E] ?=> IO[A]

  def run: IO[Unit] =
    IO.println("Submarine Leak")


  def analysis =
    def onComplete =
      if true then "hello".raise
      else IO(12)

    allow:
      if true then 12.raise
      else onComplete
    .rescue:
      case _: String => onComplete
      case res: Int => res.raise
