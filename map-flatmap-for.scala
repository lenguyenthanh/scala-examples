//> using scala 3.nightly
//> using dep org.typelevel::cats-core:2.13.0
//> using options -explain

import cats.{Functor, Monad, Traverse}
import cats.syntax.flatMap.*
import cats.syntax.functor.*
import cats.syntax.traverse.*

type File = Int

def x[F[_]: Monad: Traverse: Functor](f: F[Int]) =
  for
    y <- f
  yield y

  def run[F[_]: Monad, A](
    files: Vector[File],
    fa: F[A],
  ) : F[A] = {

    final case class Wrapper(str: String)

    val map: Map[String, Vector[F[String]]] = Map.empty

    lazy val newA =
      map.toList
        .filter { case (k, _) => List("gdsg").contains(k) }
        .flatMap { case (_, v) => v }
        .sequence
        .map {

          _.traverse { contents =>
            Option(contents.mkString("\n")).filter(_.nonEmpty).map(Wrapper(_))
          }
        }
    for {
      a <- fa
    } yield a


  }
