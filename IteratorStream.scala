//> using scala 3.5.1
//> using toolkit typelevel:latest

import cats.effect.{ IO, IOApp, Sync, Temporal }
import cats.syntax.all.*
import concurrent.duration.*
import scala.jdk.CollectionConverters.*

def main =
  val reader = new java.io.BufferedReader(new java.io.FileReader("build.sbt"))
  fs2.Stream.fromBlockingIterator[IO](reader.lines().iterator().asScala, 4096)
