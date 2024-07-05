//> using scala 3.5.0-RC3
// //> using options -source:3.6-migration
//> using dep org.reactivemongo::reactivemongo-bson-api:1.1.0-RC12

import reactivemongo.api.bson.*

trait SameRuntime[A, B]

opaque type Id = String
object Id:
  given SameRuntime[Id, String] = ???

trait NoDbHandler[A]

given BSONHandler[String] = ???
given [T: BSONHandler]: BSONHandler[List[T]] = ???

given opaqueWriter[T, A](using
    rs: SameRuntime[T, A],
    writer: BSONWriter[A]
): BSONWriter[T] = ???

def write[A](a: A)(using writer: BSONWriter[A]): Unit = ???

val ids: List[Id] = ???

val x = write(ids)
