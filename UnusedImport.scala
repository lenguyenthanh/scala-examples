//> using scala "3.3.0-RC5"
//> using options "-Wunused:all"

case class Foo(x: Int)

val foo = Foo(1)

@main def main =
  import foo.{ x as y }
  println(y)
