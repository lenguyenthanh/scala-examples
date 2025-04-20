//> using dep org.typelevel::cats-parse:1.0.0

import cats.parse.{Parser, Parser0}

object IgnoreParenthesesParser {
  // Define a parser that matches any content inside parentheses, including nested ones
  private val nestedParens: Parser0[Unit] = {
    val openParen = Parser.char('(')
    val closeParen = Parser.char(')')
    val content = Parser.recursive[Unit] { recurse =>
      (openParen *> recurse.rep0 *> closeParen).void | Parser.anyChar.void
    }
    (openParen *> content.rep0 *> closeParen).void
  }

  // Define a parser that matches any character except parentheses
  private val otherContent: Parser[Unit] = Parser.anyChar.filter(c => c != '(' && c != ')').void

  // Combine the two parsers to handle both nested parentheses and other content
  val parser: Parser[Unit] = (nestedParens | otherContent).rep0.void

  def main(args: Array[String]): Unit = {
    val input = "This is (ignored (nested (parentheses))) text"
    val result = parser.parseAll(input)
    result match {
      case Right(s) => println(s"Successfully ignored parentheses! $s")
      case Left(err) => println(s"Parsing failed: $err")
    }
  }
}
