//> using scala 3.3.1
//> using toolkit typelevel:latest
//> using repository https://raw.githubusercontent.com/lichess-org/lila-maven/master
//> using dep org.lichess::scalachess:15.2.13

import cats.effect.*
import fs2.{Stream, text}
import fs2.data.csv.generic.semiauto.*
import fs2.io.file.{ Files, Path }

import chess.opening.*

object ChessOpenings extends IOApp.Simple:

  val run: IO[Unit] =
    Stream.emits("id, name" +: all)
      .through(Files[IO].writeUtf8Lines(Path("openings.csv")))
      .compile
      .drain

// do openings as tree
def all =
  OpeningDb.all
    .map(x => s"${OpeningKey.fromName(x.name)},${escapeComma(x.name.value)}")

def escapeComma(s: String) = s"\"$s\""
