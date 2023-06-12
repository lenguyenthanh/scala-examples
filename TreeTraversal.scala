//> using scala 3.3.0
//> using toolkit typelevel:latest

import cats.effect.{IO, IOApp}

case class Rose[A](a: A, f: Forest[A])
case class Forest[A](rs: List[Rose[A]])

def foldR[A, B, C](f: (A, C) => B, g: List[B] => C)(r: Rose[A]): B =
  f(r.a, foldF(f, g)(r.f))

def foldF[A, B, C](f: (A, C) => B, g: List[B] => C)(r: Forest[A]): C =
  g(r.rs.map(foldR(f, g)))

def foldRose[A, B](f: (A, List[B]) => B)(r: Rose[A]): B =
  f(r.a, r.f.rs.map(foldRose(f)))

def unfoldR[A, B](f: B => A, g: B => List[B])(b: B): Rose[A] =
  Rose(f(b), Forest(g(b).map(unfoldR(f, g))))

def unfoldF[A, B](f: B => A, g: B => List[B])(b: B): Forest[A] =
  Forest(g(b).map(unfoldR(f, g)))


object Main extends IOApp.Simple:
  def run =
    val rose: Rose[Int] = Rose(1, Forest(List(Rose(2, Forest(List(Rose(3, Forest(Nil))))))))
    IO.println("Hello World!")
