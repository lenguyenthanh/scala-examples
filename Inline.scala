//> using scala "3.3.0-RC5"

import scala.compiletime.error

inline def half(inline x: Any): Any =
  inline x match
    case x: Int => x / 2
    case x: String => x.substring(0, x.length / 2)
    case _ => error("Unknown type :p")

def half2(x: Any): Any =
  x match
    case x: Int => x / 2
    case x: String => x.substring(0, x.length / 2)

inline def doSomething(inline mode: Boolean): Unit =
  if mode then println("Mode is true")
  else if !mode then println("Mode is false")
  else error("Mode must be a known value")

trait Chef

case object FrenchChef extends Chef:
  def speakFrench = println("Bienvenue dans mon restaurant !")

case object EnglishChef extends Chef:
  def speakEnglish = println("Welcome to my restaurant!")

// getChef returns Chef, but at compile-time it will be replaced
// by one of its implementation
transparent inline def getChef(isFrench: Boolean): Chef =
  if isFrench then FrenchChef else EnglishChef

@main def Test =
  inline def n: Any = 4
  println(half(6))
  println(half("hello world"))
  println(half(n))
  // println(half2(n)) // compile but crash

  doSomething(true)
  doSomething(false)
  // val bool: Boolean = scala.util.Random.nextBoolean()
  // doSomething(bool) // compile error

  val frenchChef = getChef(true)
  frenchChef.speakFrench // Bienvenue dans mon restaurant !
  getChef(false).speakEnglish // "Welcome to my restaurant!"
