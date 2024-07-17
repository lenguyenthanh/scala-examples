//> using scala 3.5.0-RC3

import scala.language.experimental.namedTuples

object x:

  opaque type Castle = (king: (from: Int, to: Int), rook: (from: Int, to: Int))

  object Castle:
    def apply(kingFrom: Int, kingTo: Int, rookFrom: Int, rookTo: Int): Castle =
      (king = (from = kingFrom, to = kingTo), rook = (from = rookFrom, to = rookTo))

  case class C(kingFrom: Int, kingTo: Int, rookFrom: Int, rookTo: Int)
