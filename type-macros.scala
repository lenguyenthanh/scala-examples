//> using scala 3.7.0
//> using options -Xprint:postInlining

import scala.util.NotGiven
import deriving.Mirror
import compiletime.*

case class CaseClass(name: String)
class RegularClass(name: String)
enum MyEnum:
  case One
  case Two

type MyTuple = (i: Int, n: String)
trait MyTrait

@main def hello: Unit =

  println("Hello, World!")
  bus[CaseClass]
  // bus(RegularClass("RegularClass"))
  bus[MyEnum.One.type]
  bus[MyEnum]
  // bus(new MyTrait {})
  // bus((1, "Tuple"))

def bus[A](using NotGiven[A <:< Tuple], Mirror.Of[A]): Unit =
  val mirror = summon[Mirror.Of[A]]
  mirror match
    case m: Mirror.ProductOf[A] =>
      println("Product: " + m)
      // println("Product: " + m.fromProduct(Tuple.fromProductTyped(a)))
    case m: Mirror.SumOf[A] =>
      println("Sum: " + m)
      // println("Sum: " + m.ordinal(a))
    case _ =>
      println(s"Not a product or sum: $mirror")
