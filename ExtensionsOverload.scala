//> using scala 3.nightly

import scala.language.experimental.relaxedExtensionImports

object A:
  extension (s: String)
    def wow: Unit = println(s)
object B:
  extension (i: Int)
    def wow: Unit = println(i)

import A.*
import B.*

@main def Test =
  5.wow
  "five".wow
