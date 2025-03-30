//> using scala nightly
//> using dep "org.typelevel::toolkit:latest.release"
//> using repository "https://raw.githubusercontent.com/lichess-org/lila-maven/master"
//> using dep "org.lichess::scalachess:14.9.20"
//> using dep "dev.optics::monocle-core:3.2.0"
//> using dep "dev.optics::monocle-macro:3.2.0"

import cats.syntax.all.*
import cats.effect.*
import monocle.syntax.all.*

import chess.format.pgn.*

val game = """
  {This move:} 1.e4! {, was cosidered by R.J.Fischer as "best by test"} 
    ( {This move:} {looks pretty} 1.d4?! {not.} ) 
    ( ;Neither does :
      ;this or that
      {or whatever}
      1.b4?! {this one} )
    1... e5! {, was cosidered by R.J.Fischer as "best by test"} 
    ( {This move:} {looks pretty} 1...d5?! {not.} ) 
    ( ;Neither does :
      ;this or that
      {or whatever}
      1...b5?! {this one} )
    2. d4 d5??
    3. e5 c5!
  """

object Hello extends IOApp.Simple:
  def run =
    val parsed = Parser.full(PgnStr(game)).toOption.get
    val tree   = parsed.tree.get
    // count total nodes in the game
    IO.println(tree.totalNodes) >>
      // get the main line of the pgn
      IO.println(tree.mainLine) >>
      // remove the child of the first node
      IO.println(tree.revalueChild(_ => true).mainLine) >>
      // println all san values in the tree
      tree.traverse_(x => IO.println(x.san)) >>
      // remove all comments & metas from the pgn
      IO.println(tree.map(_.copy(variationComments = None, metas = Metas.empty)).mainLine)
