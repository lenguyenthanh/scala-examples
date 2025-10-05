//> using scala 3.7.3
//> using dep "org.typelevel::cats-core:2.13.0"

import cats.data.Ior
import cats.syntax.all.*

  val left: Ior[Int, String]  = Ior.Both(1, "foo")
  val right: Ior[Int, String] = Ior.Right("foo")
  val both: Ior[Int, String]  = Ior.Both(1, "bar")
  val xs = List(right, both, left)

@main def runIor =
  val r1 = xs.traverse(identity)
  println(r1)

