//> using scala 3.5.0-RC4

// objective
// be lawful be correct be typesafe be categorical correct
// easy to create new variant

type Bitboard = Long
type ByColor[A] = A
type ByRole[A] = A
enum Color:
  case White, Black
type Move

// Move representation
sealed trait Movable:
  def apply(position: Position[A]): Either[String, Position]

case class State(
    turn: Color,
    castles: Castles = Castles.all,
    unmovedRooks: UnmovedRooks,
)


// describe what you can see on the chess board
case class Board(
    occupied: Bitboard,
    byColor: ByColor[Bitboard],
    byRole: ByRole[Bitboard]
)

case class Position(
  board: Board,
  variant: Variant,
  state: State
  )

// Set of all valid positions create a tree (not graph because history)

/*
case class Move(
    piece: Piece,
    orig: Square,
    dest: Square,
    situationBefore: Situation,
    after: Board,
    capture: Option[Square],
    promotion: Option[PromotableRole],
    castle: Option[Move.Castle],
    enpassant: Boolean,
    metrics: MoveMetrics = MoveMetrics.empty
):
*/

case class Position[Variant](
  board: Board,
  variant: Variant,
  state: State
  )

// Standard
// chess960
// Crazyhouse
// Bughouse
// Horde
// Atomic
trait Variant:
  type A
  def name: String
  def state: A
  def startPosition: Position[Variant]


// PGN => X => Game
// Fen => X => Position
