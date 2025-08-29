//> using scala 3.3.4

// https://docs.scala-lang.org/scala3/book/types-dependent-function.html
// https://docs.scala-lang.org/scala3/reference/new-types/type-lambdas-spec.html
// https://docs.scala-lang.org/scala3/reference/other-new-features/kind-polymorphism.html
// https://docs.scala-lang.org/scala3/reference/new-types/polymorphic-function-types.html
// check cats-effect polling system design for inspiration
// https://typelevel.org/blog/2015/07/13/type-members-parameters.html
// https://typelevel.org/blog/2016/03/13/information-hiding.html
@main def main =
  println("hello")

// Uci and San are two different ways to represent a move (maybe an invalid move)
// Or at least a potential move
// So, if we can apply it to a situation, we can get a move

// Move representation
sealed trait Movable:
  def apply(situation: Situation): Either[String, Move]

// not sure we need it yet, but the idea is we can implement this for uci and San
trait FromString[A]:
  def parse(s: String): Either[String, A]

sealed trait Uci extends Movable:

  def x: Int => Int = ???

  case class Standard(from: String, to: String) extends Uci:
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
  def current: Situation
  def after: Situation

  // toUci(current) == after.right
  def toUci: Uci
  // toSan(current) == after.right
  def toSan: San

  object Normal
  object EnPassant
  object Castle
  object Drop


def parse(xs: List[Moveable]): Either[String, List[Move]] = ???

case class Game(moves: List[Move], ply: Int, clock: Int)

// Rename to Position
case class Situation(
    board: Board,
    history: History,
    variant: Variant,
)

// what about Situation[Variant] ?

// suggestion rename to Position
// because it present a position of the game
case class Board(
    occupied: Bitboard,
    byColor: ByColor[Bitboard],
    byRole: ByRole[Bitboard]
)

// rename to GameContext or GameState
case class History(
    color: Color,
    lastMove: Option[Uci] = None,
    positionHashes: PositionHash = Monoid[PositionHash].empty,
    castles: Castles = Castles.all,
    unmovedRooks: UnmovedRooks,
)

trait Variant:
  def legalMoves(situation: Situation): List[Move]

trait Fen[A]:
  def parse(fen: A): Either[String, Situation]
