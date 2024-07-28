trait BackendOps2[F[_], V]:
  def empty: F[V]
  extension (x: F[V])
    inline def get(key: String): Option[V]
    inline def put(key: String, value: V): Unit


given [A]: BackendOps2[List, A] with
  def empty = List.empty
  extension (x: List[A])
    inline def get(key: String): Option[A] = x.headOption
    inline def put(key: String, value: A): Unit = x.updated(0, value)

given [A]: BackendOps2[[X] =>> Map[String, X], A] with
  def empty = Map.empty
  extension (x: Map[String, A])
    inline def get(key: String): Option[A] = x.get(key)
    inline def put(key: String, value: A): Unit = x.updated(key, value)
