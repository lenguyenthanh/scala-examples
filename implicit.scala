//> using scala 3.nightly
//> using dep org.typelevel::cats-core:2.13.0

import cats.*

object Main:

  // trait Functor[F[_]]:
  //   def map[A, B](fa: F[A])(f: A => B): F[B]
  //
  // trait Applicative[F[_]] extends Functor[F]:
  //   def pure[A](x: A): F[A]
  //   def ap[A, B](ff: F[A => B])(fa: F[A]): F[B]
  //
  // trait Distributive[F[_]] extends Functor[F]:
  //   def distribute[G[_]: Functor, A, B](fa: G[A])(f: A => F[B]): F[G[B]]
  //   def cosequence[G[_]: Functor, A](fa: G[F[A]]): F[G[A]] =
  //     distribute(fa)(identity)
  //
  def transpose[A](ta: Two[Two[A]])(using D: Distributive[Two]): Two[Two[A]] =
    D.cosequence(ta)

  case class Two[A](p1: A, p2: A)

  given Functor[Two]:
    def map[A, B](fa: Two[A])(f: A => B): Two[B] = ???

  given Applicative[Two]:
    def pure[A](x: A): Two[A] = ???
    def ap[A, B](ff: Two[A => B])(fa: Two[A]): Two[B] = ???

  given Distributive[Two]:
    def distribute[G[_]: Functor, A, B](fa: G[A])(f: A => Two[B]): Two[G[B]] = ???
    def map[A, B](fa: Two[A])(f: A => B): Two[B] = ???
