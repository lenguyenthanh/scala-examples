abstract class A:
  val message: String
class B extends A:
  val message = "I'm an instance of class B"
trait C extends A:
  def loudMessage = message.toUpperCase()
class D extends B, C

val d = D()
println(d.message)  // I'm an instance of class B
println(d.loudMessage)  // I'M AN INSTANCE OF CLASS B
