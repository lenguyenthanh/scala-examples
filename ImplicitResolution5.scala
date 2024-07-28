// //> using scala 3.5.0-RC4
// //> using scala 3.4.2
//> using scala 3.3.3
//> using options -Xprint:typer
// //> using options -source:3.6-migration

// https://github.com/scala/scala3/issues/8092
// https://github.com/scala/scala3/discussions/17809


object x:
  given Int = 1

object z:
  import x.given

  given Int = 0
  def main(args: Array[String]): Unit =
    val y = summon[Int]
    println(y)
