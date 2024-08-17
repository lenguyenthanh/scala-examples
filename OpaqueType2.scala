//> using scala 3.4.2

package opaque

object Main:
  opaque type Bar = Int
  object Bar:
    def apply(i: Int): Bar = i

opaque type Foo = Int
object Foo:
  def apply(i: Int): Foo = i

val x: Foo = Foo(42)
val y = x.value

extension (f: Foo)
  def value: Int = f

object t:
  val x = Foo(42).value
