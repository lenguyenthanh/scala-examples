//> using scala 3.4.2

trait KVStore[Context[_]]{
  def put(key: String, value: String): Context[Unit]
  def get(key: String)                : Context[Option[String]]
  def delete(key: String)             : Context[Unit]
}

sealed trait KVStoreOp[Output]
object KVStoreOp {
  case class Put(key: String, value: String)  extends KVStoreOp[Unit]
  case class Get(key: String)                 extends KVStoreOp[Option[String]]
  case class Delete(key: String)              extends KVStoreOp[Unit]
}

trait ~>[F[_], G[_]]{
  def apply[A](fa: F[A]): G[A]
}

def asNaturalTransformation[Context[_]](impl: KVStore[Context]) = new (KVStoreOp ~> Context){
  def apply[A](fa: KVStoreOp[A]): Context[A] = fa match {
    case KVStoreOp.Put(key, value) => impl.put(key, value)
    case KVStoreOp.Get(key)        => impl.get(key)
    case KVStoreOp.Delete(key)     => impl.delete(key)
  }
}

def fromNaturalTransformation[Context[_]](run: KVStoreOp ~> Context) = new KVStore[Context]{
  def put(key: String, value: String) = run(KVStoreOp.Put(key, value))
  def get(key: String)                = run(KVStoreOp.Get(key))
  def delete(key: String)             = run(KVStoreOp.Delete(key))
}
