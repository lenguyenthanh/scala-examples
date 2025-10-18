//> using scala "3.7.3"
//> using dep org.scala-lang.modules::scala-collection-compat:2.13.0

import scala.collection.compat._

def foo[F[c] <: Iterable[c], A, B](fa: F[A])(f: PartialFunction[A, B])(implicit
    fac: Factory[B, F[B]]
): F[B] = fa.collect(f).to(fac)

val l: List[String] = foo(List(Option("str"))) { case Some(a) => a }

println(l)

