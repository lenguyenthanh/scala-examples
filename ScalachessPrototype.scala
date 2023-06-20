//> using scala 3.2.2

@main def main =
  println("hello")

type Situation = Unit

// Uci and San are two different ways to represent a move (maybe an invalid move)
// Or at least a potential move
// So, if we can apply it to a situation, we can get a move

sealed trait Movable:
  def apply(situation: Situation): Either[String, Move]

// not sure we need it yet, but the idea is we can implement this for uci and San
trait FromString[A]:
  def parse(s: String): Either[String, A]

sealed trait Uci extends Movable:

  case class Move1(from: String, to: String) extends Uci:
    def apply(situation: Situation): Either[String, Move] = ???

  case class Drop(piece: String, to: String) extends Uci:
    def apply(situation: Situation): Either[String, Move] = ???

  object Null extends Uci:
    def apply(situation: Situation): Either[String, Move] = ???

object Uci extends FromString[Uci]:
  def parse(s: String): Either[String, Uci] = ???

sealed trait San extends Movable:
  def parse(s: String): Either[String, San] = ???

  case class Standard(from: String, to: String) extends San:
    def apply(situation: Situation): Either[String, Move] = ???

  case class Castle(side: String) extends San:
    def apply(situation: Situation): Either[String, Move] = ???

  case class Drop(piece: String, to: String) extends San:
    def apply(situation: Situation): Either[String, Move] = ???

  object Null extends San:
    def apply(situation: Situation): Either[String, Move] = ???

// A move is a valid transition from one situation to another
// So if we have move we know the current situation and the after situation
trait Move2:
  def f: Situation => Option[Situation]

sealed trait Move:
  val current: Situation
  val after: Situation

  def toUci: Uci
  def toSan: San

  object Normal
  object EnPassant
  object Castles
  object Drop

case class Game(moves: List[Move], ply: Int, clock: Int)

// Rename to Position
case class Situation(
    board: Board,
    history: History,
    variant: Variant,
)

// suggestion rename to Position
// because it present a position of the game
case class Board(
    occupied: Bitboard,
    byColor: ByColor[Bitboard],
    byRole: ByRole[Bitboard]
)


// rename to GameContext
case class History(
    color: Color,
    lastMove: Option[Uci] = None,
    positionHashes: PositionHash = Monoid[PositionHash].empty,
    castles: Castles = Castles.all,
    unmovedRooks: UnmovedRooks,
    halfMoveClock: HalfMoveClock = HalfMoveClock.initial
    checkCount: CheckCount = CheckCount(0, 0),
    crazyData: Option[Crazyhouse.Data] = None,
)

