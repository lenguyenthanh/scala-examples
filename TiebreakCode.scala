import scala.compiletime.{erasedValue, summonInline, constValueTuple}
import scala.deriving.Mirror

// type Code = "BPG" | "BH" | "KS"
//
// extension (code: Code)
//   inline def value: String = code
//
// object Code:
//   import scala.compiletime._
//
//   inline def summonAllUnion[A]: List[A] =
//     inline erasedValue[A] match
//       case _: (a | b) => (constValue[a] :: summonAllUnion[b]).asInstanceOf[List[A]]
//       case _: Nothing => Nil
//       case _          => List(constValue[A])
//
//   val all: List[Code] = summonAllUnion[Code]
//
import scala.compiletime.*
import scala.Tuple

// 1. Only maintain your codes here
type CodeTuple = ("BPG", "BH", "KS")

// 2. Derive the union type from the tuple:
type Code = CodeTupleToUnion[CodeTuple]

// 3. Type-level utility to convert tuple of singletons to their union
type CodeTupleToUnion[T <: Tuple] = T match
  case EmptyTuple => Nothing
  case h *: t => h | CodeTupleToUnion[t]

extension (code: Code)
  inline def value: String = code

object Code:
  // Macro to enumerate tuple elements
  inline def allTuple[T <: Tuple]: List[Any] =
    inline erasedValue[T] match
      case _: (h *: t) => constValue[h] :: allTuple[t]
      case _: EmptyTuple => Nil

  val all: List[Code] = allTuple[CodeTuple].asInstanceOf[List[Code]]

@main def demo = println(Code.all) // Output: List(BPG, BH, KS)
