//> using scala 3.6.4
//> using toolkit typelevel:default

import cats._
import cats.effect._
import cats.implicits._

trait MyTrait[F[_]] {

  def methodOne(data: String): F[String]
  def methodTwo(data: String): F[Int]
  def methodThree(data: String): F[Unit]

  def method4(data: String)(using Monad[F]) : F[Unit] = {
    for {
      _ <- methodOne(data)
      _ <- methodTwo(data)
      _ <- methodThree(data)
    } yield ()
  }
}

class MyClass[F[_]: Sync] extends MyTrait[F]:

  override def methodOne(data: String) : F[String] = {
    Monad[F].pure(data.toUpperCase)
  }

  override def methodTwo(data: String) : F[Int] = {
    Monad[F].pure(data.length)
  }

  override def methodThree(data: String) : F[Unit] = {
    for {
      _      <- Sync[F].delay(println(s"Processing: $data"))
      length <- methodTwo(data)
      _      <- Sync[F].delay(println(s"Data Length: $length"))
    } yield ()
  }

object MyApp extends IOApp.Simple {
  override def run: IO[Unit] = {
    given Monad[IO] = Sync[IO] // Provide the Monad[IO] instance

    val myClassInstance = new MyClass[IO]
    for {
      resultOne <- myClassInstance.methodOne("Hello Cats Effect!")
      resultTwo <- myClassInstance.methodTwo("Hello Cats Effect!")
      _         <- myClassInstance.methodThree("Hello Cats Effect!")
      _         <- IO(println(s"Result One: $resultOne, Result Two: $resultTwo"))
    } yield ()
  }
}

