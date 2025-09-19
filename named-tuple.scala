//> using scala 3.7.0-RC1

// import Tuple.*
import scala.NamedTuple.withNames
import scala.deriving.Mirror
// import scala.compiletime.*
// import NamedTuple.NamedTuple

type ageT = (age: Int)
type nameT = (name: String)
type nameAndAgeT = NamedTuple.Concat[nameT, ageT]

val age = (age = 30)
val name = (name = "Alice")
val an: Person = name ++ age

type Person = (name: String, age: Int)
type x = (first: String, second: Int)
// type y = x ++ Person
type PersonForm = NamedTuple.Map[Person, Option]

val p1: Person = ("Alice", 30)
val p2: Person = ("Alice", 30).withNames[("name", "age")]
val p3 = summon[Mirror.Of[Person]].fromProduct(p1.toTuple)
val p4 = summon[Mirror.Of[nameAndAgeT]].fromProduct(p2.toTuple)

// val f1: PersonForm = (name = Some("Alice"), age = None)

@main def run =

  println(p4.name)
  println(p4.age)
  println(p2.name)
  println(an.name)
