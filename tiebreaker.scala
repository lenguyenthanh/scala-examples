//> using scala 3.7.1
//> using repository jitpack
//> using dep com.github.lichess-org.scalachess::scalachess:17.8.5
//> using dep com.github.lichess-org.scalachess::scalachess-rating:17.8.5

import chess.rating.Elo

// example of how to use it in lila
object Lila:
  trait RelayPlayer:
    def tournament: Tournament[RelayPlayer]
    def tiebreakers: List[TieBreaker]
    def computeTiebreakers: List[PlayerWithScore[RelayPlayer]] =
      tournament.compute(tiebreakers)

  given Player[RelayPlayer] with
    extension (player: RelayPlayer) def id: PlayerId = ???
    def elo: Option[Elo]                             = ???

type PlayerId = String

trait Player[A]:
  extension (player: A) def id: PlayerId
  def elo: Option[Elo]

case class PlayerWithScore[A: Player](
    player: A,
    score: Float,
    tiebreakers: List[TieBreaker.Point]
)

trait Games[A: Player]:
  def pointsById(id: PlayerId): Option[Float] = ???

import TieBreaker.*
trait Tournament[A: Player]:
  def players: List[A]
  def games: Games[A]
  def currentRound: Int
  def totalRounds: Int
  def byes: (PlayerId, Int) => Boolean // playerId, round => true if player has a bye in that round

  given Ordering[TieBreaker.Point]       = ???
  given Ordering[List[TieBreaker.Point]] = ???

  // compute and sort players by their scores and tiebreakers
  def compute(tiebreakers: List[TieBreaker]): List[PlayerWithScore[A]] =
    val points = tiebreakers.foldLeft(Map.empty[PlayerId, List[Point]]): (acc, tiebreaker) =>
      tiebreaker.compute(this, acc)
    val playersWithScores = players.map: player =>
      val score = games.pointsById(player.id).getOrElse(0f)
      PlayerWithScore(player, score, points.getOrElse(player.id, Nil))
    playersWithScores
      .sortBy(p => (p.score, p.tiebreakers))

trait TieBreaker:
  // compute players' tiebreak points based on the tournament and a list of previously computed tiebreak points
  def compute[A: Player](tour: Tournament[A], previousPoints: PlayerPoints): PlayerPoints
  def tiebreakType: TiebreakType

enum TiebreakType:
  case Elo
  case Points
  case OpponentScore

object TieBreaker:
  case class Point(tiebreaker: TieBreaker, value: Float)
  type PlayerPoints = Map[PlayerId, List[Point]]
