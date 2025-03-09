//> using scala 3.nightly

// package adhoc

object opaque:

  def adHoc[E]: AdHocSyntax[E] =
    AdHoc.apply[E]

  def adHocClass[E]: AdHocClass[E] =
    AdHocClass[E]()

  opaque type AdHoc[E] = Unit

  inline trait AdHocSyntax[A]:
    opaque type AdHoc[E] = Unit

  private object AdHoc:
    def apply[E]: AdHoc[E] = ()
    extension [E](adhoc: AdHoc[E])
      def print(): Unit = println("Adhoc")


  class AdHocClass[E]:
    def print(): Unit = println("AdhocClass")

object test:
  import opaque.*

  def test() =
    adHoc.print

    adHocClass[String].print()
