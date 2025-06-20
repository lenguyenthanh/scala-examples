//> using scala 3.7.1
//> using options -Wunused:all

sealed abstract class Tree[+A] {
  def leafs: List[A]
  def value: A
}

extension [A](tree: Tree[A])
   def fist: A = second
   private def second: A = thrid
   private def thrid: A = null.asInstanceOf[A] // This is a false positive, as `thrid` is not used.


// // An external class that doesn't get its own `copy` method.
// class Foo(val a: String, val b: Int)
//
// //
// // Example 1: add `copy` method via an extension method.
// //
// extension (self: Foo)
//   def copy(a: String = self.a, b: Int = self.b): Foo = Foo(a, b)
//
// //
// // Example 2: implement `copyFoo` with parameter groups.
// //
// def copyFoo(foo: Foo)(a: String = foo.a, b: Int = foo.b): Foo = Foo(a, b)
