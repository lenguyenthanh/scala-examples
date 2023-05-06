//> using scala "3.3.0-RC3"

import scala.concurrent.duration._

@main def main =
  val s = -1.seconds
  println(s <= 0.seconds)
