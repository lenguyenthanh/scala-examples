//> using scala 3.5.1
//> using options -Ysafe-init, -feature

package chess

import scala.concurrent.duration.*

@main def main =
  val fst = Centis(10)
  val snd = Centis(20)
  println(fst.atMost(snd))
