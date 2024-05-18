//> using scala "3.4.1"
//> using toolkit typelevel:0.1.25
//> using dep de.lhns::fs2-compress-zip4j:2.0.0

import cats.effect.{ IO, IOApp }
import cats.syntax.all.*
import org.http4s.*
import org.http4s.client.Client
import org.http4s.implicits.*
import org.http4s.ember.client.EmberClientBuilder

object Main extends IOApp.Simple:
  val downloadUrl = uri"http://ratings.fide.com/download/players_list.zip"
  lazy val request = Request[IO](
    method = Method.GET,
    uri = downloadUrl
  )

  def run =
    EmberClientBuilder
      .default[IO]
      .build
      .use:
        _.stream(request)
          .switchMap(_.body)
          .through(Decompressor.decompress)
          .compile
          .drain

object Decompressor:

  import de.lhns.fs2.compress.*
  import fs2.Pipe
  val defaultChunkSize = 1024 * 4

  def decompress: Pipe[IO, Byte, Byte] =
    _.through(ArchiveSingleFileDecompressor(Zip4JUnarchiver.make[IO](defaultChunkSize)).decompress)
