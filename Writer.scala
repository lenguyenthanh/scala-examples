//> using scala 3.5.0
//> using toolkit typelevel:default

import cats._
import cats.syntax.all._
import cats.data._
import cats.data.Writer
import cats.instances.all._
import cats.instances.vector._
import cats.syntax.applicative._
import cats.syntax.writer._
import cats.Id

type Logged[A] = Writer[Vector[String], A]

object Main {
  def main(args: Array[String]): Unit = {
    val writer1 = for {
      a <- 10.pure[Logged]
      _ <- Vector(
        "a",
        "b",
        "c"
      ).tell // this is the line I have trouble understanding, how does _ affect the result?
      b <- 32.writer(Vector("x", "y", "z"))
    } yield a + b
    println(writer1)
  }

  // your writer1 example is equivalent to the following (once we desugar the for comprehension):
  val writer2 = 10
    .pure[Logged]
    .flatMap { a =>
      Vector("a", "b", "c").tell
        .flatMap { _ => // _ () is the unit so We can ignore it
          32.writer(Vector("x", "y", "z"))
            .map(b => a + b)
        }
    }
  // the magic is underline in flatMap, during the flatMap, it'll concatenate (using monoid instance) the Vector("a", "b", "c") and Vector("x", "y", "z")
  // check line 55

  println(writer2)

  val x: Writer[Vector[String], Unit] = Vector("a", "b", "c").tell
}

// a simplified version of Writer
case class Wr[L, A](l: L, a: A)

object Wr {
  implicit def monad[L: Monoid]: Monad[[A] =>> Wr[L, A]] = new Monad[[A] =>> Wr[L, A]] {
    def pure[A](x: A): Wr[L, A] = Wr(Monoid[L].empty, x)
    def flatMap[A, B](fa: Wr[L, A])(f: A => Wr[L, B]): Wr[L, B] = {
      val Wr(l1, a) = fa
      val Wr(l2, b) = f(a)
      Wr(Monoid[L].combine(l1, l2), b) // so for every flatMap they'll combine the L together
    }
    def tailRecM[A, B](a: A)(f: A => Wr[L, Either[A, B]]): Wr[L, B] = ???
  }
}
