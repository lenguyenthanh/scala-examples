//> using scala 3.nightly
//> using options -Wall

enum Foo:
  case Bar(i: Int)
case class Container(foo: Foo)

@main def main = Container(Foo.Bar(1)) match {
  case Container(Foo.Bar) => println("yes")
  case _ => println("no")
}
