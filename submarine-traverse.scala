//> using scala 3.7.3
//> using dep "org.typelevel::cats-core:2.13.0"
//> using dep "org.typelevel::cats-mtl:1.6.0"
//> using dep "org.typelevel::cats-effect:3.6.3"
// //> using options -Xprint:typer
//> using options -explain

import cats.syntax.all.*
import cats.effect.*
import cats.mtl.{ Handle, Raise }
import cats.Monad
import scala.concurrent.{ ExecutionContext as EC, Future }
import java.util.concurrent.Executor
import cats.Traverse
import cats.Applicative

object Main extends IOApp.Simple:
  type IORaise[E, A] = Raise[IO, E] ?=> IO[A]

  def foo(s: Int): IORaise[String, Int] =
    if s == 2 then Raise[IO, String].raise("Error at 2")
    else IO.println(s"Processing $s").as(s * 2)

  val xs: List[Int] = List(1, 2, 3)

  // traverse the list, short-circuiting on the first error
  // returning the first error and the successfully processed elements
  def kindOfTraverse1[E, A, B](list: List[A])(f: A => Either[E, B]): (Option[E], List[B]) =
    val (o, xs) = list
      .foldLeft((none[E], List.empty[B])): (acc, a) =>
        acc match
          case (Some(e), bs) => (Some(e), bs) // short-circuit on first error
          case (None, bs) =>
            f(a) match
              case Left(e)  => (Some(e), bs)
              case Right(b) => (None, b :: bs)
    (o, xs.reverse)

  // traverse the list, short-circuiting on the first error
  // returning the first error and the successfully processed elements
  def kindOfTraverse[F[_]: Traverse, G[_]: Applicative, E, A, B](list: F[A])(f: A ?=> Raise[G, E] => G[B])(
      using Raise[G, E]
  ): G[(Option[E], F[B])] = ???

  def test =
    Handle
      .allow:
        xs.traverse(foo)
      .rescue: e =>
        IO.println(s"Error: $e").as(Nil)

  def run: IO[Unit] =
    test *> IO.println("Submarine Traverse")

object FU:
  type Fu[A]         = Future[A]
  type FuRaise[E, A] = Raise[Future, E] ?=> Future[A]
  def fuccess[A](a: A): Fu[A] = Future.successful(a)
  import cats.syntax.all.*

  extension [A](list: List[A])
    def sequentially[G[_], B](f: A => G[B])(using Monad[G]): G[List[B]] =
      list
        .foldLeft(List.empty[B].pure[G]): (acc, a) =>
          acc.flatMap: bs =>
            f(a).map(_ :: bs)
        .map(_.reverse)

    /** traverse the list, short-circuiting on the first error returning the first error if there is and the
      * successfully processed elements
      */
    def sequentiallyRaise[E, B](f: A => FuRaise[E, B])(using EC): Fu[(List[B], Option[E])] =
      import cats.mtl.Handle.*
      list
        .foldLeft(fuccess((List.empty[B], none[E]))) { (facc, a) =>
          facc.flatMap: (bs, acc) =>
            acc match
              case Some(e) => fuccess(bs -> acc) // short-circuit on first error
              case None =>
                allow:
                  f(a).map { b => (b :: bs) -> none }
                .rescue: e =>
                  fuccess((bs, e.some))
        }

  def foo(s: Int)(using EC): FuRaise[String, Int] =
    if s == 2 then Raise[Future, String].raise("Error at 2")
    else Future(println(s"Processing $s")).map(_ => s * 2)

  def test[B, E](using EC) =
    cats.mtl.Handle
      .allow:
        List(1, 2, 3).sequentially(foo)
      .rescue: _ =>
        Future(println(s"Error")).as(Nil)
