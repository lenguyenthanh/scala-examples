//> using scala 3.nightly
//> using options -Xkind-projector -language:higherKinds

trait Functor[F[_]]:
  extension [A] (fa: F[A])
    def map[B](f: A => B): F[B]

trait Contravariant[F[_]]:
  extension [A] (fa: F[A])
    def contramap[B](f: B => A): F[B]

trait Profunctor[F[_, _]]:
  extension [A, B] (pab: F[A, B])
    def dimap[C, D](f: C => A, g: B => D): F[C, D]

// Function1
case class Reader[-R, +A](run: R => A)

object Reader:

  given [R]: Profunctor[Reader] with
    extension [A, B] (pab: Reader[A, B])
      def dimap[C, D](f: C => A, g: B => D): Reader[C, D] =
        Reader(c => g(pab.run(f(c))))

  given [R]: Functor[[A] =>> Reader[R, A]] with
    extension [A] (fa: Reader[R, A])
      def map[B](f: A => B): Reader[R, B] =
        Reader(r => f(fa.run(r)))

  given [A]: Contravariant[[R] =>> Reader[R, A]] with
    extension [R] (fa: Reader[R, A])
      def contramap[T](f: T => R): Reader[T, A] =
        Reader(t => fa.run(f(t)))

case class Op[R, -A](run: A => R)
object Op:
  given [R]: Contravariant[[A] =>> Op[R, A]] with
    extension [A] (fa: Op[R, A])
      def contramap[B](f: B => A): Op[R, B] =
        Op(fa.run compose f)

  def predToString[A]: Op[Boolean, A] => Op[String, A] =
    case Op(f) => Op(a => if f(a) then "true" else "false")

  val f: B => A = ???

  val op: Op[Boolean, A] = ???
  // x and y should be equivalent
  // naturality law
  val x = op.contramap(f).compose(predToString)
  val y = predToString(op.contramap(f))
