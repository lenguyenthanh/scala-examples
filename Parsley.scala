//> using scala "3.3.0-RC6"
//> using options "-Wunused:all"
//> using dep "com.github.j-mie6::parsley:4.2.9"
//> using dep "org.typelevel::toolkit:latest.release"

import parsley.Parsley, Parsley.attempt
import parsley.character.digit
import parsley.implicits.character.charLift
import parsley.implicits.zipped.Zipped2

// Standard number parser
val number = digit.foldLeft1[Int](0)((n, d) => n * 10 + d.asDigit)

lazy val expr: Parsley[Int] =
  attempt((expr <* '+', term).zipped(_ + _)) <|>
    (expr <* '-', term).zipped(_ - _) <|>
    term
lazy val term: Parsley[Int] =
  (term <* '*', atom).zipped(_ * _) <|> atom
lazy val atom = '(' *> expr <* ')' <|> number

@main def main =
  println("hello")
