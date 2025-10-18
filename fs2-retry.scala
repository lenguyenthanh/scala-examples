//> using scala 3.7.3
//> using dep co.fs2::fs2-core:3.12.2

import cats.effect.{IO, IOApp}

object main extends IOApp.Simple {

  import fs2.Stream
  import scala.concurrent.duration._

  type VaultToken = String
  val initialRenewBackoff: FiniteDuration = 2.seconds
  val renewBackoffFactor: Float = 1.5f

  def renewOnDuration(token: VaultToken): IO[VaultToken] = {

    def backoff(prev: FiniteDuration): FiniteDuration =
      Duration.fromNanos(Math.ceil(prev.toNanos.toDouble * renewBackoffFactor).toLong)

    val renew = IO.realTimeInstant.flatMap(t => IO.println(s"Token renewed at $t")) *> IO.raiseError(new Exception("Simulated renew failure"))

    fs2.Stream.retry(renew, initialRenewBackoff, backoff, 8)
      .compile
      .lastOrError
  }

  override def run: IO[Unit] = renewOnDuration("my-vault-token").void
}
