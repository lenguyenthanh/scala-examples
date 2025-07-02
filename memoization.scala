//> using scala 3.7.1
//> using dep org.typelevel::cats-effect:3.6.1

import cats.effect.*
import cats.syntax.all.*

object Main extends IOApp.Simple {

  def run: IO[Unit] =
    // A simple memoization function
    val fibonacci : Int => Int = {
      val cache = scala.collection.mutable.Map.empty[Int, Int]

      def real(n: Int): Int = {
        if (n <= 1) n
        else real(n - 1) + real(n - 2)
      }

      (x: Int) => {
        println(s"Calculating value for $x $cache")
        cache.getOrElseUpdate(x, real(x))
      }
    }

    // Testing the memoized function
    List.range(0, 10).traverse_ { n =>
      IO(println(s"Fibonacci($n) = ${fibonacci(n)}"))
    }
}
