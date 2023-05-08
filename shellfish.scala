//> using scala "3.2.2"
//> using dep "org.typelevel::toolkit:latest.release"
//> using dep "io.chrisdavenport::shellfish:0.0.0+16-600b939e+20230508-2051-SNAPSHOT"

import cats.effect._
import cats.syntax.all._

import io.chrisdavenport.shellfish._
object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    import Shell.io._
    val p = SubProcess.io
    for {
      _ <- cd("..")
      h <- home
      _ <- echo(h)
      _ <- touch(s"${h}/test.txt")
      e <- exists(s"${h}/test.txt")
      _ <- echo(e)
      got <- ls.compile.toList
      _ <- echo(got.toString)
    } yield ExitCode.Success
  }

}

