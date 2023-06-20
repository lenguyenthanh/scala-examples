//> using scala "3.3.0"
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

sealed abstract class Tree[A](val value: A, val child: Option[Node[A]]):

  def withValue(value: A): TreeSelector[A, this.type] = this match
    case n: Node[A]      => n.copy(value = value)
    case v: Variation[A] => v.copy(value = value)

  def mainline: List[Tree[A]]                           = this :: child.fold(List.empty[Tree[A]])(_.mainline)
  def hasId[Id](id: Id)(using h: HasId[A, Id]): Boolean = h.getId(value) == id
  def modifyAt[Id](path: List[Id], f: TreeModifier[A])(using h: HasId[A, Id]): Option[TreeSelector[A, this.type]]

  // find node in the mainline
  def findInMainline(predicate: A => Boolean): Option[Tree[A]] =
    if predicate(value) then this.some
    else
      child.fold(none[Node[A]]): c =>
        if predicate(c.value) then c.some
        else c.findInMainline(predicate)

object Tree:

  given HasId[Int, Int] with
    def getId(a: Int): Int = a

  def f[A]: TreeModifier[A] = node =>
    node match
      case n: Node[A]      => n
      case v: Variation[A] => v

  def lift[A](f: A => A): TreeModifier[A] = tree => tree.withValue(f(tree.value))

  // def lift[A, B](f: A => B): (tree: Tree[A]) => IsTree[B, Tree[B]] = tree =>
  //   tree match
  //     case n: Node[A]      => n.map(f)
  //     case v: Variation[A] => v.map(f)

  val node = Node(1, Some(Node(2)))
  def modifyAt[A, Id](tree: Tree[A], path: List[Id], f: TreeModifier[A])(using
      h: HasId[A, Id]
  ): TreeSelector[A, tree.type] = ???
  val x: Node[Int] = modifyAt(Node(1, Some(Node(2))), List(1), f)
  summon[TreeSelector[Int, Node[Int]] =:= Node[Int]]
  summon[TreeSelector[Int, Variation[Int]] =:= Variation[Int]]
  // summon[IsTree[Int, Node[Int]] =:= Variation[Int]]
  f(node)

type TreeSelector[A, X <: Tree[A]] <: Tree[A]= X match
  case Node[A]      => Node[A]
  case Variation[A] => Variation[A]

// type Tree[A]         = Node[A] | Variation[A]
type TreeModifier[A] = (tree: Tree[A]) => TreeSelector[A, tree.type]

final case class Node[A](
    override val value: A,
    override val child: Option[Node[A]] = None,
    variations: List[Variation[A]] = Nil
) extends Tree[A](value, child)
    derives Functor,
      Traverse:
  def toVariation: Variation[A] = Variation(value, child)

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

  def modifyInMainline(predicate: A => Boolean, f: Node[A] => Node[A]): Option[Node[A]] =
    if predicate(value) then f(this).some
    else
      child.fold(none[Node[A]]): c =>
        if predicate(c.value) then c.modifyInMainline(predicate, f)
        else c.modifyInMainline(predicate, f)

final case class Variation[A](override val value: A, override val child: Option[Node[A]] = None) extends Tree[A](value, child)
    derives Functor,
      Traverse:
  def toNode: Node[A] = Node(value, child)
  override def modifyAt[Id](path: List[Id], f: TreeModifier[A])(using h: HasId[A, Id]): Option[Variation[A]] = ???

type IntTree = Node[Int]

object Hello extends IOApp.Simple:
  val node: Node[Int] = Node(1, Some(Node(2, Some(Node(3)))), List(Variation(4, Some(Node(5)))))
  val xs: List[Tree[Int]] = ???

  def const(xs: List[Tree[Int]]): List[Tree[Int]] =
    xs.map(_.withValue(1))

  def run =
    IO.println(node) >>
      IO.println(node.mainline) >>
      IO.println(node.size)
