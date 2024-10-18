//> using scala 3.5.1
//> using options -Wsafe-init, -feature

package chess

import scala.concurrent.duration.*

@main def main =
  val fst = Centis(10)
  val snd = Centis(20)
  println("hello")
  // println(fst.atMost(snd))

// maximum centis = Int.MaxValue / 100 / 60 / 60 / 24 = 248 days
type Centis = Centis.Centis
object Centis:
  opaque type Centis = Int
  extension (c: Centis)

    inline def centis: Int = c

    inline def *(inline o: Int): Centis = c * o

    def roundTenths: Int  = (if c > 0 then c + 5 else c - 4) / 10
    def roundSeconds: Int = Math.round(c * 0.01f)

    inline def toSeconds: BigDecimal = java.math.BigDecimal.valueOf(c, 2)
    inline def millis: Long          = c * 10L
    def toDuration: FiniteDuration   = FiniteDuration(millis, MILLISECONDS)

    def *~(scalar: Float): Centis   = Centis.ofFloat(scalar * c)
    def /(div: Int): Option[Centis] = Option.when(div != 0)(c / div)

    def avg(other: Centis): Centis = (c + other) >> 1

    inline def nonNeg: Centis = Math.max(c, 0)

    inline def unary_- : Centis                   = -c
    inline infix def >(o: Centis): Boolean  = >(o)
    inline infix def <(o: Centis): Boolean  = <(o)
    inline infix def >=(o: Centis): Boolean = >=(o)
    inline infix def <=(o: Centis): Boolean = <=(o)
    inline infix def +(o: Centis): Centis   = c + o
    inline infix def -(o: Centis): Centis   = c - o
    inline def atLeast(bot: Centis): Centis = Math.max(c, bot) //java.lang.Math.max(c, bot)
    inline def atMost(inline top: Centis): Centis  = Math.min(c, top)
  end extension

  def ofLong(l: Long): Centis =
    try Math.toIntExact(l)
    catch
      case _: ArithmeticException =>
        if l > 0 then Integer.MAX_VALUE
        else Integer.MIN_VALUE

  inline def apply(inline i: Int): Centis = i

  def apply(d: FiniteDuration): Centis =
    ofMillis:
      if d.unit eq MILLISECONDS then d.length
      else d.toMillis

  inline def ofFloat(f: Float): Centis   = Math.round(f)
  inline def ofDouble(d: Double): Centis = ofLong(Math.round(d))

  inline def ofSeconds(s: Int): Centis = 100 * s
  inline def ofMillis(l: Long): Centis = ofLong(if l > 0 then l + 5 else l - 4) / 10

