//> using scala "3.5.0"
//> using dep "org.typelevel::toolkit:latest.release"
//> using repository "https://raw.githubusercontent.com/lichess-org/lila-maven/master"
//> using dep "org.lichess::scalachess:16.1.0"

import cats.syntax.all.*
import cats.effect.*
import fs2.io.file.{ Files, Path }

import chess.format.pgn.*

object Hello extends IOApp.Simple:
  def run =
    Files[IO]
      .readAll(Path("study.pgn"))
      .through(fs2.text.utf8.decode)
      .through(fs2.text.lines)
      .compile
      .string
      .flatMap: pgn =>
        // IO.println(pgn) >>
        IO.pure(Parser.full(PgnStr(pgn))).flatMap:
          case Left(err) => IO.println(err)
          case Right(parsed) =>
            val pgn2 = parsed.toPgn.toString
            // IO.println(parsed) >>
            IO.println(pgn == pgn2) >>
            IO.println(pgn2) >>
            IO.pure(Parser.full(PgnStr(pgn2))).flatMap:
              case Left(err) => IO.println(err)
              case Right(parsed2) =>
                // IO.println(parsed2) >>
                IO.println(parsed == parsed2)
