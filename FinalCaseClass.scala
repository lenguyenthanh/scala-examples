//> using scala 3.3.1

package example

object Main:
  val b1 = ByColorG(Bitboard(1))
  val b2 = ByColor(Bitboard(1))

  def main(args: Array[String]): Unit =
    println(b1)
    println(b2)

case class ByColorG[T](x: T)
case class ByColor(x: Bitboard)


opaque type Bitboard = Long
object Bitboard:
  def apply(x: Long): Bitboard = x

object Board:
  final case class FinalX(x: Int)
  case class NonFinalO(x: Int)
  private case class PrivateC(x: Int)

final case class FinalO(x: Int)
case class NonFinalO(x: Int)
private case class PrivateO(x: Int)

