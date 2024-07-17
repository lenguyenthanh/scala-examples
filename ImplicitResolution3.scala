//> using scala 3.5.0-RC3
//> using options -source:3.6-migration
//> using options -Xprint:typer

trait A[T]

given A[String] = ???
given [T: A, U: A]: A[(T, U)] = ???
given [T: A]: A[Iterable[T]] = ???
given [T: A]: A[List[T]] = ???
// given m[T: A]: A[Map[String, T]] = ???
given collectionWriter[T, Repr <: Iterable[T]](using A[T]): A[Repr] = ???
// val x = summon[A[Map[String, String]]]
// val y = summon[A[Iterable[(String, String)]]]
val z = summon[A[List[String]]]
