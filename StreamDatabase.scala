//> using scala 3.3.1
//> using toolkit typelevel:latest
//> using dep de.lhns::fs2-compress-zstd:0.5.0
//> using repository https://raw.githubusercontent.com/lichess-org/lila-maven/master
//> using dep org.lichess::scalachess:15.2.8

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

val uri    = uri"https://database.lichess.org/standard/lichess_db_standard_rated_2023-05.pgn.zst"
val total  = 1_000_000L
val output = "games.csv"

object StreamDatabase extends IOApp.Simple:

  import Games.*
  lazy val request = Request[IO](
    method = Method.GET,
    uri = uri
  )

  def execute(client: Client[IO]) =
    client
      .stream(request)
      .switchMap(_.body)
      .through(Decompressor.decompress)
      .through(text.utf8.decode)
      .through(fs2.text.lines)
      // .evalTap(IO.println)
      .through(PgnDecoder.decode)
      .evalTap(x => if x.isLeft then IO.println(x) else IO.unit)
      .collect:
        case Right(x) => x
      .through(Games.transform)
      .through(encodeWithoutHeaders[Games.Csv](fullRows = true))
      .take(total)
      .through(text.utf8.encode)
      .through(Files[IO].writeAll(Path(output)))
      .compile
      .drain

  val run: IO[Unit] = EmberClientBuilder
    .default[IO]
    .build
    .use(execute)

object Games:
  import fs2.data.csv.*
  import fs2.data.csv.generic.semiauto.*

  val transform: Pipe[IO, ParsedPgn, Csv] =
    _.map(_.toCsv)
      .collect:
        case Some(x) => x

  enum Termination(val id: Int, val name: String):
    case ClockFlag   extends Termination(0, "Clock flag")
    case Disconnect  extends Termination(1, "Disconnect")
    case Resignation extends Termination(2, "Resignation")
    case Checkmate   extends Termination(3, "Checkmate")
    case Unknown     extends Termination(4, "Checkmate")

  object Termination:
    def fromString(str: String): Termination = str match
      case "Clock flag"  => ClockFlag
      case "Disconnect"  => Disconnect
      case "Resignation" => Resignation
      case "Checkmate"   => Checkmate
      case _             => Unknown

  case class Player(name: String, rating: Int, ratingDiff: Int)
  case class Game(
      id: String,
      white: Player,
      black: Player,
      winner: Color,
      playedAt: String,
      clock: String,
      totalMoves: Int,
      termination: Termination
  ):
    def toCsv: Csv =
      Csv(
        id,
        white.name,
        white.rating,
        white.ratingDiff,
        black.name,
        black.rating,
        black.ratingDiff,
        winner.toInt,
        playedAt,
        clock,
        totalMoves,
        termination.id
      )

  extension (c: Color)
    def toInt = c match
      case White => 0
      case Black => 1

  case class Csv(
      id: String,
      whiteName: String,
      whiteRating: Int,
      whiteRatingDiff: Int,
      blackName: String,
      blackRating: Int,
      blackRatingDiff: Int,
      winner: Int,
      playedAt: String,
      clock: String,
      totalMoves: Int,
      termination: Int
  )

  extension (pgn: ParsedPgn)
    def toCsv: Option[Csv] =
      pgn.toGame.map(_.toCsv)

    def toGame: Option[Game] =
      for
        id             <- pgn.tags("Site").headOption
        playedAt       <- pgn.tags("Date")
        white          <- pgn.tags("White").headOption
        black          <- pgn.tags("Black").headOption
        whiteElo       <- pgn.tags("WhiteElo").headOption
        blackElo       <- pgn.tags("BlackElo").headOption
        whiteDiff      <- pgn.tags("WhiteRatingDiff").headOption
        blackDiff      <- pgn.tags("BlackRatingDiff").headOption
        clock          <- pgn.tags("TimeControl").headOption
        totalMoves     <- pgn.tree.map(_.size)
        winner         <- pgn.tags.outcome.map(_.winner).flatten
        terminationStr <- pgn.tags("Termination").headOption
        termination = Termination.fromString(terminationStr)
      yield Game(
        id,
        Player(white, whiteElo.toInt, whiteDiff.toInt),
        Player(black, blackElo.toInt, blackDiff.toInt),
        winner,
        playedAt,
        clock,
        totalMoves.toInt,
        termination
      )

  given RowEncoder[Csv] = deriveRowEncoder

object Decompressor:
  val defaultChunkSize = 1024 * 4

  def decompress: Pipe[IO, Byte, Byte] =
    _.through(ZstdDecompressor[IO](defaultChunkSize).decompress)

object PgnDecoder:
  import chess.format.pgn.*

  val decodeToPgnStr: Pipe[IO, String, String] =
    def go(s: Stream[IO, String], p: Option[PartialPgnState]): Pull[IO, String, Unit] =
      s.pull.uncons1.flatMap:
        case Some((line, tl)) =>
          p match
            case None =>
              if line.isEmpty then go(tl, None)
              else if line.isTag then go(tl, Some(Tags(line)))
              else Pull.raiseError[IO](RuntimeException(s"Pgn has to start with tag $line"))
            case Some(partial) =>
              partial.take(line) match
                case Left(err) => Pull.raiseError(RuntimeException(s"Error parsing pgn: $err with state: $partial"))
                case Right(next) =>
                  if next.isInstanceOf[Done] then Pull.output1(next.value) >> go(tl, None)
                  else go(tl, Some(next))
        case None => Pull.done

    in => go(in, None).stream

  val decode: Pipe[IO, String, Either[RuntimeException, ParsedPgn]] =
    _.through(decodeToPgnStr)
      .map(_.parse)

  sealed trait PartialPgnState:
    val value: String
    def take(line: String): Either[Throwable, PartialPgnState]

  case class Tags(value: String) extends PartialPgnState:
    def take(line: String) =
      if line.isTag then Tags(s"$value\n$line").asRight
      else if line.isEmpty then WatingForMoves(s"$value\n").asRight
      else RuntimeException(s"Invalid line: $line").asLeft

  case class WatingForMoves(value: String) extends PartialPgnState:
    def take(line: String) =
      Done(s"$value\n$line").asRight

  case class Done(value: String) extends PartialPgnState:
    def take(line: String) =
      RuntimeException(s"Invalid line: $line").asLeft

  extension (s: String)
    def isTag   = s.startsWith("[")
    def isMoves = s.startsWith("1")
    def parse   = Parser.full(PgnStr(s)).toEither.leftMap(x => RuntimeException(x.value))
