//> using scala 3.5.0-RC4
//> using options -Xprint:typer

trait SameRuntime[A, B]
trait BSONWriter[T]
trait BSONHandler[T] extends BSONWriter[T]

opaque type Id = String
object Id:
  given SameRuntime[Id, String] = ???

given BSONHandler[String]                    = ???
given [T: BSONHandler]: BSONHandler[List[T]] = ???

given opaqueWriter[T, A](using rs: SameRuntime[T, A], writer: BSONWriter[A]): BSONWriter[T] = ???

val x = summon[BSONWriter[List[Id]]]
