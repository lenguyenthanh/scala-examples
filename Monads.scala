//> using scala 3.3.1

trait Functor[F[_]]:
  def fmap[A, B](f: A => B): F[A] => F[B]

trait Monad[F[_]]:
  def pure[A](x: A): F[A]
  def flatMap[A, B](f: A => F[B]): F[A] => F[B]

enum O[A] extends Functor[O] with Monad[O]:

  case Some(x: A) extends O[A]

  def fmap[A, B](f: A => B): O[A] => O[B] =
    case Some(x) => Some(f(x))

  def pure[A](x: A): O[A] = Some(x)

  def flatMap[A, B](f: A => O[B]): O[A] => O[B] =
    case Some(x) => f(x)



enum Some[A]:
  def flatMap[A, B](f: A => Some[B]): Some[A] => Some[B] =
    case Some(x) => f(x)

object Some:
  def pure[A](x: A): Some[A] = Some(x)
