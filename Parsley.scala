//> using scala 3.3.0-RC6
//> using options -Wunused:all
//> using dep com.github.j-mie6::parsley:4.2.10
//> using toolkit typelevel:latest

import parsley.Parsley
import parsley.character.digit
import parsley.implicits.character.charLift
import parsley.expr.chain

// Standard number parser
val number = digit.foldLeft1[Int](0)((n, d) => n * 10 + d.asDigit)

val add = (x: Int, y: Int) => x + y
val sub = (x: Int, y: Int) => x - y

// chain.left1[A](p: Parsley[A], op: Parsley[(A, A) => A]): Parsley[A]
lazy val expr: Parsley[Int] = chain.left1(term, ('+' #> add <|> '-' #> sub))
lazy val term               = chain.left1[Int](atom, '*' #> (_ * _))
val atom                    = '(' *> expr <* ')' <|> number

@main def main =
  println("hello")
