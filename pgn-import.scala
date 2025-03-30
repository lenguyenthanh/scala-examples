//> using scala 3.nightly
//> using repository "https://raw.githubusercontent.com/lichess-org/lila-maven/master"
//> using dep org.lichess::scalachess:17.3.0

import chess.format.pgn.*

val pgn = "1. e4 Nf6 2. e5 d5 3. exd6 cxd6"
@main def main =
  println(Parser.full(PgnStr(pgn)).map(_.toPgn))
