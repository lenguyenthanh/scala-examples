//> using scala 3.5.0-RC4
//> using options -Xprint:typer
//> using options -source:3.6-migration

trait SameRuntime[A, B]
trait BSONWriter[T]
trait BSONHandler[T] extends BSONWriter[T]

given BSONHandler[String]                    = ???

opaque type Id = String
object Id:
  given SameRuntime[Id, String] = ???

object x:

  // given (using SameRuntime[Id, String], BSONWriter[String]): BSONWriter[Id] = ???
  // given (using SameRuntime[Id, String]): BSONWriter[Id] = ???
  given BSONWriter[Id] = ???

object Handlers:

  // given [T, A](using SameRuntime[T, A], BSONWriter[A]): BSONWriter[T] = ???
  // given (using SameRuntime[Id, String], BSONWriter[String]): BSONWriter[Id] = ???
  given BSONWriter[Id] = ???

object o:
  import Handlers.given
  import x.given

  val y = summon[BSONWriter[Id]]

