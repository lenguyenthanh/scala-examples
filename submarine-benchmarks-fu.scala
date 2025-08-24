//> using scala 3.7.1
//> using dep org.typelevel::cats-effect:3.6.3
//> using dep org.typelevel::cats-mtl:1.6-6ad7882-SNAPSHOT
package bench

import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit

import cats.*
import cats.data.EitherT
import cats.mtl.Handle
import cats.mtl.Handle.*
import cats.mtl.syntax.all.*
import cats.syntax.all.*
import cats.effect.*
import cats.effect.unsafe.implicits.global
import scala.util.control.NoStackTrace

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.Throughput))
@OutputTimeUnit(TimeUnit.SECONDS)
@Measurement(iterations = 15, timeUnit = TimeUnit.SECONDS, time = 3)
@Warmup(iterations = 15, timeUnit = TimeUnit.SECONDS, time = 3)
@Fork(3)
@Threads(value = 1)
class SubmarineBenchmarks:

  @Param(Array( /*"100", */ "1000" /*, "10000"*/ ))
  var size: Int = scala.compiletime.uninitialized

  @Param(Array("100"))
  var cpuTokens: Long = scala.compiletime.uninitialized

  @Benchmark
  def simpleIOWithThrowable(bh: Blackhole) =
    1.to(size)
      .toList
      .traverse_(_ => Future.success(Blackhole.consumeCPU(cpuTokens)) *> simpleIOWithThrowableImpl)
      .unsafeRunSync()

  @Benchmark
  def simpleIOHandle(bh: Blackhole) =
    1.to(size)
      .toList
      .traverse_(_ => IO(Blackhole.consumeCPU(cpuTokens)) *> simpleIOHandleImpl)
      .unsafeRunSync()

  @Benchmark
  def simpleEitherT(bh: Blackhole) =
    1.to(size)
      .toList
      .traverse_(_ => EitherT.rightT[IO, Error.type](Blackhole.consumeCPU(cpuTokens)) *> simpleEitherTImpl)
      .value
      .unsafeRunSync()

  @Benchmark
  def simpleEither(bh: Blackhole) =
    1.to(size)
      .toList
      .traverse_(_ => IO(Blackhole.consumeCPU(cpuTokens)) *> simpleEitherImpl)
      .unsafeRunSync()

/*
Benchmark                                  (cpuTokens)  (size)   Mode  Cnt     Score    Error  Units
SubmarineBenchmarks.simpleEitherT                  100    1000  thrpt   45  2316.082 ± 25.633  ops/s
SubmarineBenchmarks.simpleIOHandle                 100    1000  thrpt   45  3611.171 ± 16.276  ops/s
SubmarineBenchmarks.simpleIOWithThrowable          100    1000  thrpt   45  3774.398 ± 30.061  ops/s
 */

object Error

def simpleIOHandleImpl: IO[String] =
  allow[Error.type]:
    Error.raise[IO, String].as("nope")
  .rescue:
    case Error => "error".pure

def simpleEitherTImpl: EitherT[IO, Error.type, String] =
  EitherT
    .leftT[IO, String](Error)
    .as("nope")
    .recoverWith:
      case Error => EitherT.pure[IO, Error.type]("error")

def simpleEitherImpl: IO[Either[Error.type, String]] =
    Future.success(Left(Error))
    .as("nope".asRight)
    .flatMap:
      case Left(e) => IO.pure("error".asRight)
      case r @ Right(v) => IO.pure(r)

object Error2 extends NoStackTrace
def simpleIOWithThrowableImpl: Future[String] =
  Future.fail(Error2)
    .as("nope")
    .recoverWith:
      case Error2 => Future.succeed("error")
