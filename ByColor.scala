//> using scala 3.5.2
// //> using options -Xprint:typer

import scala.util.NotGiven

enum Color:
  case White, Black

case class ByColor[A](white: A, black: A)

object ByColor:
  import Color.*
  def apply[A](f: Color => A)(using NotGiven[f.type <:< PartialFunction[Color, A]]): ByColor[A] = ByColor(white = f(White), black = f(Black))


// def f(f: Int => Int)(using NotGiven[f.type <:< PartialFunction[?, ?]]): Int = ???
// f(Map.empty) // not compiled
// f(_ => 1) // compiled

@main def ByColorMain: Unit =
  val x: ByColor[Map[Int, Int]] = ByColor(_ => Map.empty)
  // val x: ByColor[Map[Int, Int]] = ByColor(_ => Map.empty)
  println(x)
