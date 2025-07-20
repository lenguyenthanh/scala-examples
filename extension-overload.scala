//> using scala 3.7
//> using options -Xprint:typer

import scala.annotation.targetName

extension (long: Long)
  // def contains(i: Int): Boolean = ???
  // def contains(l: Long): Boolean = ???
  @targetName("containsSquare")
  def contains(s: Square.Square): Boolean = ???

object Square:
  opaque type Square = Int
  inline def apply(inline i: Int): Square = i
  extension (s: Square)
    def f(l: Long): Boolean = l.contains(s)

  // extension (i: Int)
  //   def f(l: Long): Boolean = l.contains(i)
