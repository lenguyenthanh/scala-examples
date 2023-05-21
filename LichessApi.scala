//> using scala 3.3.0-RC6
//> using toolkit typelevel:latest

import cats.effect.{IO, IOApp}
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.client.Client
import org.http4s.*
import org.http4s.implicits.*
import org.http4s.headers.*

// http4s for Lichess api
// this is isomorphic to the following curl command:
// curl -X POST "https://lichess.org/api/games/export/_ids" -H "accept: application/x-ndjson" -H "Content-Type: text/plain" -d "TJxUmbWK,4OtIh2oh,ILwozzRZ"

object LichessApi extends IOApp.Simple:

  def printResponse(client: Client[IO]): IO[Unit] =
    client
      .expect[String](postRequest)
      .flatMap(IO.println)

  val run: IO[Unit] = EmberClientBuilder
    .default[IO]
    .build
    .use(printResponse)

  lazy val postRequest = Request[IO](
    method = Method.POST,
    uri = uri"https://lichess.org/api/games/export/_ids",
    headers = Headers(
      Accept(ndJson)
    )
  ).withEntity(
  "TJxUmbWK,4OtIh2oh,ILwozzRZ"
  )

  val ndJson = MediaType("application", "x-ndjson", true, false, List("ndjson"))
  val pgn = MediaType("application", "x-chess-pgn", true, false, List("pgn"))
