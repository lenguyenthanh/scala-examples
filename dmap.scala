//> using scala 3.3.0-RC6
//> using toolkit typelevel:latest

import cats.Functor
import cats.syntax.all.*

extension [F[_] : Functor, G[_]: Functor, A, B](fga: F[G[A]])
  def dmap(f: A => B): F[G[B]] = fga.map(x => x.map(f))

@main def main =
  println(List(Option(1), Option(2)).dmap(_ + 1))
