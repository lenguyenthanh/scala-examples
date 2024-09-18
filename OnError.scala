//> using scala 3.5.0
//> using dep org.typelevel::cats-effect:3.5.4

import cats.effect.*

object OnError extends IOApp.Simple:

  def run: IO[Unit] =
    // currentOnError *>
    newOnError

def currentOnError: IO[Unit] =
  IO.raiseError(new Exception("Oh no 1!"))
    .onError(e => IO.raiseError(new Exception("Something went wrong!")) )

def newOnError: IO[Unit] =
  IO.raiseError(new RuntimeException("Oh no 2!"))
    .onError2 { case e: RuntimeException => IO.raiseError(new Exception("Something went wrong!")) }


extension (io: IO[Unit])
  def reportError: IO[Unit] =
    io.handleErrorWith: t =>
      IO.executionContext.flatMap(ec => IO(ec.reportFailure(t)))

extension [A](io: IO[A])

  def onError2(pf: PartialFunction[Throwable, IO[Unit]]): IO[A] =
    io.handleErrorWith(t => pf.applyOrElse(t, (_: Throwable) => IO.unit).reportError *> IO.raiseError(t))
