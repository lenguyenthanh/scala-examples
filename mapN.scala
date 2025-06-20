//> using scala 3.nightly
//> using dep org.typelevel::cats-core:2.13.0

import cats.syntax.all.*

object Main:

  val x: Option[Int] = Some(1)
  val y: Option[Int] = Some(2)
  case class Two[A](p1: A, p2: A)

  println("mapN")

  val forcom: Option[Two[Int]] =
    for
      a <- x
      b <- y
    yield Two(a, b)

  val flatmap: Option[Two[Int]] =
    x.flatMap(a => y.map(b => Two(a, b)))

  val mapn: Option[Two[Int]] =
    (x, y).mapN((a, b) => Two(a, b))
