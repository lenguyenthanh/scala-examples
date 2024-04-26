//> using scala "3.4.1"
//> using toolkit typelevel:0.1.25

import cats.syntax.all.*
import fs2.*
import cats.effect.{ IO, IOApp }
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.client.Client
import org.http4s.*
import org.http4s.implicits.*

import cats.effect.{ IO, IOApp }

object FidePlayerListChecker extends IOApp.Simple:

  val downloadUrl = "http://ratings.fide.com/download/players_list.zip"
  val preDateString =
    """<i class="fa   fa-download" style="color: #50618d; padding-right: 10px;"></i> <a href=http://ratings.fide.com/download/players_list.zip class=tur>TXT format</a> <small>("""

  val run: IO[Unit] = EmberClientBuilder
    .default[IO]
    .build
    .use(find)

  lazy val request = Request[IO](
    method = Method.GET,
    uri = uri"https://ratings.fide.com/download_lists.phtml"
  )

  def printResponse(client: Client[IO]): IO[Unit] =
    client
      .run(request)
      .use(x => x.bodyText.compile.string.flatMap(body => IO(println(body))))

  def find(client: Client[IO]): IO[Unit] =
    client
      .run(request)
      .use(extract)
      .flatMap(IO.println)

  def extract(res: Response[IO]): IO[Option[String]] =
    res.bodyText
      .map(_.linesIterator.filter(_.contains(downloadUrl)))
      .filter(_.nonEmpty)
      .map(_.next) // WARNING: This is fine only because this is placed after filter(_.nonEmpty)
      .map(_.drop(preDateString.length).takeWhile(_ != ','))
      .compile
      .last
