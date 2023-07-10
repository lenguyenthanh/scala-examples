//> using scala 3.3.0
//> using toolkit typelevel:latest

import cats.data.State
import scala.concurrent.Future

object Test:
  given scala.concurrent.ExecutionContext.global
  val nextLong: State[Future[AsyncSeed], Future[Long]] = State { seedF =>
    seedF.map { seed =>
      (seed.next, seed.long)
    }
  }
