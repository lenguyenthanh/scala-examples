//> using scala 3.3.0-RC6
//> using toolkit typelevel::latest
//> using repository https://raw.githubusercontent.com/lichess-org/lila-maven/master
//> using dep org.lichess::scalachess:15.2.6
//> using dep de.lhns::fs2-compress-zstd::0.5.0

import scala.util.control.NoStackTrace
import cats.syntax.all.*
import cats.effect.*
import fs2.*
import fs2.data.csv.*
import fs2.data.csv.generic.semiauto.*
import fs2.io.file.{ Files, Path }
import de.lhns.fs2.compress.ZstdDecompressor

import chess.*
import chess.MoveOrDrop.*
import chess.format.*
import chess.format.pgn.{ Move as PgnMove, * }
import chess.variant.Standard

object Main extends IOApp.Simple:
  import Puzzle.given

  def run: IO[Unit] = converter.compile.drain

  val defaultChunkSize = 1024 * 4

  def converter =
    Files[IO]
      .readAll(Path("lichess_db_puzzle.csv.zst"))
      .through(ZstdDecompressor[IO](defaultChunkSize).decompress)
      .through(text.utf8.decode)
      .through(decodeWithoutHeaders[Puzzle]())
      .map(_.toPgn)
      .rethrow
      .map(_.render.value)
      .intersperse("\n\n")
      .through(text.utf8.encode)
      .through(Files[IO].writeAll(Path("puzzles.pgn")))

case class Puzzle(
    id: String,
    fen: EpdFen,
    moves: List[Uci],
    rating: Int,
    ratingDeviation: Int,
    popularity: Int,
    nbPlays: Int,
    themes: List[String],
    gameUrl: String,
    openingTags: List[String]
):
  def toPgn =
    for
      sit   <- Fen.readWithMoveNumber(fen).toRight(Puzzle.Error.InvalidFen(s"Invalid fen: $fen with id: $id"))
      moves <- Puzzle.validateMoves(sit.situation)(moves)
      tree = Node.buildWithIndex(moves, (m, i) => PgnMove(sit.ply + i, m.toSanStr))
      tags = Tags(List(Tag("Fen", fen.value), Tag("Site", gameUrl)))
      pgn  = Pgn(tags, Initial.empty, tree)
    yield pgn

object Puzzle:
  given CellDecoder[EpdFen]       = CellDecoder[String].map(EpdFen(_))
  given CellDecoder[List[String]] = CellDecoder[String].map(_.split(" ").toList)
  given ucis: CellDecoder[List[Uci]] =
    CellDecoder[String].emap(Uci.readList(_).liftTo[DecoderResult](DecoderError("Invalid uci")))
  given RowDecoder[Puzzle] = deriveRowDecoder

  def validateMoves(sit: Situation)(ucis: List[Uci]) = ucis
    .foldM((List.empty[MoveOrDrop], sit))(validateMove)
    .map(_._1.reverse)

  def validateMove(p: (List[MoveOrDrop], Situation), uci: Uci) =
    uci(p._2).toEither
      .bimap(e => Error.InvalidUci(s"Invalid move $uci"), move => (move :: p._1) -> move.situationAfter)

  enum Error(message: String) extends NoStackTrace:
    case InvalidFen(message: String) extends Error(message)
    case InvalidUci(message: String) extends Error(message)
