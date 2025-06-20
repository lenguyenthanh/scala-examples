//> using scala 3.7.0
//> using dep "org.typelevel::cats-core:2.13.0"

import cats.Show
import cats.syntax.all.*

trait Differ[A] {
  def diff(a: A, b: A): DiffResult
}

sealed trait DiffResult

object DiffResult:

  case object NoDiff extends DiffResult

  // scalar
  case class ValueDiff[A](original: A, modified: A) extends DiffResult

  sealed trait OptionDiff[+A] extends DiffResult

  object OptionDiff:
    case class Removed[A](original: A) extends OptionDiff[A]
    case class Added[A](modified: A) extends OptionDiff[A]
    case class Modified(diff: DiffResult) extends OptionDiff[Nothing]

  case class ProductDiff(diff: Map[String, DiffResult]) extends DiffResult

  sealed trait SumDiff extends DiffResult

  object SumDiff:
    case class CaseChanged[A](original: A, modified: A) extends SumDiff
    case class Updated(result: DiffResult) extends SumDiff

  given Show[DiffResult]:
    def show(diff: DiffResult): String =
      diff match
        case NoDiff => "NoDiff"
        case ValueDiff(original, modified) =>
          s"ValueDiff(original = $original, modified = $modified)"
        case OptionDiff.Removed(original) =>
          s"OptionDiff.Removed(original = $original)"
        case OptionDiff.Added(modified) =>
          s"OptionDiff.Added(modified = $modified)"
        case OptionDiff.Modified(diff) =>
          s"OptionDiff.Modified(diff = ${diff.show})"
        case ProductDiff(diff) =>
          s"ProductDiff(diff = ${diff.map { case (k, v) => s"$k -> ${v.show}" }.mkString(", ")})"
        case SumDiff.CaseChanged(original, modified) =>
          s"SumDiff.CaseChanged(original = $original, modified = $modified)"
        case SumDiff.Updated(result) =>
          s"SumDiff.Updated(result = ${result.show})"

trait DifferInstances:

  import DiffResult.*
  // import DiffResult.OptionDiff.*

  given Differ[Int]:
    def diff(a: Int, b: Int): DiffResult =
      if a == b then NoDiff else ValueDiff(a, b)

  given Differ[Boolean]:
    def diff(a: Boolean, b: Boolean): DiffResult =
      if a == b then NoDiff else ValueDiff(a, b)

  given Differ[String]:
    def diff(a: String, b: String): DiffResult =
      if a == b then NoDiff else ValueDiff(a, b)

  given [A: Differ] => Differ[Option[A]]:
    def diff(a: Option[A], b: Option[A]): DiffResult =
      (a, b) match
        case (None, None) => NoDiff
        case (Some(a), None) => OptionDiff.Removed(a)
        case (None, Some(b)) => OptionDiff.Added(b)
        case (Some(a), Some(b)) =>
          summon[Differ[A]].diff(a, b) match
            case NoDiff => NoDiff
            case diff => OptionDiff.Modified(diff)

  case class Address(street: String, isCity: Option[Boolean])
  case class Person(id: Int, address: Option[Address])

  given Differ[Address]:
    def diff(a: Address, b: Address): DiffResult =
      val streetDiff = summon[Differ[String]].diff(a.street, b.street)
      val isCityDiff = summon[Differ[Option[Boolean]]].diff(a.isCity, b.isCity)

      (streetDiff, isCityDiff) match
        case (NoDiff, NoDiff) => NoDiff
        case _ =>
          val diffs = Map(
            "street" -> streetDiff,
            "isCity" -> isCityDiff
          ).collect { case (k, v) if v != NoDiff => k -> v }
          ProductDiff(diffs)

  given Differ[Person]:
    def diff(a: Person, b: Person): DiffResult =
      val idDiff = summon[Differ[Int]].diff(a.id, b.id)
      val addressDiff = summon[Differ[Option[Address]]].diff(a.address, b.address)

      (idDiff, addressDiff) match
        case (NoDiff, NoDiff) => NoDiff
        case _ =>
          val diffs = Map(
            "id" -> idDiff,
            "address" -> addressDiff
          ).collect { case (k, v) if v != NoDiff => k -> v }
          ProductDiff(diffs)

object DifferInstances extends DifferInstances

@main def main(): Unit =
  import DifferInstances.{given, *}

  val person1 = Person(1, Some(Address("123 Main St", Some(true))))
  val person2 = Person(1, Some(Address("123 Main St", Some(false))))

  val diff = summon[Differ[Person]].diff(person1, person2)
  println(diff.show)

  // Example of OptionDiff
  val optionDiff = summon[Differ[Option[Int]]].diff(Some(1), None)
  println(optionDiff.show)
