//> using scala "3.3.0-RC5"

import scala.quoted.* // imports Quotes, Expr

def inspectCode(x: Expr[Any])(using Quotes): Expr[Any] =
  println(x.show)
  x

inline def inspect(inline x: Any): Any = ${ inspectCode('x) }

@main def hello: Unit =

  val eitherList: List[Either[String, Int]] = List(Right(1), Left("a"), Right(2))

  // if all values in the list are Right, return a list of the values
  // otherwise return the first Left


  inspect(println("Hello"))
