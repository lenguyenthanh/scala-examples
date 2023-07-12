//> using scala 3.3.0
//> using toolkit typelevel:latest

import cats.{ Foldable, Monoid }
import cats.syntax.all.*

object Main:
  def main(args: Array[String]) =
    val a1 = All(true)
    val a2 = All(false)
    println(s"Hello world!${a1 |+| a2}")

opaque type All = Boolean
object All:
  def apply(getAll: Boolean): All = getAll
  def unapply(arg: All): Boolean  = arg

  given Monoid[All] = new:
    val empty: All                   = All(true)
    def combine(x: All, y: All): All = All(x && y)
