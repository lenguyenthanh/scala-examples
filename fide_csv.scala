//> using scala 3.6.3
//> using toolkit typelevel:default

import cats.syntax.all.*
import cats.effect.*
import fs2.*
import fs2.data.csv.*
import fs2.data.csv.generic.semiauto.*
import fs2.io.file.{ Files, Path }


object Main extends IOApp.Simple:
  def run: IO[Unit] =
    IO.println("Hello, World!")

  val path = Path("""/Users/tle/Downloads/FIDE-main/Step 2 - Reformat""")

  val blitz = path / "Blitz"
  val rapid = path / "Rapid"
  val standard = path / "Standard"

  // list all files in a directory (ex: blitz)
  // read csv and transform to a more standard format

  // def convert =
  //   Files[IO]
  //     .readAll(path)
  //     .through(text.utf8.decode)
  //     .through(decodeSkippingHeaders[Puzzle]()) //???
  //     .take(20)
  //     .evalTap(IO.println)
  //     .compile
  //     .drain
