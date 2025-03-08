//> using scala 3.nightly
//> using dep org.typelevel::cats-core:2.13.0
//> using dep org.typelevel::kittens:3.5.0

import cats.*
import cats.syntax.all.*
import cats.derived.*

case class Three[A](p1: A, p2: A, p3: A)

case class Grid[A](grid: Three[Three[A]])

given Applicative[Grid]:
  def pure[A](x: A): Grid[A] = Grid(Three(Three(x, x, x), Three(x, x, x), Three(x, x, x)))
  def ap[A, B](ff: Grid[A => B])(fa: Grid[A]): Grid[B] =
    (fa, ff) match
      case (Grid(Three(Three(a1, a2, a3), Three(a4, a5, a6), Three(a7, a8, a9))),
            Grid(Three(Three(f1, f2, f3), Three(f4, f5, f6), Three(f7, f8, f9)))) =>
        Grid(Three(Three(f1(a1), f2(a2), f3(a3)),
                   Three(f4(a4), f5(a5), f6(a6)),
                   Three(f7(a7), f8(a8), f9(a9))))

given Functor[Three]:
  def map[A, B](fa: Three[A])(f: A => B): Three[B] = Three(f(fa.p1), f(fa.p2), f(fa.p3))

given Applicative[Three]:
  def pure[A](x: A): Three[A] = Three(x, x, x)
  def ap[A, B](ff: Three[A => B])(fa: Three[A]): Three[B] =
    (fa, ff) match
      case (Three(a1, a2, a3), Three(f1, f2, f3)) => Three(f1(a1), f2(a2), f3(a3))

given Distributive[Three]:
  def distribute[F[_]: Functor, A, B](fa: F[A])(f: A => Three[B]): Three[F[B]] =
    val ftb = fa.map(f)
    Three(ftb.map(_.p1), ftb.map(_.p2), ftb.map(_.p3))

  // unnecessary, but for demonstration purposes
  override def cosequence[F[_]: Functor, A](fa: F[Three[A]]): Three[F[A]] =
    Three(fa.map(_.p1), fa.map(_.p2), fa.map(_.p3))

  def map[A, B](fa: Three[A])(f: A => B): Three[B] =
    Three(f(fa.p1), f(fa.p2), f(fa.p3))

given Distributive[Grid]:
  def distribute[F[_]: Functor, A, B](fa: F[A])(f: A => Grid[B]): Grid[F[B]] =
    val fga = fa.map(f)
    Grid(
      Three(
        Three(fga.map(_.grid.p1.p1), fga.map(_.grid.p1.p2), fga.map(_.grid.p1.p3)),
        Three(fga.map(_.grid.p2.p1), fga.map(_.grid.p2.p2), fga.map(_.grid.p2.p3)),
        Three(fga.map(_.grid.p3.p1), fga.map(_.grid.p3.p2), fga.map(_.grid.p3.p3))
      )
    )

  def map[A, B](fa: Grid[A])(f: A => B): Grid[B] =
    Grid(Three(fa.grid.p1.map(f), fa.grid.p2.map(f), fa.grid.p3.map(f)))

def composeGrid[A](ga: Grid[A]): Grid[A] =
  Grid(Distributive[Three].cosequence(ga.grid))


@main
def run() =
  val grid = Grid(Three(Three(1, 2, 3), Three(4, 5, 6), Three(7, 8, 9)))
  val composed = composeGrid(grid)
  println(composed)
