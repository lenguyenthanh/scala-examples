//> using scala 4.7.3
//> using options -Xkind-projector

trait Functor[F[_]] {
  def map[A, B](x: F[A], f: A => B): F[B]
}


def f[L]: Functor[Either[L, *]] = new Functor[Either[L, *]] {
  def map[A, B](x: Either[L, A], f: A => B): Either[L, B] =
    x.map(f)
}
