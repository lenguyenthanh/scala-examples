//> using scala "3.7.3"
import scala.quoted.*

object Lut {

  def macroImpl(i: Expr[Int])(using q: Quotes): Expr[Double] = {
    i match {
      case Expr(ii) => Expr(math.sin(ii / 100.0))
      case _        => q.reflect.report.errorAndAbort("nope")
    }
  }
  inline def apply(inline i: Int): Double = ${ macroImpl('i) }
}

