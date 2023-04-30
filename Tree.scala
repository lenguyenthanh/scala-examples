//> using scala "3.3.0-RC5"
//> using dep "org.typelevel::toolkit:latest.release"
//> using dep "org.typelevel::kittens:3.0.0"
//> using dep "dev.optics::monocle-core:3.2.0"
//> using options "-Yexplicit-nulls"

import cats.*
import cats.effect.*
import cats.derived.*
import cats.syntax.all.*

trait HasId[A, Id]:
  def getId(a: A): Id

object HasId:
  given [A]: HasId[A, A] with
    def getId(a: A): A = a

sealed trait Tree[A]:

// sealed abstract class Tree[A](value: A, child: Option[Node[A]]) derives Functor, Traverse:
//   def mainline: List[Tree[A]]                     = this :: child.fold(List.empty[Tree[A]])(_.mainline)
//   def hasId[Id](id: Id)(using h: HasId[A, Id]): Boolean = h.getId(value) == id
  def modifyAt[Id](path: List[Id], f: TreeModifier[A])(using h: HasId[A, Id]): Option[IsTree[A, this.type]]

object Tree:
  // def f(node: Tree[A]) =
  def f[A]: TreeModifier[A] = node =>
    node match
      case n: Node[A]      => n
      case v: Variation[A] => v

  val node = Node(1, Some(Node(2)))
  def modifyAt[A, Id](tree: Tree[A], path: List[Id], f: TreeModifier[A])(using
      h: HasId[A, Id]
  ): IsTree[A, tree.type] = ???
  val x: Node[Int] = modifyAt(Node(1, Some(Node(2))), List(1), f)
  summon[IsTree[Int, Node[Int]] =:= Node[Int]]
  summon[IsTree[Int, Variation[Int]] =:= Variation[Int]]
  // summon[IsTree[Int, Node[Int]] =:= Variation[Int]]
  f(node)

type IsTree[A, X <: Tree[A]] = X match
  case Node[A]      => Node[A]
  case Variation[A] => Variation[A]

// type Tree[A]         = Node[A] | Variation[A]
type TreeModifier[A] = (tree: Tree[A]) => IsTree[A, tree.type]

final case class Node[A](
    value: A,
    child: Option[Node[A]] = None,
    variations: List[Variation[A]] = Nil
) extends Tree[A] derives Functor,
      Traverse:
  def mainline: List[Tree[A]]                           = this :: child.fold(List.empty[Tree[A]])(_.mainline)
  def hasId[Id](id: Id)(using h: HasId[A, Id]): Boolean = h.getId(value) == id
  def toVariation: Variation[A]                         = Variation(value, child)

  override def modifyAt[Id](path: List[Id], f: TreeModifier[A])(using h: HasId[A, Id]): Option[Node[A]] =
    path match
      case Nil                        => none[Node[A]]
      case head :: Nil if hasId(head) => f(this: Node[A]).some
      case head :: rest if hasId(head) =>
        child.flatMap(_.modifyAt(rest, f)) match
          case None    => None
          case Some(c) => copy(child = c.some).some
      case _ =>
        variations.foldLeft((false, List.empty[Variation[A]])) {
          case ((true, acc), n) => (true, acc :+ n)
          case ((false, acc), n) =>
            n.modifyAt(path, f) match
              case Some(nn) => (true, acc :+ nn)
              case None     => (false, acc :+ n)
        } match
          case (true, ns) => copy(variations = ns).some
          case (false, _) => none

final case class Variation[A](value: A, child: Option[Node[A]] = None) extends Tree[A] derives Functor, Traverse:
  def toNode: Node[A]                                   = Node(value, child)
  def mainline: List[Tree[A]]                           = this :: child.fold(List.empty[Tree[A]])(_.mainline)
  def hasId[Id](id: Id)(using h: HasId[A, Id]): Boolean = h.getId(value) == id
  override def modifyAt[Id](path: List[Id], f: TreeModifier[A])(using h: HasId[A, Id]): Option[Variation[A]] = ???

type IntTree = Node[Int]

object Hello extends IOApp.Simple:
  val node: Node[Int] = Node(1, Some(Node(2, Some(Node(3)))), List(Variation(4, Some(Node(5)))))
  def run =
    IO.println(node) >>
      IO.println(node.mainline) >>
      IO.println(node.size)
