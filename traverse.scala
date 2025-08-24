//> using scala 3.7.2
//> using dep org.typelevel::cats-core:2.13.0
//> using dep org.typelevel::cats-effect:3.6.3
//> using options -Xprint:typer

import cats.*
import cats.syntax.all.*
import cats.effect.*

// def a: Option[IO[Int]] = ???
// def b: Option[IO[String]] = ???
// def c: Option[IO[String]] = ???
// def pair: (Option[IO[Int]], Option[IO[String]]) = (a, b)
//
// // Fu[(Option[A], Option[B])]
// // IO(Option[A], Option[B])
//
// def x: Option[(IO[Int], IO[String])] = pair.mapN((a, b) => (a -> b))
// def y: IO[Option[(Int, String)]] = pair.mapN((a, b) => (a, b).mapN((a, b) => (a, b))).sequence
// val s: IO[(Option[Int], Option[String])] = (a.sequence, b.sequence).tupled
// val x1 = pair.bitraverse( _.sequence, _.sequence)
// // def z: IO[(Option[Int], Option[String])] = pair.mapN((a, b) => (a, b))
// val x2 = pair.bisequence
// // val x3 = (a.sequence, b.sequence, c.sequence).bisequence

inline def genericF[T <: Tuple, F[_], G[_], I <: Tuple.InverseMap[T, [A] =>> G[F[A]]], B](f: I => B, t: T): F[B] = ???
val x: (Option[List[Int]], Option[List[String]]) = ???
val f: ((Int, String)) => Int = ???
val y = genericF(f, x)
