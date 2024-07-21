// //> using scala 3.5.0-RC4
// //> using scala 3.4.2
//> using scala 3.3.3
//> using options -Xprint:typer
// //> using options -source:3.6-migration

// https://github.com/scala/scala3/issues/8092
// https://github.com/scala/scala3/discussions/17809

import scala.compiletime.*

trait A[T]

object x:

  // given A[String] = ???
  given xa: A[String] = ???

object y:

  given y: A[String] = ???

object z:
  import y.given
  import x.given

  val a = summonInline[A[String]]
  val b = summon[A[String]]
