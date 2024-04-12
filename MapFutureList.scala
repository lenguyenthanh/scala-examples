//> using scala "3.4.1"
//> using repository "https://raw.githubusercontent.com/lichess-org/lila-maven/master"
//> using dep "org.lichess::scalalib-model:11.1.3"

import scalalib.paginator.*
import scala.concurrent.Future

val page: Future[Paginator[Int]] = ???

def f(x: Seq[Int])(using Float): Future[Seq[String]] = ???

import scala.concurrent.ExecutionContext.Implicits.global

@main def main =
  given x: Float = 1.0
  page.flatMap(_.mapFutureList(f))
