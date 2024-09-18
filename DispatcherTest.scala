//> using scala 3.5.0
//> using dep org.typelevel::cats-effect:3.5.4

import cats.effect.std.Dispatcher
import cats.effect.unsafe.implicits.global
import cats.effect.IO
import scala.concurrent.Await
import scala.concurrent.duration.Duration

@main def main() =
  val (dispatcher, shutDownDispatcher) = Dispatcher.sequential[IO].allocated.unsafeRunSync()

  dispatcher.unsafeToFuture(IO.never)
  val cancel = dispatcher.unsafeRunCancelable(IO.unit)
  Await.result(cancel(), Duration.Inf) // hangs
