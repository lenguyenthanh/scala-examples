//> using scala 3.5.1
//> using options -Xprint:typer

trait A:
  def apply(f: String): Int = ???

object A:
  extension (a: Int) def apply: String = ???

opaque type X = Int
val x: X = X(3)
// trait B[T, U] extends A[T, U]:
//   extension (a: T) def apply: U = ???

// trait E[T, U] extends A[T, U]:
//   extension (a: T) def apply: U = ???

// opaque type B = String => Unit
// object B extends E[B, String => Unit]
//
// object C:
//   def apply(msg: String): Unit = ???
//   def send: B = B(apply)
