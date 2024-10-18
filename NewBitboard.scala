//> using scala 3.3.4
//> using repository https://raw.githubusercontent.com/lichess-org/lila-maven/master
//> using dep org.lichess::scalachess:15.4.3
//> using options -Ysafe-init, -feature

package bitboard

import chess.*
import NewBitboard.*
import scala.annotation.targetName

object Main:
  def main(args: Array[String]) =
    val b              = NewBitboard(0x8100000000000081L)
    val c: NewBitboard = b & NewBitboard(10L)
    val d: NewBitboard = c & b

opaque type NewBitboard = Long
object NewBitboard:

  def apply(l: Long): NewBitboard    = l
  val empty: NewBitboard             = 0L
  protected val ALL: NewBitboard     = -1L
  protected val CORNERS: NewBitboard = 0x8100000000000081L

  inline def apply(inline xs: Iterable[Square]): NewBitboard = xs.foldLeft(empty)((b, p) => b | p.bb)

  extension (l: Long)
    def bb: NewBitboard        = NewBitboard(l)
    private def lsb: Square = Square(java.lang.Long.numberOfTrailingZeros(l))

  extension (s: Square)
    inline def bb: NewBitboard = 1L << s.value

  private val RANKS = Array.fill(8)(0L)
  private val FILES = Array.fill(8)(0L)

  val firstRank: NewBitboard = 0xffL
  val lastRank: NewBitboard  = 0xffL << 56

  // all light squares
  val lightSquares: NewBitboard = 0x55aa55aa55aa55aaL
  // all dark squares
  val darkSquares: NewBitboard = 0xaa55aa55aa55aa55L

  private[bitboard] val KNIGHT_DELTAS     = Array[Int](17, 15, 10, 6, -17, -15, -10, -6)
  private[bitboard] val BISHOP_DELTAS     = Array[Int](7, -7, 9, -9)
  private[bitboard] val ROOK_DELTAS       = Array[Int](1, -1, 8, -8)
  private[bitboard] val KING_DELTAS       = Array[Int](1, 7, 8, 9, -1, -7, -8, -9)
  private[bitboard] val WHITE_PAWN_DELTAS = Array[Int](7, 9)
  private[bitboard] val BLACK_PAWN_DELTAS = Array[Int](-7, -9)

  private[bitboard] val KNIGHT_ATTACKS     = Array.fill(64)(0L)
  private[bitboard] val KING_ATTACKS       = Array.fill(64)(0L)
  private[bitboard] val WHITE_PAWN_ATTACKS = Array.fill(64)(0L)
  private[bitboard] val BLACK_PAWN_ATTACKS = Array.fill(64)(0L)

  private[bitboard] val BETWEEN = Array.ofDim[Long](64, 64)
  private[bitboard] val RAYS    = Array.ofDim[Long](64, 64)

  // Large overlapping attack table indexed using magic multiplication.
  private[bitboard] val ATTACKS = Array.fill(88772)(0L)

  inline def rank(inline r: Rank): NewBitboard                        = RANKS(r.value)
  inline def file(inline f: File): NewBitboard                        = FILES(f.value)
  inline def ray(inline from: Square, inline to: Square): NewBitboard = RAYS(from.value)(to.value)

  /** Slow attack set generation. Used only to bootstrap the attack tables.
    */
  private[bitboard] def slidingAttacks(square: Int, occupied: NewBitboard, deltas: Array[Int]): NewBitboard =
    var attacks = 0L
    deltas.foreach { delta =>
      var sq: Int = square
      var i       = 0
      while
        i += 1
        sq += delta
        val con = (sq < 0 || 64 <= sq || distance(sq, sq - delta) > 2)
        if !con then attacks |= 1L << sq

        !(occupied.contains(Square(sq)) || con)
      do ()
    }
    attacks

  private def initMagics(square: Int, magic: Magic, shift: Int, deltas: Array[Int]) =
    var subset = 0L
    while
      val attack = slidingAttacks(square, subset, deltas)
      val idx    = ((magic.factor * subset) >>> (64 - shift)).toInt + magic.offset
      ATTACKS(idx) = attack

      // Carry-rippler trick for enumerating subsets.
      subset = (subset - magic.mask) & magic.mask

      subset != 0
    do ()

  private def initialize() =
    (0 until 8).foreach { i =>
      RANKS(i) = 0xffL << (i * 8)
      FILES(i) = 0x0101010101010101L << i
    }

    val squareRange = 0 until 64
    squareRange.foreach { sq =>
      KNIGHT_ATTACKS(sq) = slidingAttacks(sq, NewBitboard.ALL, KNIGHT_DELTAS)
      KING_ATTACKS(sq) = slidingAttacks(sq, NewBitboard.ALL, KING_DELTAS)
      WHITE_PAWN_ATTACKS(sq) = slidingAttacks(sq, NewBitboard.ALL, WHITE_PAWN_DELTAS)
      BLACK_PAWN_ATTACKS(sq) = slidingAttacks(sq, NewBitboard.ALL, BLACK_PAWN_DELTAS)

      initMagics(sq, Magic.ROOK(sq), 12, ROOK_DELTAS)
      initMagics(sq, Magic.BISHOP(sq), 9, BISHOP_DELTAS)
    }

    for
      a <- squareRange
      b <- squareRange
      _ =
        if slidingAttacks(a, 0, ROOK_DELTAS).contains(Square(b)) then
          BETWEEN(a)(b) = slidingAttacks(a, 1L << b, ROOK_DELTAS) & slidingAttacks(b, 1L << a, ROOK_DELTAS)
          RAYS(a)(b) =
            (1L << a) | (1L << b) | slidingAttacks(a, 0, ROOK_DELTAS) & slidingAttacks(b, 0, ROOK_DELTAS)
        else if slidingAttacks(a, 0, BISHOP_DELTAS).contains(Square(b)) then
          BETWEEN(a)(b) =
            slidingAttacks(a, 1L << b, BISHOP_DELTAS) & slidingAttacks(b, 1L << a, BISHOP_DELTAS)
          RAYS(a)(b) =
            (1L << a) | (1L << b) | slidingAttacks(a, 0, BISHOP_DELTAS) & slidingAttacks(b, 0, BISHOP_DELTAS)
    yield ()

  initialize()

  def aligned(a: Square, b: Square, c: Square): Boolean =
    ray(a, b).contains(c)

  def between(a: Square, b: Square): NewBitboard =
    BETWEEN(a.value)(b.value)

  extension (s: Square)

    def bishopAttacks(occupied: NewBitboard): NewBitboard =
      val magic = Magic.BISHOP(s.value)
      ATTACKS(((magic.factor * (occupied & magic.mask) >>> (64 - 9)).toInt + magic.offset))

    def rookAttacks(occupied: NewBitboard): NewBitboard =
      val magic = Magic.ROOK(s.value)
      ATTACKS(((magic.factor * (occupied & magic.mask) >>> (64 - 12)).toInt + magic.offset))

    def queenAttacks(occupied: NewBitboard): NewBitboard =
      bishopAttacks(occupied) ^ rookAttacks(occupied)

    def pawnAttacks(color: Color): NewBitboard =
      color match
        case Color.White => WHITE_PAWN_ATTACKS(s.value)
        case Color.Black => BLACK_PAWN_ATTACKS(s.value)

    def kingAttacks: NewBitboard =
      KING_ATTACKS(s.value)

    def knightAttacks: NewBitboard =
      KNIGHT_ATTACKS(s.value)

  private def distance(a: Int, b: Int): Int =
    inline def file(p: Int) = p & 7
    inline def rank(p: Int) = p >>> 3
    Math.max(Math.abs(file(a) - file(b)), Math.abs(rank(a) - rank(b)))

  extension (a: NewBitboard)
    inline def value: Long = a
    inline def unary_~ : NewBitboard                  = (~a)
    // inline infix def &(inline o: Long): NewBitboard   = (a & o)
    inline infix def ^(inline o: Long): NewBitboard   = (a ^ o)
    inline infix def |(inline o: Long): NewBitboard   = (a | o)
    inline infix def <<(inline o: Long): NewBitboard  = (a << o)
    inline infix def >>>(inline o: Long): NewBitboard = (a >>> o)
    @targetName("and")
    inline infix def &(o: NewBitboard): NewBitboard = (a & o)
    @targetName("xor")
    inline infix def ^(o: NewBitboard): NewBitboard = (a ^ o)
    @targetName("or")
    inline infix def |(o: NewBitboard): NewBitboard = (a | o)
    @targetName("shiftLeft")
    inline infix def <<(o: NewBitboard): NewBitboard = (a << o)
    @targetName("shiftRight")
    inline infix def >>>(o: NewBitboard): NewBitboard = (a >>> o)


    def contains(square: Square): Boolean =
      (a & (1L << square.value)) != 0L

    def addSquare(square: Square): NewBitboard    = a | square.bb
    def removeSquare(square: Square): NewBitboard = a & ~square.bb

    def move(from: Square, to: Square): NewBitboard =
      a & ~from.bb | to.bb

    def moreThanOne: Boolean =
      (a & (a - 1L)) != 0L

    // Gets the only square in the set, if there is exactly one.
    def singleSquare: Option[Square] =
      if moreThanOne then None
      else first

    def squares: List[Square] =
      var b       = a
      val builder = List.newBuilder[Square]
      while b != 0L
      do
        builder += b.lsb
        b &= (b - 1L)
      builder.result

    // total non empty squares
    def count: Int = java.lang.Long.bitCount(a)

    // the first non empty square (the least significant bit/ the rightmost bit)
    def first: Option[Square] = Square.at(java.lang.Long.numberOfTrailingZeros(a))

    // the last non empty square (the most significant bit / the leftmost bit)
    def last: Option[Square] = Square.at(63 - java.lang.Long.numberOfLeadingZeros(a))

    // remove the first non empty position
    def removeFirst: NewBitboard = (a & (a - 1L))

    inline def intersects(inline o: Long): Boolean =
      (a & o) != 0L

    @targetName("intersectsB")
    inline def intersects[B](o: NewBitboard): Boolean =
      (a & o).nonEmpty

    inline def isDisjoint(inline o: Long): Boolean =
      (a & o).isEmpty

    @targetName("isDisjointB")
    inline def isDisjoint[B](o: NewBitboard): Boolean =
      (a & o).isEmpty

    def first[B](f: Square => Option[B]): Option[B] =
      var b                 = a
      var result: Option[B] = None
      while b != 0L && result.isEmpty
      do
        result = f(b.lsb)
        b &= (b - 1L)
      result

    def fold[B](init: B)(f: (B, Square) => B): B =
      var b      = a
      var result = init
      while b != 0L
      do
        result = f(result, b.lsb)
        b &= (b - 1L)
      result

    def filter(f: Square => Boolean): List[Square] =
      val builder = List.newBuilder[Square]
      var b       = a
      while b != 0L
      do
        if f(b.lsb) then builder += b.lsb
        b &= (b - 1L)
      builder.result

    def withFilter(f: Square => Boolean): List[Square] =
      filter(f)

    def foreach[U](f: Square => U): Unit =
      var b = a
      while b != 0L
      do
        f(b.lsb)
        b &= (b - 1L)

    def forall[B](f: Square => Boolean): Boolean =
      var b      = a
      var result = true
      while b != 0L && result
      do
        result = f(b.lsb)
        b &= (b - 1L)
      result

    def exists[B](f: Square => Boolean): Boolean =
      var b      = a
      var result = false
      while b != 0L && !result
      do
        result = f(b.lsb)
        b &= (b - 1L)
      result

    def flatMap[B](f: Square => IterableOnce[B]): List[B] =
      var b       = a
      val builder = List.newBuilder[B]
      while b != 0L
      do
        builder ++= f(b.lsb)
        b &= (b - 1L)
      builder.result

    def map[B](f: Square => B): List[B] =
      var b       = a
      val builder = List.newBuilder[B]
      while b != 0L
      do
        builder += f(b.lsb)
        b &= (b - 1L)
      builder.result

    def isEmpty: Boolean  = a == empty
    def nonEmpty: Boolean = !isEmpty


case class Magic(mask: Long, factor: Long, offset: Int)

object Magic:
  val ROOK = Array[Magic](
    Magic(0x000101010101017eL, 0x00280077ffebfffeL, 26304),
    Magic(0x000202020202027cL, 0x2004010201097fffL, 35520),
    Magic(0x000404040404047aL, 0x0010020010053fffL, 38592),
    Magic(0x0008080808080876L, 0x0040040008004002L, 8026),
    Magic(0x001010101010106eL, 0x7fd00441ffffd003L, 22196),
    Magic(0x002020202020205eL, 0x4020008887dffffeL, 80870),
    Magic(0x004040404040403eL, 0x004000888847ffffL, 76747),
    Magic(0x008080808080807eL, 0x006800fbff75fffdL, 30400),
    Magic(0x0001010101017e00L, 0x000028010113ffffL, 11115),
    Magic(0x0002020202027c00L, 0x0020040201fcffffL, 18205),
    Magic(0x0004040404047a00L, 0x007fe80042ffffe8L, 53577),
    Magic(0x0008080808087600L, 0x00001800217fffe8L, 62724),
    Magic(0x0010101010106e00L, 0x00001800073fffe8L, 34282),
    Magic(0x0020202020205e00L, 0x00001800e05fffe8L, 29196),
    Magic(0x0040404040403e00L, 0x00001800602fffe8L, 23806),
    Magic(0x0080808080807e00L, 0x000030002fffffa0L, 49481),
    Magic(0x00010101017e0100L, 0x00300018010bffffL, 2410),
    Magic(0x00020202027c0200L, 0x0003000c0085fffbL, 36498),
    Magic(0x00040404047a0400L, 0x0004000802010008L, 24478),
    Magic(0x0008080808760800L, 0x0004002020020004L, 10074),
    Magic(0x00101010106e1000L, 0x0001002002002001L, 79315),
    Magic(0x00202020205e2000L, 0x0001001000801040L, 51779),
    Magic(0x00404040403e4000L, 0x0000004040008001L, 13586),
    Magic(0x00808080807e8000L, 0x0000006800cdfff4L, 19323),
    Magic(0x000101017e010100L, 0x0040200010080010L, 70612),
    Magic(0x000202027c020200L, 0x0000080010040010L, 83652),
    Magic(0x000404047a040400L, 0x0004010008020008L, 63110),
    Magic(0x0008080876080800L, 0x0000040020200200L, 34496),
    Magic(0x001010106e101000L, 0x0002008010100100L, 84966),
    Magic(0x002020205e202000L, 0x0000008020010020L, 54341),
    Magic(0x004040403e404000L, 0x0000008020200040L, 60421),
    Magic(0x008080807e808000L, 0x0000820020004020L, 86402),
    Magic(0x0001017e01010100L, 0x00fffd1800300030L, 50245),
    Magic(0x0002027c02020200L, 0x007fff7fbfd40020L, 76622),
    Magic(0x0004047a04040400L, 0x003fffbd00180018L, 84676),
    Magic(0x0008087608080800L, 0x001fffde80180018L, 78757),
    Magic(0x0010106e10101000L, 0x000fffe0bfe80018L, 37346),
    Magic(0x0020205e20202000L, 0x0001000080202001L, 370),
    Magic(0x0040403e40404000L, 0x0003fffbff980180L, 42182),
    Magic(0x0080807e80808000L, 0x0001fffdff9000e0L, 45385),
    Magic(0x00017e0101010100L, 0x00fffefeebffd800L, 61659),
    Magic(0x00027c0202020200L, 0x007ffff7ffc01400L, 12790),
    Magic(0x00047a0404040400L, 0x003fffbfe4ffe800L, 16762),
    Magic(0x0008760808080800L, 0x001ffff01fc03000L, 0),
    Magic(0x00106e1010101000L, 0x000fffe7f8bfe800L, 38380),
    Magic(0x00205e2020202000L, 0x0007ffdfdf3ff808L, 11098),
    Magic(0x00403e4040404000L, 0x0003fff85fffa804L, 21803),
    Magic(0x00807e8080808000L, 0x0001fffd75ffa802L, 39189),
    Magic(0x007e010101010100L, 0x00ffffd7ffebffd8L, 58628),
    Magic(0x007c020202020200L, 0x007fff75ff7fbfd8L, 44116),
    Magic(0x007a040404040400L, 0x003fff863fbf7fd8L, 78357),
    Magic(0x0076080808080800L, 0x001fffbfdfd7ffd8L, 44481),
    Magic(0x006e101010101000L, 0x000ffff810280028L, 64134),
    Magic(0x005e202020202000L, 0x0007ffd7f7feffd8L, 41759),
    Magic(0x003e404040404000L, 0x0003fffc0c480048L, 1394),
    Magic(0x007e808080808000L, 0x0001ffffafd7ffd8L, 40910),
    Magic(0x7e01010101010100L, 0x00ffffe4ffdfa3baL, 66516),
    Magic(0x7c02020202020200L, 0x007fffef7ff3d3daL, 3897),
    Magic(0x7a04040404040400L, 0x003fffbfdfeff7faL, 3930),
    Magic(0x7608080808080800L, 0x001fffeff7fbfc22L, 72934),
    Magic(0x6e10101010101000L, 0x0000020408001001L, 72662),
    Magic(0x5e20202020202000L, 0x0007fffeffff77fdL, 56325),
    Magic(0x3e40404040404000L, 0x0003ffffbf7dfeecL, 66501),
    Magic(0x7e80808080808000L, 0x0001ffff9dffa333L, 14826)
  )

  val BISHOP = Array[Magic](
    Magic(0x0040201008040200L, 0x007fbfbfbfbfbfffL, 5378),
    Magic(0x0000402010080400L, 0x0000a060401007fcL, 4093),
    Magic(0x0000004020100a00L, 0x0001004008020000L, 4314),
    Magic(0x0000000040221400L, 0x0000806004000000L, 6587),
    Magic(0x0000000002442800L, 0x0000100400000000L, 6491),
    Magic(0x0000000204085000L, 0x000021c100b20000L, 6330),
    Magic(0x0000020408102000L, 0x0000040041008000L, 5609),
    Magic(0x0002040810204000L, 0x00000fb0203fff80L, 22236),
    Magic(0x0020100804020000L, 0x0000040100401004L, 6106),
    Magic(0x0040201008040000L, 0x0000020080200802L, 5625),
    Magic(0x00004020100a0000L, 0x0000004010202000L, 16785),
    Magic(0x0000004022140000L, 0x0000008060040000L, 16817),
    Magic(0x0000000244280000L, 0x0000004402000000L, 6842),
    Magic(0x0000020408500000L, 0x0000000801008000L, 7003),
    Magic(0x0002040810200000L, 0x000007efe0bfff80L, 4197),
    Magic(0x0004081020400000L, 0x0000000820820020L, 7356),
    Magic(0x0010080402000200L, 0x0000400080808080L, 4602),
    Magic(0x0020100804000400L, 0x00021f0100400808L, 4538),
    Magic(0x004020100a000a00L, 0x00018000c06f3fffL, 29531),
    Magic(0x0000402214001400L, 0x0000258200801000L, 45393),
    Magic(0x0000024428002800L, 0x0000240080840000L, 12420),
    Magic(0x0002040850005000L, 0x000018000c03fff8L, 15763),
    Magic(0x0004081020002000L, 0x00000a5840208020L, 5050),
    Magic(0x0008102040004000L, 0x0000020008208020L, 4346),
    Magic(0x0008040200020400L, 0x0000804000810100L, 6074),
    Magic(0x0010080400040800L, 0x0001011900802008L, 7866),
    Magic(0x0020100a000a1000L, 0x0000804000810100L, 32139),
    Magic(0x0040221400142200L, 0x000100403c0403ffL, 57673),
    Magic(0x0002442800284400L, 0x00078402a8802000L, 55365),
    Magic(0x0004085000500800L, 0x0000101000804400L, 15818),
    Magic(0x0008102000201000L, 0x0000080800104100L, 5562),
    Magic(0x0010204000402000L, 0x00004004c0082008L, 6390),
    Magic(0x0004020002040800L, 0x0001010120008020L, 7930),
    Magic(0x0008040004081000L, 0x000080809a004010L, 13329),
    Magic(0x00100a000a102000L, 0x0007fefe08810010L, 7170),
    Magic(0x0022140014224000L, 0x0003ff0f833fc080L, 27267),
    Magic(0x0044280028440200L, 0x007fe08019003042L, 53787),
    Magic(0x0008500050080400L, 0x003fffefea003000L, 5097),
    Magic(0x0010200020100800L, 0x0000101010002080L, 6643),
    Magic(0x0020400040201000L, 0x0000802005080804L, 6138),
    Magic(0x0002000204081000L, 0x0000808080a80040L, 7418),
    Magic(0x0004000408102000L, 0x0000104100200040L, 7898),
    Magic(0x000a000a10204000L, 0x0003ffdf7f833fc0L, 42012),
    Magic(0x0014001422400000L, 0x0000008840450020L, 57350),
    Magic(0x0028002844020000L, 0x00007ffc80180030L, 22813),
    Magic(0x0050005008040200L, 0x007fffdd80140028L, 56693),
    Magic(0x0020002010080400L, 0x00020080200a0004L, 5818),
    Magic(0x0040004020100800L, 0x0000101010100020L, 7098),
    Magic(0x0000020408102000L, 0x0007ffdfc1805000L, 4451),
    Magic(0x0000040810204000L, 0x0003ffefe0c02200L, 4709),
    Magic(0x00000a1020400000L, 0x0000000820806000L, 4794),
    Magic(0x0000142240000000L, 0x0000000008403000L, 13364),
    Magic(0x0000284402000000L, 0x0000000100202000L, 4570),
    Magic(0x0000500804020000L, 0x0000004040802000L, 4282),
    Magic(0x0000201008040200L, 0x0004010040100400L, 14964),
    Magic(0x0000402010080400L, 0x00006020601803f4L, 4026),
    Magic(0x0002040810204000L, 0x0003ffdfdfc28048L, 4826),
    Magic(0x0004081020400000L, 0x0000000820820020L, 7354),
    Magic(0x000a102040000000L, 0x0000000008208060L, 4848),
    Magic(0x0014224000000000L, 0x0000000000808020L, 15946),
    Magic(0x0028440200000000L, 0x0000000001002020L, 14932),
    Magic(0x0050080402000000L, 0x0000000401002008L, 16588),
    Magic(0x0020100804020000L, 0x0000004040404040L, 6905),
    Magic(0x0040201008040200L, 0x007fff9fdf7ff813L, 16076)
  )
