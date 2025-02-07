//> using scala 3.5.2
//> using toolkit typelevel:0.1.29

import cats.effect.{ IO, IOApp }
import scala.concurrent.duration.*
import fs2.concurrent.Topic
import cats.effect.kernel.Resource
import cats.effect.implicits.*

object Main extends IOApp.Simple:

  def run: IO[Unit] =
    resource.use(_ => IO.unit)

  def resource =
    for
      _ <- IO.println("start").toResource
      _ <- background3(doWork).toResource.flatMap(_.compile.drain.background)
      _ <- IO.sleep(10.second).toResource
    yield ()

  def background =
    infinity.flatMap(
      _.evalTap(_ => doWork)
        // _.evalTap(_ => doWork.attempt)
        // .handleErrorWith(_ => fs2.Stream.eval(IO.println("*******errrr*****************")).as(List.empty[Int]))
        .compile.drain.background
    )

  def background2 =
    infinity.allocated.toResource
      .flatMap: (stream, unsubscribe) =>
        stream
          .evalTap(_ => doWork)
          // _.evalTap(_ => doWork.attempt)
          .handleErrorWith(_ =>
            fs2.Stream.eval(unsubscribe *> IO.println("*******errrr*****************")).as(List.empty[Int])
          )
          .compile
          .drain
          .background

  def background3(work: IO[Unit]): IO[fs2.Stream[IO, List[Int]]] =
    infinity.allocated
      .map: (stream, unsubscribe) =>
        stream
          .evalTap(_ => work)
          .handleErrorWith(_ =>
            fs2.Stream
              .eval(unsubscribe *> IO.println("*******errrr*****************"))
              .flatMap(_ => x(work))
          )
    // .compile
    // .drain
    // .background

  def x(work: IO[Unit]): fs2.Stream[IO, List[Int]] =
    fs2.Stream.eval(background3(work)).flatten

  def doWork =
    IO.println("ping") *> IO.raiseError(RuntimeException("error"))

  def infinity: Resource[IO, fs2.Stream[IO, List[Int]]] =
    for
      topic <- Topic[IO, List[Int]]().toResource
      _     <- loop(List(0), topic).background
      stream <- topic.subscribeAwaitUnbounded
        .map(_.evalTap(_ => IO.println("pong")))
    yield stream

  def loop(xs: List[Int], topic: Topic[IO, List[Int]]): IO[Unit] =
    for
      e <- topic.publish1(xs)
      _ <- IO.println(e)
      _ <- IO.sleep(1.second)
      _ <- loop(1 :: xs, topic)
    yield ()
