//> using scala 3.nightly
//> using dep org.typelevel::cats-core:2.13.0
//> using dep org.typelevel::kittens:3.5.0

import cats.*
import cats.syntax.all.*
import cats.derived.*

case class Three[A](p1: A, p2: A, p3: A) derives Functor

case class Grid[A](grid: Three[Three[A]]) derives Functor

// given Applicative[Grid]:
//   def pure[A](x: A): Grid[A] = Grid(Three(Three(x, x, x), Three(x, x, x), Three(x, x, x)))
//   def ap[A, B](ff: Grid[A => B])(fa: Grid[A]): Grid[B] =
//     (fa, ff) match
//       case (Grid(Three(Three(a1, a2, a3), Three(a4, a5, a6), Three(a7, a8, a9))),
//             Grid(Three(Three(f1, f2, f3), Three(f4, f5, f6), Three(f7, f8, f9)))) =>
//         Grid(Three(Three(f1(a1), f2(a2), f3(a3)),
//                    Three(f4(a4), f5(a5), f6(a6)),
//                    Three(f7(a7), f8(a8), f9(a9))))

given Applicative[Three]:
  def pure[A](x: A): Three[A] = Three(x, x, x)
  def ap[A, B](ff: Three[A => B])(fa: Three[A]): Three[B] =
    (fa, ff) match
      case (Three(a1, a2, a3), Three(f1, f2, f3)) => Three(f1(a1), f2(a2), f3(a3))

given Distributive[Three]:
  def distribute[F[_]: Functor, A, B](fa: F[A])(f: A => Three[B]): Three[F[B]] =
    val ftb = fa.map(f)
    Three(ftb.map(_.p1), ftb.map(_.p2), ftb.map(_.p3))

  def map[A, B](fa: Three[A])(f: A => B): Three[B] =
    Three(f(fa.p1), f(fa.p2), f(fa.p3))

