//> using scala 3.3.0
//> using toolkit typelevel:latest
//> using dep "dev.optics::monocle-core:3.2.0"
//> using dep "org.typelevel::kittens:3.0.0"

import cats.*
import cats.effect.*
import cats.derived.*
import cats.syntax.all.*

trait HasId[A, Id]:
  def getId(a: A): Id

object HasId:
  given [A]: HasId[A, A] with
    def getId(a: A): A = a

trait Tree[A]:
  def value: A
  def child: Option[Tree[A]]
  def variations: List[Variation[A]]

trait TreeLike[A, F[_], T]
case class Node[A](value: A, child: Option[Node[A]], variations: List[Variation[A]])
    extends TreeLike[A, Tree, Node[A]]
case class Variation[A](value: A, child: Option[Node[A]]) extends TreeLike[A, Tree, Variation[A]]

object Hello extends IOApp.Simple:
  def run =
    IO.println("Hello World!")
