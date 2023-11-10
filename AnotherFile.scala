//> using scala 3.3.0

package example

object Consume:
  val y = PrivateObject.x

@main def Test =
  println(Consume.y)
