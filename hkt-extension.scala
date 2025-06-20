package chess

trait Functor[F[_]]:
  def map[A, B](fa: F[A])(f: A => B): F[B]

trait CanPlay[A]:
    def play(a: A, move: Int): A
    def play[F[_]: Functor](a: A, moves: F[A]): A

object CanPlay:
  given CanPlay[Int]:
    def play(a: Int, move: Int): Int = ???
    def play[F[_]: Functor](a: Int, moves: F[Int]): Int = ???

  3.play(2) // 5
  2.play(List(1, 2, 3)) // List(3, 4, 5)
