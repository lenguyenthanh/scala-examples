//> using scala 3.7.1
//> using dep co.fs2::fs2-core:3.12.0
//> using options -language:higherKinds -Xkind-projector


import cats.syntax.all.*
import cats.effect.*
import fs2.Stream
import scala.concurrent.duration.*
import cats.effect.kernel.Unique.Token
import cats.effect.kernel.Sync.Type
import scala.concurrent.ExecutionContext

object main extends IOApp.Simple {

  // Example usage
  // val exampleStream = Stream.empty // This is an empty stream
  // val exampleStream = Stream(1, 2, 3) // This is a non-empty stream

  trait Logger[F[_]] {
    def log(msg: String): F[Unit]
  }
  object Logger {
    def apply[F[_]](implicit logger: Logger[F]): Logger[F] = logger

    implicit val ioLogger: Logger[IO] = new Logger[IO] {
      def log(msg: String): IO[Unit] = IO(println(msg))
    }

  }

  def terminatingOnError[F[_]: Logger: Temporal](e: Throwable): F[Unit] =
    // println(F.isInstanceOf[IO[?]])
    if (Temporal[F].isInstanceOf[Async[IO] @unchecked]) {
      // If we are in an IO context, we can signal the process control
      Logger[F].log("yo it's an io")
    } else {
      Logger[F].log("yo it's not an io")
    }


  def f[F[_]: Temporal: Logger]: F[Unit] =
    Temporal[F].sleep(10.milliseconds) *> (new RuntimeException("hahaha").raiseError[F, Unit])

  def run: IO[Unit] =
    // f[IO].onError(terminatingOnError)
    f[Option].onError(terminatingOnError)
    IO.println("Finished running")

  given Logger[Option] = new Logger[Option] {
    def log(msg: String): Option[Unit] = Some(println(msg))
  }

  given Async[Option[*]]:


    override def evalOn[A](fa: Option[A], ec: ExecutionContext): Option[A] = ???

    override def executionContext: Option[ExecutionContext] = ???

    override def cont[K, R](body: Cont[Option, K, R]): Option[R] = ???

    override def suspend[A](hint: Type)(thunk: => A): Option[A] = ???

    override def flatMap[A, B](fa: Option[A])(f: A => Option[B]): Option[B] = fa match {
      case None => None
      case Some(a) => f(a)
    }

    override def tailRecM[A, B](a: A)(f: A => Option[Either[A, B]]): Option[B] = ???

    override def ref[A](a: A): Option[Ref[Option, A]] = ???

    override def deferred[A]: Option[Deferred[Option, A]] = ???

    override def start[A](fa: Option[A]): Option[Fiber[Option, Throwable, A]] = ???

    override def never[A]: Option[A] = ???

    override def cede: Option[Unit] = ???

    override def forceR[A, B](fa: Option[A])(fb: Option[B]): Option[B] = ???

    override def uncancelable[A](body: Poll[Option] => Option[A]): Option[A] = ???

    override def canceled: Option[Unit] = ???

    override def onCancel[A](fa: Option[A], fin: Option[Unit]): Option[A] = ???

    override def unique: Option[Token] = ???

    def pure[A](x: A): Option[A] = Some(x)

    def raiseError[A](e: Throwable): Option[A] = None

    def handleErrorWith[A](fa: Option[A])(f: Throwable => Option[A]): Option[A] = fa match {
      case None => f(new RuntimeException("Handled error"))
      case Some(a) => Some(a)
    }

    def monotonic: Option[FiniteDuration] = Some(FiniteDuration(System.currentTimeMillis(), "ms"))

    def realTime: Option[FiniteDuration] = Some(FiniteDuration(System.currentTimeMillis(), "ms"))

    protected def sleep(time: FiniteDuration): Option[Unit] = Some(())

//   given Temporal[Either[Throwable, *]]:
//
//
//     override def pure[A](x: A): Either[Throwable, A] = Right(x)
//
//     override def raiseError[A](e: Throwable): Either[Throwable, A] = Left(e)
//
//     override def handleErrorWith[A](fa: Either[Throwable, A])(f: Throwable => Either[Throwable, A]): Either[Throwable, A] = fa match {
//       case Left(e) => f(e)
//       case Right(a) => Right(a)
//     }
//
//     override def monotonic: Either[Throwable, FiniteDuration] = Right(FiniteDuration(System.currentTimeMillis(), "ms"))
//
//     override def realTime: Either[Throwable, FiniteDuration] = Right(FiniteDuration(System.currentTimeMillis(), "ms"))
//
//     override protected def sleep(time: FiniteDuration): Either[Throwable, Unit] = Right(println(s"Sleeping for $time")) // Simulate sleep
//
//     override def flatMap[A, B](fa: Either[Throwable, A])(f: A => Either[Throwable, B]): Either[Throwable, B] = fa match {
//       case Left(e) => Left(e)
//       case Right(a) => f(a)
//     }
//
//     override def tailRecM[A, B](a: A)(f: A => Either[Throwable, Either[A, B]]): Either[Throwable, B] = {
//       def loop(a: A): Either[Throwable, B] = f(a) match {
//         case Left(e) => Left(e)
//         case Right(Left(nextA)) => loop(nextA)
//         case Right(Right(b)) => Right(b)
//       }
//       loop(a)
//     }
//
//     // override def ref[A](a: A): Either[Throwable, Ref[[_] =>> Either[Throwable, _], A]] = Left(new NotImplementedError("Ref not implemented"))
//
//     // override def deferred[A]: Either[Throwable, Deferred[[_] =>> Either[Throwable, _], A]] = ???
//
//     // override def start[A](fa: Either[Throwable, A]): Either[Throwable, Fiber[[_] =>> Either[Throwable, _], Throwable, A]] = ???
//
//     override def never[A]: Either[Throwable, A] = ???
//
//     override def cede: Either[Throwable, Unit] = ???
//
//     override def forceR[A, B](fa: Either[Throwable, A])(fb: Either[Throwable, B]): Either[Throwable, B] = ???
//
//     override def uncancelable[A](body: Poll[[_] =>> Either[Throwable, _]] => Either[Throwable, A]): Either[Throwable, A] = ???
//
//     override def canceled: Either[Throwable, Unit] = ???
//
//     override def onCancel[A](fa: Either[Throwable, A], fin: Either[Throwable, Unit]): Either[Throwable, A] = ???
//
//     override def unique: Either[Throwable, Token] = ???
//
}

