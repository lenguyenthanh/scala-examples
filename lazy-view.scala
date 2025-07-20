//> using scala 3.nightly


import scala.collection.View

private class Wrapper[K, V](underlying: Map[K, Option[V]]) extends Map[K, V] {

  override def isEmpty: Boolean = lazyFlattenedUnderlying.isEmpty
  override def knownSize: Int = lazyFlattenedUnderlying.knownSize
  override def size: Int = flattenedUnderlying.size

  override def keySet: Set[K] =
    flattenedUnderlying.keySet

  override def removed(key: K): Map[K, V] =
    flattenedUnderlying.removed(key)

  override def updated[V1 >: V](key: K, value: V1): Map[K, V1] =
    flattenedUnderlying.updated(key, value)

  override def get(key: K): Option[V] =
    underlying.get(key).flatten

  override def contains(key: K): Boolean =
    get(key).isDefined

  override def apply(key: K): V =
    get(key).getOrElse(default(key))

  override def iterator: Iterator[(K, V)] =
    lazyFlattenedUnderlying.iterator

  private lazy val flattenedUnderlying: Map[K, V] =
    lazyFlattenedUnderlying.toMap

  private val lazyFlattenedUnderlying: View[(K, V)] = {
    println("Creating lazy view") // For debugging purposes
    underlying.view.filter {
      case (k, Some(v)) =>
        println(s"Accessing key: $k") // For debugging purposes
        true
      case _ =>
        println("Skipping None value") // For debugging purposes
        false
    }
      .map {
        case (k, Some(v)) => (k, v)
        case _ => throw new NoSuchElementException("Unexpected None value in lazy view")
      }
  }

}

object Main {
  def main(args: Array[String]): Unit = {
    // val lazyMap = new Wrapper(Map("a" -> None, "b" -> None, "c" -> None))
    val lazyMap = new Wrapper(Map("a" -> Some(1), "b" -> None, "c" -> Some(3)))

    println("lazyMap.isEmpty======")
    println(lazyMap.isEmpty) // Should print false

    println("lazyMap.size======")
    println(lazyMap.size) // Should print 2

    println("lazyMap.get(a)======")
    println(lazyMap.get("a")) // Should print 1

    println("lazyMap.get(b)======")
    println(lazyMap.get("b")) // Should print None

    println("lazyMap.get(c)======")
    println(lazyMap.get("c")) // Should print Some(3)

    println("lazyMap.keySet======")
    println(lazyMap.keySet) // Should print Set(a, c)
  }
}
