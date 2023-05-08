//> using scala "3.2.2"
//> using dep "org.typelevel::toolkit:latest.release"
//> using dep "org.typelevel::kittens:3.0.0"
//> using dep "dev.optics::monocle-core:3.2.0"
//> using options "-Yexplicit-nulls", "Xmas-inline 64"

import cats.*
import cats.effect.*
import cats.derived.*
import cats.syntax.all.*

trait HasId[A, Id]:
  def getId(a: A): Id

object HasId:
  given [A]: HasId[A, A] with
    def getId(a: A): A = a

enum Node[A]:
  case Leaf(value: A)
  case Branch(value: A, children: Node[A])

enum Tree[A]:
  case Mainline(node: Node[A], variations: List[Variation[A]])
  case Variation(node: Node[A])

object Hello extends IOApp.Simple:
  def run =
    IO.println("Hello World!")
