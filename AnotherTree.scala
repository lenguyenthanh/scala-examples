//> using scala "3.3.0"
//> using dep "org.typelevel::toolkit:latest.release"
//> using dep "org.typelevel::kittens:3.0.0"
//> using dep "dev.optics::monocle-core:3.2.0"
//> using options "-Yexplicit-nulls"

import cats.*
import cats.effect.*
import cats.derived.*
import cats.syntax.all.*
import scala.annotation.tailrec

trait HasId[A, Id]:
  def getId(a: A): Id

// try reducible
trait Tree[F[_]: Functor: Traverse, A]:

  def value(fa: F[A]): A
  def child(fa: F[A]): Option[Mainline[A]]
  def withValue(fa: F[A])(value: A): F[A]

  def withChild(fa: F[A])(child: Mainline[A]): F[A]
  def withoutChild(fa: F[A]): F[A]
  def isVariation(fa: F[A]): Boolean

  def hasId[Id](fa: F[A])(id: Id)(using h: HasId[A, Id]): Boolean = h.getId(value(fa)) == id

  def modifyAt[Id](fa: F[A])(path: List[Id], f: Tree[F, A] => Option[Tree[F, A]])(using HasId[A, Id]): Option[F[A]]

  def findPath[Id](path: List[Id])(using HasId[A, Id]): Option[List[Tree[F, A]]] = ???
    // @tailrec
    // def loop(tree: Tree[F, A], path: List[Id], acc: List[Tree[F, A]]): Option[List[Tree[F]]] = path match
    //   case Nil                             => None
    //   case head :: Nil if tree.hasId(head) => (tree :: acc).some
    //   case head :: rest if tree.hasId(head) =>
    //     tree.child match
    //       case None        => None
    //       case Some(child) => loop(child, rest, tree :: acc)
    //   case head :: _ =>
    //     tree match
    //       case node: Mainline[A] =>
    //         node.findVariation(head) match
    //           case None            => None
    //           case Some(variation) => loop(variation, path, acc)
    //       case _ => None
    //
    // if path.isEmpty then None else loop(this, path, Nil)
    //

object Tree:

  given mainLineTree[A]: Tree[Mainline, A] =
    new:
      def modifyAt[Id](fa: Mainline[A])(path: List[Id], f: Mainline[A] => Option[Mainline[A]])(using
          HasId[A, Id]
      ): Option[Mainline[A]] = ???

      def value(fa: Mainline[A]) = fa.value
      def child(fa: Mainline[A]) = fa.child
      def withValue(fa: Mainline[A])(value: A): Mainline[A] = fa.copy(value = value)
      def withChild(fa: Mainline[A])(child: Mainline[A]): Mainline[A] = fa.copy(child = child.some)
      def withoutChild(fa: Mainline[A]): Mainline[A] = fa.copy(child = None)
      def isVariation(fa: Mainline[A]): Boolean = false

  given variationTree[A]: Tree[Variation, A] =
    new :
      def modifyAt[Id](v: Variation[A])(path: List[Id], f: Variation[A] => Option[Variation[A]])(using
          HasId[A, Id]
      ): Option[Variation[A]] = ???

      def value(fa: Variation[A]) = fa.value
      def child(fa: Variation[A]) = fa.child
      def withValue(fa: Variation[A])(value: A): Variation[A] = fa.copy(value = value)
      def withChild(fa: Variation[A])(child: Mainline[A]): Variation[A] = fa.copy(child = child.some)
      def withoutChild(fa: Variation[A]): Variation[A] = fa.copy(child = None)
      def isVariation(fa: Variation[A]): Boolean = true

sealed abstract class Node[A](value: A, child: Option[Mainline[A]])

final case class Mainline[A](
    value: A,
    child: Option[Mainline[A]] = None,
    variations: List[Variation[A]] = Nil
) extends Node[A](value, child) derives Functor,
      Traverse:
  def toVariation: Variation[A] = Variation(value, child)

final case class Variation[A](value: A, child: Option[Mainline[A]] = None) extends Node[A](value, child) derives Functor, Traverse:
  def toMainline: Mainline[A] = Mainline(value, child)
