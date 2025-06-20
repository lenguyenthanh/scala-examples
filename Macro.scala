//> using scala 3.7.0

import scala.quoted.* // imports Quotes, Expr

def inspectCode(x: Expr[Any])(using Quotes): Expr[Any] =
  println(x.show)
  x

inline def inspect(inline x: Any): Any = ${ inspectCode('x) }

