//> using scala 3.3.3
//> using toolkit typelevel:latest

import cats.Applicative
import cats.Monad
import cats.Functor
import cats.syntax.all.*
import cats.effect.{IO, IOApp, Temporal}
import org.http4s.Uri
import org.http4s.client.Client
import org.http4s.client.middleware.RetryPolicy.exponentialBackoff
import org.http4s.client.middleware.{Retry, RetryPolicy}
import scala.concurrent.duration.*

object Main extends IOApp.Simple {

  def retry[F[_]: Temporal]: Client[F] => Client[F] = Retry(
    RetryPolicy(
      exponentialBackoff(10.seconds, 3),
      (_, response) =>
        response match {
          case Right(_) => true
          case _ => false
        }
    )
  )

  def run: IO[Unit] =
    Option(1).map(_ + 1) // Some(2)

    def f(x: Int): Option[Int] = Some(x + 1)

    Option(1).map(f) // Some(Some(2))
    Option(1).flatMap(f) // Some(2)

    IO.println("Hello Cats Effect!")

  def x(x: Option[Int])(f: Int => Option[Double]): Option[Option[Double]] =
    x.map(f)

  class X[A](a: A)
  Applicative[X].pure(2)

  implicit val ax: Monad[X] = ???
  // with
  //   def pure[A](x: A): X[A] = new X(x)
  //   def ap[A, B](ff: X[A => B])(fa: X[A]): X[B] = ???

  // 2.pure[Option] // Some(2)
  2.pure[X]
  Applicative[Option].pure(2) // Some(2)
  Applicative[List].pure(2) // List(2)
  Monad[Option].pure(2) // Some(2)

  def functor[F[_]: Monad](x: F[Int])(func: Int => F[Double]): F[Double] =
    x.map(func).flatten

  def functor2[F[_]: Functor](x: F[Int])(f: Int => Double): F[Double] =
    x.map(f)

  def combine[F[_]: Applicative](x: F[Int], y: F[String])(f: (Int, String) => Double): F[Double] =
    (x, y).mapN(f)

  def monad[F[_]: Monad](x: F[Int], y: F[String])(f: (Int, String) => Double): F[Double] =
    x.flatMap(a => y.map(b => f(a, b)))

}
