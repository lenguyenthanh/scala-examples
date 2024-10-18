//> using scala 3.5.1
//> using repository https://raw.githubusercontent.com/lichess-org/lila-maven/master
//> using dep org.lichess::scalalib-core:11.2.13

import scalalib.newtypes.*

@main def main =
  import Bar.Z
  val y = Bar(42)
  val y1 = Bar(43)
  val y2 = y + y1

  val z1 = Z(42)
  val z2 = Z(43)
  val z3 = z1 + z2
  val z4 = x(z1)
  Z.raw(z1)

def x(z: Bar.Z): Int =
  z.value

object Bar:
  opaque type Bar = Int
  final inline def apply(i: Int): Bar = i
  extension (inline b: Bar)
    final inline def value: Int = b
    final inline def +(other: Bar): Bar = b + other

  type Z = Z.Z
  object Z extends OpaqueInt[Z.Z]:
    opaque type Z = Int
