//> using scala 3.4.2
//> using dep "org.typelevel::cats-core:2.12.0"
//> using dep "org.typelevel::alleycats-core:2.12.0"

import scala.concurrent.{Future,Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*
import cats.syntax.all.*

object main extends App:
  val all = List(150, 50, 100, 20).zipWithIndex.traverse: (ms, i) =>
    println(s"Starting $i, sleeping for $ms ms...")
    Future {
      Thread.sleep(ms)
      println(s"Finished $i, slept for $ms ms")
      i
    }
  val result = Await.result(all, 1.second)
  println(s"Result: $result")
