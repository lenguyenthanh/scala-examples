//> using scala 3.7.0
// //> using options -Wunused:all -explain-cyclic
// //> using options -rewrite -source:3.7-migration -indent
//> using dep org.scalameta::munit::1.1.1

object Main:

  def >>(using String): String = summon[String]
  def hh(implicit str: String): String = summon[String]

  hh:
    "hello world"

  // extension (x: Int)
  //   def foo: Int = x + 1
  //   def y = foo + 3

  // def main(args: Array[String]): Unit =
  //   val x = 42
  //   println(x)


// case class Foo(x: Int)
// class BarTests extends munit.FunSuite:
//
//   extension (foo: Foo)
//     def z: Int = foo.x + 1
//     private def y: Int = z + 3
//
//   test("bar"):
//     val y = Foo(42)
//     // val x = 42
//     y.y
//     assert(y.z == 43)
//
