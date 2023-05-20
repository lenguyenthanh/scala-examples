//> using scala "3.3.0-RC3"
//> using dep "org.typelevel::toolkit::latest.release"
//> using dep "de.lhns::fs2-compress-zstd::0.4.1"

import cats.syntax.all.*
import cats.effect.*
import fs2.*
import fs2.io.file.{ Files, Path }
import de.lhns.fs2.compress.ZstdDecompressor

/** implement a partial function: List[String] => List[Pgn] pars line by line use uncons, TagParsers and MovesParser
  */
object Hello extends IOApp.Simple:
  val stream = Stream.emits(1 to 10)

  def run =
    stream
      .mapAccumulate(0) { case (acc, n) => if predicate(acc + n) then (0, acc + n) else (acc + n, 0) }
      .collect { case (s, x) if x != 0 => x }
      .evalTap(IO.println)
      .compile
      .drain

  def predicate(n: Int): Boolean = n % 3 == 0

  val defaultChunkSize = 1024 * 4
  def run1 =
    for
      c <- Files[IO]
        .readAll(Path("lichess_db_standard_rated_2013-01.pgn.zst"))
        .through(ZstdDecompressor[IO](defaultChunkSize).decompress)
        .through(fs2.text.utf8.decode)
        .take(1)
        .through(fs2.text.lines)
        .zipWithIndex
        .foreach(IO.println)
        // .through(Files[IO].writeAll(Path("2013-01.pgn")))
        .compile
        .count
      _ <- IO.println(s"total lines: $c")
    yield ()
