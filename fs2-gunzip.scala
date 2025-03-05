//> using scala 3.6.3
//> using toolkit typelevel:default
// //> using dep de.lhns::fs2-compress-zip4j:2.3.0
//> using dep de.lhns::fs2-compress-gzip:2.3.0

import cats.effect.{ IO, IOApp }
import fs2.compression.Compression.*

import java.io.FileInputStream

object App extends IOApp.Simple:
  def run: IO[Unit] =
    fs2.io
      .readInputStream(
        IO(new FileInputStream("foobar.gz")),
        1024 * 32
      )
      // .through(Compression[IO].gunzip(1024 * 8))
      // .flatMap(_.content)
      .through(Decompressor.decompress)
      // .evalTap(IO.println)
      .through(fs2.text.utf8.decode)
      // .evalTap(IO.println)
      .through(fs2.text.lines)
      .evalTap(IO.println)
      .compile
      .drain

object Decompressor:

  import de.lhns.fs2.compress.*
  val defaultChunkSize = 1024 * 32

  def decompress: fs2.Pipe[IO, Byte, Byte] =
      _.through(GzipDecompressor.make[IO](defaultChunkSize).decompress)
    // _.through(ArchiveSingleFileDecompressor(GzipDecompressor.make[IO](defaultChunkSize)).decompress)
