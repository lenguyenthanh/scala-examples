//> using scala 3.3.4
//> using repository https://raw.githubusercontent.com/lichess-org/lila-maven/master
//> using dep com.github.ornicar::scalalib:9.5.5
//> using options -Ysafe-init, -feature

package opaque

import ornicar.scalalib.newtypes.*
import Foo.*
import LimitMinutes.*

object Main:
  def main(args: Array[String]) =
    val f: Foo = Foo(42)
    val f1 = f.addF(1)
    val ff: Foo = Foo(42)
    val ff1 = ff.addF(1)
    val l: LimitMinutes = LimitMinutes(42)
    val l1 = l.addL(1)
    val ll: LimitMinutes = LimitMinutes(43)
    val ll1 = ll.addL(1)
    val ll2 = ll + 2
    println(s"Hello world! $f1 $l1")

opaque type Foo = Int
object Foo:
  def apply(i: Int): Foo = i
  extension (f: Foo)
    def addF(a: Int): Foo = f + a

opaque type LimitMinutes = Int
object LimitMinutes extends OpaqueInt[LimitMinutes]:
  extension (l: LimitMinutes)
    def addL(a: Int): LimitMinutes = l + a
