//> using scala 3.4.0
//> using toolkit typelevel:0.1.23
//> using repository https://raw.githubusercontent.com/lichess-org/lila-maven/master
//> using dep org.lichess::scalachess:15.8.1

import cats.syntax.all.*
import cats.effect.{ IO, IOApp }
import chess.format.Uci
import chess.*
import chess.format.Fen
import chess.variant.Variant
import chess.MoveOrDrop.*

object Converter extends IOApp.Simple:

  // val ucisString =
  //   "e2e4 c7c5 c2c4 g7g6 g1f3 f8g7 d2d3 d7d6 b1c3 b8c6 c1e3 e7e6 d1c2 c6b4 c2d2 h7h5 d3d4 c5d4 e3d4 e6e5 d4e3 g7f8 a2a3 b4a6 a1d1 c8d7 d2c1 g8f6 c1b1 d7e6 b2b4 e6g4 c4c5 a6c7 c5d6 g4f3 g2f3 c7a6 f1a6 b7a6 e3c5 a8b8 e1h1 f6d7 b1c2 d8g5" // c1g5

  // val ucisString = "e2e3 e7e5 c2c3 d7d6 f1c4 c8e6 c4b5 e6d7 d1b3 g8f6 b5c4 b7b6 g1f3 d8e7 b1a3 b8c6 c4b5 c6a5 b3c2 c7c6 b5a6 a8b8 b2b4 a5b7 e1h1 d7f5 c2f5" // e6f5

  val ucisString = "e2e4 d7d5 g1f3 b8c6 f1c4 g8f6 d2d4" // e5d4 e1h1 d7d5 e4d5 f6d5 f3d4 c6d4 d1d4 c7c6"

  val run: IO[Unit] =
    val ucis   = ucisString.split(" ").toList.traverse(Uci.apply).get
    val replay = uciToSan(Situation(chess.variant.Standard), ucis)
    IO.println(replay.map(_.mkString(" ")))

def uciToSan(sit: Situation, moves: List[Uci]) =
  moves
    .foldM(sit -> Nil): (x, move) =>
      move(x._1).map(md => Situation(md.finalizeAfter, !x._1.color) -> (md.toSanStr :: x._2))
    .map(_._2.reverse)
