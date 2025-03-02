//> using scala 3.6.3
//> using toolkit typelevel:default

import cats.effect.{IO, MonadCancelThrow, Async}
import cats.effect.syntax.all.*

trait Algebra[F[_]]:
  def run[F[_]: Async](f: F[Unit]) =
    f.bracket(_ => Async[F].unit)(_ => Async[F].unit)
