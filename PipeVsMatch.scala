//> using scala 3.5.0-RC3


import scala.language.experimental.namedTuple

object x:

  def x: String = ???

  val y = x.pipe(_.length)
  val z = x.match { case a =>
    a.length
  }
