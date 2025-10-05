//> using scala 3.7.3
//> using deps org.typelevel::cats-core:2.13.0

import cats.Applicative
import cats.syntax.all.*

val xs: List[Int] = ???
def f[F[_]: Applicative](x: Int): List[F[String]] = ???

def test[F[_]: Applicative]: F[List[String]] = xs.flatMap(f).sequence
def test2[F[_]: Applicative]: F[List[String]] = xs.flatTraverse(x => f(x).sequence)
