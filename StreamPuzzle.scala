//> using scala 3.3.0-RC6
//> using toolkit typelevel:latest
//> using dep de.lhns::fs2-compress-zstd:0.5.0
//> using repository https://raw.githubusercontent.com/lichess-org/lila-maven/master
//> using dep org.lichess::scalachess:15.2.7

import cats.syntax.all.*
import cats.effect.*
import fs2.data.csv.*
import fs2.data.csv.generic.semiauto.*
import fs2.io.file.{ Files, Path }
import de.lhns.fs2.compress.ZstdDecompressor

import cats.effect.{ IO, IOApp }
import fs2.*
import fs2.io.file.{ Files, Path }
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.client.Client
import org.http4s.*
import org.http4s.implicits.*

import chess.*
import chess.MoveOrDrop.*
import chess.format.*
import chess.format.pgn.{ Move as PgnMove, * }
import chess.variant.Standard

object PuzzleStream extends IOApp.Simple:

  lazy val request = Request[IO](
    method = Method.GET,
    uri = uri"https://database.lichess.org/lichess_db_puzzle.csv.zst"
  )

  def download(client: Client[IO]) =
    client
      .stream(request)
      .switchMap(_.body)
      .through(convert)
      .compile
      .drain

  val run: IO[Unit] = EmberClientBuilder
    .default[IO]
    .build
    .use(download)

  val defaultChunkSize = 1024 * 4

  def convert: Pipe[IO, Byte, Nothing] =
      _.through(ZstdDecompressor[IO](defaultChunkSize).decompress)
      .through(text.utf8.decode)
      .through(decodeSkippingHeaders[Puzzle]())
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
