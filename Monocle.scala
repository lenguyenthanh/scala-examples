//> using scala "3.3.0-RC5"
//> using dep "org.typelevel::toolkit:latest.release"
//> using dep "dev.optics::monocle-core:3.2.0"
//> using dep "dev.optics::monocle-macro:3.2.0"
//> using dep "org.typelevel::kittens:3.0.0"

import cats.*
import cats.derived.*
import cats.syntax.all.*
import cats.effect.*
import monocle.syntax.all.*
import monocle.macros.GenLens

sealed abstract class Tree[A](value: A, child: Option[Node[A]]) derives Functor, Traverse

final case class Node[A](
    val value: A,
    val child: Option[Node[A]] = None,
    variations: List[Variation[A]] = Nil
) extends Tree[A](value, child)
    derives Functor,
      Traverse

final case class Variation[A](val value: A, val child: Option[Node[A]] = None) extends Tree[A](value, child)
    derives Functor,
      Traverse

object Tree:
  def value[A] = GenLens[Tree[A]](_.value)

object Hello extends IOApp.Simple:
  def run =
    val node = Node(1, Some(Node(2)))
    IO.println(Tree.value.get(node))
