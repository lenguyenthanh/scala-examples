//> using scala "3.3.1"
//> using dep "org.typelevel::toolkit:latest.release"
//> using repository "https://raw.githubusercontent.com/lichess-org/lila-maven/master"
//> using dep "org.lichess::scalachess:15.6.8"
//> using dep "dev.optics::monocle-core:3.2.0"
//> using dep "dev.optics::monocle-macro:3.2.0"

import cats.syntax.all.*
import cats.effect.*
import monocle.syntax.all.*

import chess.*

object Hello extends IOApp.Simple:
  def run =
    IO.println("Hello World!")

object Patterns:

  // object PatternAt:
  //   def findAll(sit: Situation): List[Pattern] =
  //     List.empty

    // def findAll(sits: List[Situation]): List[Pattern] =
    //   sits.flatMap(findAll)


  trait PatternSearcher[F[_], C, P]:
    def search(c: C): F[List[P]]

  object Puzzles:
    enum Pattern:
      case fork
      case doubleCheck
      case xRay

    def apply[F[_]](): PatternSearcher[F, Situation, Pattern] =
      new PatternSearcher[F, Situation, Pattern]:
        def search(sit: Situation): F[List[Pattern]] =
          ???


  object Rosen:
    enum Pattern:
      case fork
      case doubleCheck
      case xRay

    def apply[F[_]](): PatternSearcher[F, Situation, Pattern] =
      new PatternSearcher[F, Situation, Pattern]:
        def search(sit: Situation): F[List[Pattern]] =
          ???
