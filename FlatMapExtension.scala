//> using scala 3.3.3

extension [A](xs: Iterator[A])
  def flatMapEither[B](f: A => Either[?, B]): Iterator[B] =
    xs.flatMap(f(_).toOption)

val xs                             = Iterator(1, 2, 3)
def f(x: Int): Either[String, Int] = if x % 2 == 0 then Right(x) else Left("odd")

val ys = xs.flatMapEither(x => if x % 2 == 0 then Right(x) else Left("odd"))

def odd(x: Int): Boolean = (x % 2) != 0

val x = List(1, 3, 5).map(odd).forall(identity) // false


//
def id[A](x: A): A = x

def transform[A](xs: List[A]): List[A] = xs.flatMap(x => List(x, x))

def f[A, B](x: A): B = ???

def theorem[A, B] =

  val xs: List[A] = ???

  transform(xs.map(f)) == transform(xs).map(f)
