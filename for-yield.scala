//> using scala 3.3.6
//> using options -explain

def fetch(s: String)(s2: String): Option[Int] = ???

object main:
  for
    response <- fetch("hello"):
      "world"
  yield response
