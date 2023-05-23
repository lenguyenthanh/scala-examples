//> using scala 3.2.2

trait FromString[A]:
  def parse(s: String): Either[String, A]

sealed trait Uci:

  case class Move(from: String, to: String) extends Uci

object Uci extends FromString[Uci]:
	def parse(s: String): Either[String, Uci] = ???
