//> using scala 3.5.2
//> using toolkit typelevel:latest

import cats.effect.{GenConcurrent, Ref}
import cats.effect.Resource
import cats.~>
import cats.syntax.all.*

object R {

  def ref[F[_], A](a: A)(
    implicit F: GenConcurrent[F, ?]): Resource[F, Ref[Resource[F, *], A]] = {
    val x: F[Ref[F, A]] = F.ref(a)
    val lk: F ~> Resource[F, ?] = Resource.liftK[F]
    val f1: (F ~> Resource[F, ?], Ref[F, A]) => Ref[Resource[F, ?], A] = (x, y) => y.mapK(x)
    val f: Ref[F, A] => Ref[Resource[F, ?], A] = f1(lk, _)
    Resource.eval(x).map(f)
  }

}
