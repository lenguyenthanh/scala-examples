//> using scala 3.3.0-RC6
//> using toolkit typelevel:latest

import cats.effect.{ IO, IOApp }
import fs2.*
import fs2.io.file.{ Files, Path }
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.client.Client
import org.http4s.*
import org.http4s.implicits.*

object Http4sDownloader extends IOApp.Simple:

  lazy val request = Request[IO](
    method = Method.GET,
    uri = uri"https://database.lichess.org/lichess_db_puzzle.csv.zst"
  )

  def writeFile(s: fs2.Stream[IO, Byte]) =
    s.through(Files[IO].writeAll(Path("lichess_db_puzzle.csv.zst")))

  def download(client: Client[IO]) =
    client
      .stream(request)
      .switchMap(_.body)
      .through(Files[IO].writeAll(Path("lichess_db_puzzle.csv.zst")))
      .compile
      .drain

  val run: IO[Unit] = EmberClientBuilder
    .default[IO]
    .build
    .use(download)
