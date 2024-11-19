//> using scala 3.5.2
//> using toolkit typelevel:latest

object types:
  opaque type Title = String
  object Title:
    def apply(title: String): Either[Exception, Title] =
      if title.isBlank then Left(Exception("Title must not be blank"))
      else Right(title)

  opaque type NonEmptyList[A] = (A, List[A])
  extension [A](nel: NonEmptyList[A])
    def toList: List[A] = nel match
      case (a, list) => a :: list

  object NonEmptyList:
    def apply[A](a: A, as: A*): NonEmptyList[A] =
      (a, as.toList)

  opaque type ISBN = String
  object ISBN:
    private val isbnRegex = """^(?=(?:\D*\d){10}(?:(?:\D*\d){3})?$)[\d-]+$""".r

    def apply(isbn: String): Either[Exception, ISBN] =
      if isbnRegex.matches(isbn) then Right(isbn)
      else Left(Exception("Not a valid ISBN"))

  opaque type Author = String
  object Author:
    def apply(firstName: String, lastName: String): Either[Exception, Author] =
      if firstName.nonEmpty && lastName.nonEmpty then Right(s"$firstName $lastName")
      else Left(Exception("Author needs to have a first and last name"))

import types.*

case class Book(title: Title, isbn: ISBN, authors: NonEmptyList[Author])

import cats.syntax.all.*
@main def main = println {
  (
    Title("Functional programming in Scala"),
    ISBN("9781617299582"),
    Author("Michael", "Pilquist"),
    Author("Rúnar", "Bjarnason"),
    Author("Paul", "Chiusano")
  ).mapN { (title, isbn, mpilquist, rbjarnason, pchiusano) =>
    Book(title, isbn, NonEmptyList(mpilquist, rbjarnason, pchiusano))
  }
}
