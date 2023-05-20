//> using scala 3.3.0-RC6
//> using toolkit typelevel:latest
//> using dep de.lhns::fs2-compress-zstd:0.5.0
//> using repository https://raw.githubusercontent.com/lichess-org/lila-maven/master
//> using dep org.lichess::scalachess:15.2.6

import cats.syntax.all.*
import cats.effect.*
import fs2.*
import fs2.data.csv.*
import fs2.data.csv.generic.semiauto.*
import fs2.io.file.{ Files, Path }
import de.lhns.fs2.compress.ZstdDecompressor

import cats.data.ValidatedNel
import cats.syntax.all.*
import com.monovore.decline.*
import com.monovore.decline.effect.*

import chess.*
import chess.MoveOrDrop.*
import chess.format.*
import chess.format.pgn.{ Move as PgnMove, * }
import chess.variant.Standard

object Main
    extends CommandIOApp(
      name = "lichess-puzzles-cli",
      header = "Convert lichess puzzles to pgn",
      version = "0.0.1"
    ):

  import Puzzle.given

  override def main: Opts[IO[ExitCode]] = CLI.parse
    .map(convert(_).compile.drain.as(ExitCode.Success))

  val defaultChunkSize = 1024 * 4

  def convert(config: Config) =
    Files[IO]
      .readAll(Path("lichess_db_puzzle.csv.zst"))
      .through(ZstdDecompressor[IO](defaultChunkSize).decompress)
      .through(text.utf8.decode)
      .through(decodeWithoutHeaders[Puzzle]())
      .filter(_.filter(config))
      .evalTap(x => IO.println(s"${x.id} ${x.rating} ${x.popularity}"))
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
      sit   <- Fen.readWithMoveNumber(fen).toRight(RuntimeException(s"Invalid fen: $fen with id: $id"))
      moves <- Puzzle.validateMoves(sit.situation, moves)
      tree = Node.buildWithIndex(moves, (m, i) => PgnMove(sit.ply + i, m.toSanStr))
      tags = Tags(List(Tag("Fen", fen.value), Tag("Site", gameUrl), Tag("Source", s"https://lichess.org/training/$id")))
      pgn  = Pgn(tags, Initial.empty, tree)
    yield pgn

  def filter(config: Config) =
    config.minRating.forall(rating >= _) &&
      config.maxRating.forall(rating <= _) &&
      config.minPlays.forall(nbPlays >= _) &&
      config.maxPlays.forall(nbPlays <= _) &&
      config.minPopularity.forall(popularity >= _) &&
      config.maxPopularity.forall(popularity <= _)

case class Config(
    minRating: Option[Int],
    maxRating: Option[Int],
    minPlays: Option[Int],
    maxPlays: Option[Int],
    minPopularity: Option[Int],
    maxPopularity: Option[Int]
)

object Puzzle:
  given CellDecoder[EpdFen]          = CellDecoder[String].map(EpdFen(_))
  given CellDecoder[List[String]]    = CellDecoder[String].map(_.split(' ').toList)
  given ucis: CellDecoder[List[Uci]] = CellDecoder[String].emap(Uci.readList(_).liftTo(DecoderError("Invalid ucis")))
  given RowDecoder[Puzzle]           = deriveRowDecoder

  def validateMoves(sit: Situation, ucis: List[Uci]) = ucis
    .foldM((List.empty[MoveOrDrop], sit))(validateMove)
    .map(_._1.reverse)

  def validateMove(p: (List[MoveOrDrop], Situation), uci: Uci) =
    uci(p._2).toEither
      .bimap(e => RuntimeException(s"Invalid move $uci"), move => (move :: p._1) -> move.situationAfter)

object CLI:

  private val minRatingOpt = Opts
    .option[Int]("minRating", "Min rating")
    .orNone

  private val maxRatingOpt = Opts
    .option[Int]("maxRating", "Max rating")
    .orNone

  private val minPlayOpt = Opts
    .option[Int]("minPlays", "Min number of plays")
    .orNone

  private val maxPlayOpt = Opts
    .option[Int]("maxPlays", "Max number of plays")
    .orNone

  private val minPopularityOpt = Opts
    .option[Int]("minPopularity", "Min popularity")
    .orNone

  private val maxPopularityOpt = Opts
    .option[Int]("maxPopularity", "Max popularity")
    .orNone

  val parse: Opts[Config] =
    (minRatingOpt, maxRatingOpt, minPlayOpt, maxPlayOpt, minPopularityOpt, maxPopularityOpt).mapN(Config.apply)
