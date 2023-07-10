//> using scala 3.3.0
//> using toolkit typelevel:latest
//> using dep "org.typelevel::alleycats-core:2.9.0"
//> using repository https://raw.githubusercontent.com/lichess-org/lila-maven/master
//> using dep com.github.ornicar::scalalib:9.5.5

import ornicar.scalalib.newtypes.*

opaque type File = Int
object File extends OpaqueInt[File]:
  extension (a: File) inline def index: Int = a

  inline def offset(delta: Int): Option[File] =
    if (-8 < delta && delta < 8) atIndex(a + delta)
    else None

  inline def char: Char = (97 + a).toChar
