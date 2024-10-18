//> using scala 3.5.1
// //> using options -Xprint:typer

abstract class  TotalWrapper[Newtype, Impl](using Newtype =:= Impl):
  inline final def apply(inline s: Impl): Newtype            = s.asInstanceOf[Newtype]

abstract class FunctionWrapper[Newtype, Impl](using Newtype =:= Impl) extends TotalWrapper[Newtype, Impl]:
  extension (inline a: Newtype) inline def exec: Impl = a.asInstanceOf[Impl]

opaque type SocketSend = String => Unit
object SocketSend extends FunctionWrapper[SocketSend, String => Unit]

trait ParallelSocketSend:
  def apply(msg: String): Unit
  def send: SocketSend = SocketSend(apply)
  send.exec("Hello, world!")
