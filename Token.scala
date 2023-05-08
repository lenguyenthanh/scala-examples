//> using scala "3.3.0-RC5"
//> using dep "org.typelevel::toolkit:latest.release"
//> using dep "org.typelevel::kittens:3.0.0"
//> using dep "dev.optics::monocle-core:3.2.0"
//> using options "-Yexplicit-nulls"


enum Token(val lexeme: String):
  case Identifier(override val lexeme: String) extends Token(lexeme)
  case Str(override val lexeme: String) extends Token(lexeme)
  case Number(override val lexeme: String) extends Token(lexeme)

case class Span(start: Int, end: Int)

case class WithSpan[+A](span: Span, value: A)
