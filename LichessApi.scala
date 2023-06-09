//> using scala 3.3.0
//> using toolkit typelevel:latest
//> using dep io.circe::circe-generic:0.14.5
//> using dep org.gnieh::fs2-data-json:1.7.1
//> using dep org.gnieh::fs2-data-json-circe:1.7.1

import io.circe.generic.auto.*
import fs2.*
import cats.effect.{ IO, IOApp }
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.client.Client
import org.http4s.*
import org.http4s.headers.*
import org.http4s.implicits.*
import fs2.data.json.*
import fs2.data.json.circe.*

// Stream the response body as a string, then parse it as ndjson
// This is the same as:
// curl -X POST "https://lichess.org/api/games/export/_ids" -H "accept: application/x-ndjson" -H "Content-Type: text/plain" -d "TJxUmbWK,4OtIh2oh,ILwozzRZ"

object LichessApi extends IOApp.Simple:

  def printResponse(client: Client[IO]): IO[Unit] =
    client
      .run(postRequest)
      .use(x => parse(x.bodyText).evalTap(IO.println).compile.drain)

  val run: IO[Unit] = EmberClientBuilder
    .default[IO]
    .build
    .use(printResponse)

  def parse(s: Stream[IO, String]) =
    s.through(tokens[IO, String])
     .through(codec.deserialize[IO, Game])

  lazy val postRequest = Request[IO](
    method = Method.POST,
    uri = uri"https://lichess.org/api/games/export/_ids&opening=true",
    headers = Headers(Accept(ndJson))
  ).withEntity(
    "TJxUmbWK,4OtIh2oh,ILwozzRZ"
  )

  val ndJson = MediaType("application", "x-ndjson", true, false, List("ndjson"))
  val pgn    = MediaType("application", "x-chess-pgn", true, false, List("pgn"))

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
