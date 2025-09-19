//> using scala 2.13.16
//> using deps org.typelevel::cats-mtl:1.6.0

object fastsolveError {

  import cats.mtl.Raise
  import cats.mtl.Handle
  import cats.syntax.all._
  import scala.concurrent.Future

  sealed trait Success

  object Success {
    case object A extends Success
    case object B extends Success
  }

  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  def calculate(x: Int)(implicit r: Raise[Future, Success]): Future[Int] = ???

  def process(x: Int)(implicit r: Raise[Future, Success]): Future[Int] =
    if (x > 10) r.raise(Success.A) else ???
  def output(x: Int)(implicit r: Raise[Future, Success]): Future[Int] =
    if (x > 10) r.raise(Success.B) else ???

  def logic(x: Int)(implicit r: Raise[Future, Success]): Future[Int] =
    for {
      a <- calculate(x)
      b <- process(a)
      c <- output(b)
    } yield c

  def main: Future[Int] =
    Handle.allowF[Future, Success] { implicit h =>
      logic(5)
    }.rescue {
      case Success.A => Future.successful(-1)
      case Success.B => Future.successful(-2)
    }
    .catch { case e => Future.failed(e) } // handle other exceptions

  sealed trait ShortCircuitOrError
  sealed trait Success extends ShortCircuitOrError
  sealed trait Error extends ShortCircuitOrError
  def calculate(x: Int)(implicit r: Raise[Future, ShortCircuitOrError]): Future[Int] = ???
}
