//> using scala 3.3.0-RC6
//> using toolkit typelevel::latest
//> using dep de.lhns::fs2-compress-zstd::0.5.0
//> using repository https://raw.githubusercontent.com/lichess-org/lila-maven/master
//> using dep org.lichess::scalachess:15.2.6

import cats.syntax.all.*
import cats.effect.*
import fs2.*
import fs2.io.file.{ Files, Path }
import de.lhns.fs2.compress.ZstdDecompressor

import chess.format.pgn.*

object Hello extends IOApp.Simple:

  val defaultChunkSize = 1024 * 4

  def run: IO[Unit] = process.compile.drain

  def process =
    Files[IO]
      .readAll(Path("lichess_db_standard_rated_2013-01.pgn.zst"))
      .through(ZstdDecompressor[IO](defaultChunkSize).decompress)
      .through(fs2.text.utf8.decode)
      .through(fs2.text.lines)
      .take(36)
      .through(pgnPipe)
      .map(_.parse)
      .rethrow
      .evalTap(IO.println)

  def pgnPipe: Pipe[IO, String, String] =
    def go(s: Stream[IO, String], p: Option[PartialPgnState]): Pull[IO, String, Unit] = {
      s.pull.uncons1.flatMap {
        case Some((line, tl)) =>
          p match
            case None =>
              if line.isEmpty then go(tl, None)
              else if line.isTag then go(tl, Some(Tags(line)))
              else Pull.raiseError[IO](RuntimeException(s"Pgn has to start with tag $line"))
            case Some(partial) =>
              partial.take(line) match
                case Left(err) => Pull.raiseError(new Exception(err))
                case Right(next) =>
                  if next.isInstanceOf[Done] then Pull.output1(next.value) >> go(tl, None)
                  else go(tl, Some(next))
        case None => Pull.done
      }
    }
    in => go(in, None).stream

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
    if line.isMoves then Done(s"$value\n$line").asRight
    else RuntimeException(s"Invalid line: $line").asLeft

case class Done(value: String) extends PartialPgnState:
  def take(line: String) =
    RuntimeException(s"Invalid line: $line").asLeft

extension (s: String)
  def isTag   = s.startsWith("[")
  def isMoves = s.startsWith("1")
  def parse   = Parser.full(PgnStr(s)).toEither.leftMap(x => RuntimeException(x.value))
