//> using scala 3.3.0
//> using toolkit typelevel:latest
//> using dep "org.typelevel::alleycats-core:2.9.0"
//> using repository https://raw.githubusercontent.com/lichess-org/lila-maven/master
//> using dep com.github.ornicar::scalalib:9.5.5

import cats.syntax.option._
import alleycats.Zero
import ornicar.scalalib.zeros.given

extension [A](self: A)
  infix def so[B](f: A => B)(using az: Zero[A], bz: Zero[B]): B = if self == az.zero then bz.zero else f(self)
  infix def so[B](b: B)(using az: Zero[A], bz: Zero[B]): B = if self == az.zero then bz.zero else b

@main def Test =
  val x = true so 2 so "hello" so Option(3) so List(2) so 3L
  println(x) // 3L
  val y = true so 2 so "hello" so none so List(2) so 3L
  println(y) // 0L

