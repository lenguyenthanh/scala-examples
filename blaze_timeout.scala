//> using scala 3.nightly
//> using lib "org.http4s::http4s-blaze-server::0.23.17"
//> using lib "org.http4s::http4s-dsl::0.23.30"

//> using options -Xkind-projector -language:higherKinds

import cats.syntax.all._
import cats.effect._
import com.comcast.ip4s._
import org.http4s._
import org.http4s.dsl._
import org.http4s.server._
import org.http4s.blaze.server._
import org.http4s.server.middleware._

import scala.concurrent.duration._

trait Logger[F[_]] {
  def warn(message: String): F[Unit]
}
object Logger {
  def apply[F[_]](using logger: Logger[F]): Logger[F] = logger
}

object IssueApp extends IOApp with Http4sDsl[IO] {

  given Logger[IO] = new Logger[IO] {
    def warn(message: String): IO[Unit] = IO(println(s"WARNING: $message"))
  }

  override def run(args: List[String]): IO[ExitCode] =
    blazeServerResource.use(_ => IO.never.as(ExitCode.Success))

  val routes: HttpRoutes[IO] =
    HttpRoutes.of[IO] { case GET -> Root / "timeout" =>
      IO.sleep(10.seconds) *> Ok("ok")
    }
  val maxConnections = server.defaults.MaxConnections * 100
  def blazeServerResource: Resource[IO, Server] =
    BlazeServerBuilder[IO]
      .bindHttp(9999, "0.0.0.0")
      .withHttpApp(TimeoutMiddleware[IO](1.seconds)(routes).orNotFound)
      .withResponseHeaderTimeout(Duration.Inf)
      .withMaxConnections(maxConnections)
      .resource

}


object TimeoutMiddleware {

  import cats.Functor
  import cats.data.{Kleisli, OptionT}
  import org.http4s.*

  def timeoutResponse[F[_]: Functor: Logger](req: Request[F]): F[Response[F]] =
    Logger[F].warn(s"Request timed out: ${req.method} ${req.uri}").as(Response.timeout[F])

  def apply[F[_]: Temporal: Logger](timeout: FiniteDuration): HttpRoutes[F] => HttpRoutes[F] = routes =>
    Kleisli {
      (req: Request[F]) =>
        routes.mapF(Temporal[OptionT[F, *]].timeoutTo(_, timeout, OptionT.liftF(timeoutResponse(req))))(req)
    }
}
