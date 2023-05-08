//> using scala "3.3.0-RC5"
//> using dep "org.typelevel::toolkit:latest.release"
//> using repository "https://raw.githubusercontent.com/lichess-org/lila-maven/master"
//> using dep "org.lichess::scalachess:15.2.0"
//> using dep "dev.optics::monocle-core:3.2.0"
//> using dep "dev.optics::monocle-macro:3.2.0"
//> using dep "de.lhns::fs2-compress-zstd::0.4.1"

import cats.syntax.all.*
import cats.effect.*
import fs2.*
import fs2.data.csv.*
import fs2.data.csv.generic.semiauto.*
import fs2.io.file.{Files, Path}
import de.lhns.fs2.compress.ZstdDecompressor

import chess.*
import chess.format.*

object Hello extends IOApp.Simple:
  import Puzzle.given

  val defaultChunkSize = 1024 * 4

  def run =
      Files[IO].readAll(Path("lichess_db_puzzle.csv.zst"))
      .through(ZstdDecompressor[IO](defaultChunkSize).decompress)
      .through(text.utf8.decode)
      .through(decodeWithoutHeaders[Puzzle]())
      .take(10)
      .foreach(IO.println)
      .compile
      .drain

case class Puzzle(
  id: String,
  fen: EpdFen,
  moves: List[String],
  rating: Int,
  ratingDeviation: Int,
  popularity: Int,
  nbPlays: Int,
  themes: List[String],
  gameUrl: String,
  openingTags: List[String]
)

object Puzzle:
  given CellDecoder[List[String]] = CellDecoder[String].map(_.split(" ").toList)
  given CellDecoder[EpdFen] = CellDecoder[String].map(EpdFen(_))
  given CellDecoder[Uci] = CellDecoder[String].map(Uci(_).get)
  given RowDecoder[Puzzle]    = deriveRowDecoder
