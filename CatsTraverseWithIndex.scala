//> using scala 3.3.1
//> using toolkit typelevel:latest

package bench
import cats.syntax.all.*
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import org.openjdk.jmh.infra.Blackhole

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.Throughput))
@OutputTimeUnit(TimeUnit.SECONDS)
@Measurement(iterations = 15, timeUnit = TimeUnit.SECONDS, time = 3)
@Warmup(iterations = 15, timeUnit = TimeUnit.SECONDS, time = 3)
@Fork(3)
@Threads(value = 1)
class CatsTraverseWithIndex:

  private[this] val Work: Long = 2
  @Param(Array("50", "1000", "100000", "10000000"))
  var size: Int = _

  var xs: List[Int] = _

  @Setup
  def setup =
    xs = (1 to size).toList

  @Benchmark
  def traverseWithIndexM =
    xs.traverseWithIndexM: (x, i) =>
      Blackhole.consumeCPU(Work)
      (x + i).some

  @Benchmark
  def zipMapSequence =
    xs.zipWithIndex.map: (x, i) =>
      Blackhole.consumeCPU(Work)
      (x + i).some
    .sequence

  @Benchmark
  def zipTraverse =
    xs.zipWithIndex.traverse: (x, i) =>
      Blackhole.consumeCPU(Work)
      (x + i).some
