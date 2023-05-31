//> using scala 3.3.0
//> using toolkit typelevel:latest

import cats.*
import cats.syntax.all.*

extension [F[_] : Functor, G[_]: Functor, A, B](fga: F[G[A]])
  def dmap(f: A => B): F[G[B]] = fga.map(x => x.map(f))

extension [F[_] : Monad, A, B](fa: F[A])
  def >>=(f: A => F[B]): F[B] = fa.flatMap(f)

@main def main =
  println(List(Option(1), Option(2)).dmap(_ + 1))
  val f: Int => Option[Int] = x => Option(x + 1)
  val x = Option(1)
  println(x >>= f)
