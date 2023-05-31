//> using scala "3.3.0-RC5"
//> using dep "org.typelevel::toolkit::latest.release"

import cats.syntax.all.*
import cats.data.Validated

@main def hello: Unit =
  val l: List[Validated[List[String], Int]] =
    List(Validated.valid(1), Validated.invalid(List("a")), Validated.invalid(List("b")))
  val eitherList: List[Either[List[String], Int]] = List(Right(1), Left(List("a")), Left(List("b")))

  val il                             = List(1, 2, 3)
  def f(x: Int): Either[String, Int] = if x > 0 then Right(x) else Left("negative")

  il.map(f)   // List[Either[String, Int]]
    .sequence // Either[String, List[Int]]
    // il.traverse(f)

  val x = l.sequence
  println(x)
  println(eitherList.sequence)
