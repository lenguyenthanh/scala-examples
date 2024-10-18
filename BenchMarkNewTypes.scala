//> using scala 3.3.4
//> using repository https://raw.githubusercontent.com/lichess-org/lila-maven/master
//> using dep org.lichess::scalachess:15.4.3
//> using options -Ysafe-init, -feature

package bench

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

import chess.Square
import chess.bitboard.Bitboard.*
import chess.bitboard.Bitboard
import bitboard.NewBitboard.*
import bitboard.NewBitboard

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.Throughput))
@OutputTimeUnit(TimeUnit.SECONDS)
@Measurement(iterations = 15, timeUnit = TimeUnit.SECONDS, time = 3)
@Warmup(iterations = 15, timeUnit = TimeUnit.SECONDS, time = 3)
@Fork(3)
@Threads(value = 1)
class BenchMarkNewTypes:

  // the unit of CPU work per iteration
  private[this] val Work: Long = 10

  @Param(Array("5"))
  var size: Int = _

  var bs: List[Square] = _

  @Setup(Level.Trial)
  def setup() =
    bs = List.range(0, size).flatMap(Square.at(_))

  @Benchmark
  def initCurrentBitboard =
    Bitboard(bs)

  @Benchmark
  def initNewBitboard =
    NewBitboard(bs)
