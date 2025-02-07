//> using scala 3.6.2

object chess:

  type Piece = Piece.Piece
  object Piece:
    opaque type Piece = Short

  type Color = Color.Color
  object Color:
    opaque type Color = Boolean

    val White: Color = true
    val Black: Color = false

    val all = List(White, Black)

    inline def fromWhite(inline white: Boolean): Color = if white then White else Black
    extension (c: Color)
      inline def name = if c then "White" else "Black"
      inline def leter = if c then "w" else "b"
      inline def white: Boolean = c
      inline def black: Boolean = !c


object x:
  import chess.Color
  val c: Color = Color.White
  val n: String = c.name
  val l: String = c.leter
