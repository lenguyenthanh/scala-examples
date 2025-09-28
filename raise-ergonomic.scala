//> using scala 3.7.3
//> using dep "org.typelevel::cats-mtl:1.6.0"

trait Config:
  def hasPath(path: String): Boolean
  def origin(): Config
  def description(): String

val cfg: Config = ???
def valueThru[T](path: String, extract: (Config, String) => Either[String, T], optDefault: Option[T] = Option.empty): Either[String, T] =
    if cfg.hasPath(path) then extract(cfg, path)
    else optDefault.toRight(s"Not found: '$path' in ${cfg.origin().description()}")

import cats.Applicative
import cats.mtl.Raise
import cats.mtl.syntax.all.*

type RaiseS[F[_]] = Raise[F, String]
// type RaiseS[F[_]] = Applicative[F] ?=> Raise[F, String]

def valueThru2[F[_]: RaiseS as R, T](path: String, extract: (Config, String) => F[T], optDefault: Option[T] = Option.empty)(using Applicative[F]): F[T] =
  if cfg.hasPath(path) then extract(cfg, path)
  else R.fromOption(optDefault)(s"Not found: '$path' in ${cfg.origin().description()}")

def valueThru3[F[_]: RaiseS: Applicative, T](path: String, extract: (Config, String) => F[T], optDefault: Option[T] = Option.empty): F[T] =
  if cfg.hasPath(path) then extract(cfg, path)
  else optDefault.toRaise(s"Not found: '$path' in ${cfg.origin().description()}")

extension[A] (option: Option[A])
  def toRaise[E, F[_]: [F[_]] =>> Raise[F, E]](ifNone: => E): Applicative[F] ?=> F[A] =
    option.fold(Raise[F, E].raise[E, A](ifNone))(Applicative[F].pure)
