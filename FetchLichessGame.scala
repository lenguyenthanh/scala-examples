//> using scala 3.3.0
//> using toolkit typelevel:latest
//> using dep "org.http4s::http4s-dsl:0.23.19"
//> using dep io.circe::circe-generic:0.14.5
//> using dep com.outr::scribe-cats:3.11.5
//> using dep com.outr::scribe-slf4j:3.11.5
//> using dep org.gnieh::fs2-data-json:1.7.1
//> using dep org.gnieh::fs2-data-json-circe:1.7.1

import cats.syntax.all.*
import cats.MonadThrow
import io.circe.generic.auto.*
import fs2.{Pipe, Stream}
import cats.effect.{ IO, IOApp, Temporal }
import cats.syntax.all.*
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.client.Client
import org.http4s.*
import org.http4s.headers.*
import org.http4s.implicits.*
import scala.util.control.NoStackTrace
import scala.concurrent.duration.*
import fs2.data.json._

import fs2.data.json.*
import fs2.data.json.circe.*
import scribe.cats.*
import scribe.Scribe

// Stream the response body as a string, then parse it as ndjson
// This is the same as:
// curl -X POST "https://lichess.org/api/games/export/_ids" -H "accept: application/x-ndjson" -H "Content-Type: text/plain" -d "TJxUmbWK,4OtIh2oh,ILwozzRZ"

object FetchLichessGame extends IOApp.Simple:

  val gameIds = List("TJxUmbWK","4OtIh2oh","ILwozzRZ")

  val run: IO[Unit] =
      EmberClientBuilder.default[IO].build.map(Games.make)
      .use(_.fetch(gameIds).evalTap(IO.println).compile.drain)

trait Games[F[_]]:
  def fetch(ids: List[GameId]): Stream[F, Game]

object Games:

  case class GameError(message: String) extends NoStackTrace

  def make[F[_]: Temporal, MonadThrow](client: Client[F]): Games[F] = new:

    override def fetch(ids: List[GameId]): Stream[F, Game] = client
      .stream(createRequest(ids))
      .through(handle429)
      .flatMap(_.bodyText)
      .through(tokens[F, String])
      .through(codec.deserialize[F, Game])

    def createRequest(ids: List[GameId]) = Request[F](
      method = Method.POST,
      uri = uri"https://lichess.org/api/games/export/_ids",
      headers = Headers(Accept(ndJson)),
    ).withEntity(ids.mkString(","))

    def handle429(x: Stream[F, Response[F]]) = ((x.map: response =>
      if response.status == Status.TooManyRequests then
        none
      else if response.status.isSuccess then
        response.some
      else throw GameError(s"Unexpected status code: ${response.status}")
        ) ++ Stream.sleep(1.minute).as(none[Response[F]])).repeat.collectFirst{ case (Some(response)) => response }

  private val ndJson = MediaType("application", "x-ndjson", true, false, List("ndjson"))

type GameId = String

case class Clock(
    initial: Int,
    increment: Int,
    totalTime: Int
)

case class Players(
    white: Player,
    black: Player
)

case class Game(
    id: String,
    rated: Boolean,
    variant: String,
    speed: String,
    perf: String,
    createdAt: Long,
    lastMoveAt: Long,
    status: String,
    players: Players,
    winner: String,
    moves: String,
    tournament: String,
    clock: Clock
)

case class User(
    name: String,
    id: String
)

case class Player(
    user: User,
    rating: Int
)
