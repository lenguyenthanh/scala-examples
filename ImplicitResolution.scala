//> using scala 3.5.0-RC2
//> using options -source:3.6-migration

import bson.*

def stringMapHandler[V](using writer: BSONWriter[Map[String, V]]): BSONHandler[Map[String, V]] = ???
def typedMapHandler[K, V: BSONHandler] = stringMapHandler[V]

object bson:

  trait BSONWriter[T]
  trait BSONDocumentWriter[T] extends BSONWriter[T]
  object BSONWriter extends BSONWriterInstances

  trait BSONHandler[T] extends BSONWriter[T]

  private[bson] trait BSONWriterInstances {
    given mapWriter[V](using BSONWriter[V]): BSONDocumentWriter[Map[String, V]] = bson.mapWriter[V]
    export bson.collectionWriter
  }

  final class ¬[A, B]
  object ¬ {
    implicit def defaultEvidence[A, B]: ¬[A, B] = new ¬[A, B]()
    @annotation.implicitAmbiguous("Could not prove type ${A} is not (¬) ${A}")
    implicit def ambiguousEvidence1[A]: ¬[A, A] = null
    implicit def ambiguousEvidence2[A]: ¬[A, A] = null
  }

  private[bson] trait DefaultBSONHandlers extends LowPriorityHandlers
  private[bson] trait LowPriorityHandlers{
    given collectionWriter[T, Repr <: Iterable[T]](using BSONWriter[T], Repr ¬ Option[T]): BSONWriter[Repr] = ???
    private[bson] def mapWriter[V](implicit valueWriter: BSONWriter[V]): BSONDocumentWriter[Map[String, V]] = ???
  }

  // ---
  object bson extends DefaultBSONHandlers
