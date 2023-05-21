//> using scala 3.2.2
//> using toolkit typelevel:latest
//> using dep "org.tpolecat::skunk-core:0.6.0-RC2"
//> using dep "org.typelevel::otel4s-java:0.2.1"

import cats.effect._
import skunk._
import skunk.implicits._
import skunk.codec.all._
import org.typelevel.otel4s.trace.Tracer
import natchez.Trace.Implicits.noop

object Hello extends IOApp {

  val session: Resource[IO, Session[IO]] =
    Session.single(                                          // (2)
      host     = "localhost",
      port     = 5432,
      user     = "jimmy",
      database = "world",
      password = Some("banana")
    )

  def run(args: List[String]): IO[ExitCode] =
    session.use { s =>                                       // (3)
      for {
        d <- s.unique(sql"select current_date".query(date))  // (4)
        _ <- IO.println(s"The current date is $d.")
      } yield ExitCode.Success
    }

}
