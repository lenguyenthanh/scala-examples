//> using scala 3.5.0
//> using dep org.typelevel::cats-core:2.12.0
//> using dep org.typelevel::cats-effect:3.5.4

import cats.*
import cats.syntax.all.*
import cats.effect.*

object OnError extends IOApp.Simple:

  def run: IO[Unit] =
    val x: Either[Throwable, Unit] = Exception("Oh no 1!").asLeft
    // val y = x.onError(e => Exception("Something went wrong!").asLeft)
    val y = x.onError(_ => ().asRight)
    IO.println(y)

extension [F[_], E, A](fa: F[A])(using F: ApplicativeError[F, E])
  def onError1(pf: PartialFunction[E, F[Unit]]): F[A] =
    fa.handleErrorWith(e =>
      pf.andThen(F.map2(_, F.raiseError[A](e))((_, b) => b)).applyOrElse(e, F.raiseError)
    )
