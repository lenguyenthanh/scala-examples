//> using scala 3.7.1
//> using repository jitpack
//> using dep com.github.lichess-org.scalachess::scalachess:17.9.0
//> using dep com.github.lichess-org.scalachess::scalachess-rating:17.9.0
//> using dep org.typelevel::cats-core:2.13.0
import cats.*
import cats.syntax.all.*
import Tiebreak.*

// type Tiebreak = Tiebreak_[?]
sealed abstract class Tiebreak:
  type A <: Tiebreak.Code
  def code: A
  def compute(tour: Tournament): Unit
  def modifier: TiebreakModifier[A]

case object NbBlackGames extends Tiebreak:
  type A = "BPG"
  def code: A = "BPG"
  override def modifier = ()
  override def compute(tour: Tournament) = ()

case class Buchholz(cutModifier: CutModifier) extends Tiebreak:
  type A = "BH"
  def code: A = "BH"
  override def modifier = cutModifier
  override def compute(tour: Tournament) = ()

case class KoyaSystem(limitModifier: LimitModifier) extends Tiebreak:
  type A = "KS"
  def code: A = "KS"
  override def modifier = limitModifier
  override def compute(tour: Tournament) = ()

case class CutModifier(top: Int, bottom: Int)
opaque type LimitModifier = Int
object LimitModifier:
  def apply(value: Int): LimitModifier = value
  val zero: LimitModifier = 0

object Tiebreak:
  type Code = "BPG" | "BH" | "KS"


  extension (code: Code)
    inline def value: String = code

  type PlayerPoints = Float

  object Code:
    import scala.compiletime.*

    inline def summonAllValues[T <: String]: List[T] =
      inline erasedValue[T] match
        case _: (a | b) =>
          // Split the union type into its parts recursively
          val tuple = constValueTuple[T].asInstanceOf[Product]
          tuple.productIterator.toList.asInstanceOf[List[T]]
        case _: String =>
          List(constValue[T])

    val all: List[Code] = summonAllValues[Code]

    println(all)
    // val all: Set[Code] =
    //   Set("BPG", "BH", "KS")
    //
    // def fromString(str: String): Option[Code] =
    //   all.find(_ == str)
    //
  def apply[F[_]: Applicative, A <: Code](
      code: A,
      mkCutModifier: => F[CutModifier],
      mkLimitModifier: => F[LimitModifier]
  ): F[Tiebreak] =
    code match
      case "BPG"  => NbBlackGames.pure[F]
      case "BH"   => mkCutModifier.map(Buchholz.apply)
      case "KS"   => mkLimitModifier.map(KoyaSystem.apply)

  type TiebreakModifier[A <: Tiebreak.Code] = A match
    case "BH" => CutModifier
    case "KS"                              => LimitModifier
    case _                                 => Unit

  def all: List[Tiebreak] =
    List(
      NbBlackGames,
      Buchholz(CutModifier(0, 0)),
      KoyaSystem(LimitModifier.zero)
    )

type Tournament = Unit

