//> using scala 3.5.0-RC3
// //> using options -source:3.6-migration
//> using repository https://raw.githubusercontent.com/lichess-org/lila-maven/master
//> using dep org.lichess::scalalib-core:11.2.3
//> using dep org.reactivemongo::reactivemongo-bson-api:1.1.0-RC12

import reactivemongo.api.bson.*
import scalalib.newtypes.*
import scala.util.{ Failure, NotGiven, Success, Try }

opaque type RelayRoundId = String
object RelayRoundId extends OpaqueString[RelayRoundId]

trait NoDbHandler[A]

given BSONHandler[String] = ???
given [T: BSONHandler]: BSONHandler[List[T]] = ???

given opaqueWriter[T, A](using
    rs: SameRuntime[T, A],
    writer: BSONWriter[A]
)(using NotGiven[NoDbHandler[T]]): BSONWriter[T] with
  def writeTry(t: T) = writer.writeTry(rs(t))

def write[A](a: A)(using writer: BSONWriter[A]): Unit = ???

val ids: List[RelayRoundId] = ???

val x = write(ids)
