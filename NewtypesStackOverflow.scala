//> using scala "3.3.4"
//> using repository "https://raw.githubusercontent.com/lichess-org/lila-maven/master"
//> using dep "com.github.ornicar::scalalib:9.5.4"
//> using options -Ysafe-init, -feature

import ornicar.scalalib.newtypes.*
import scala.language.implicitConversions

case class User(id: String, name: String)

object Models:
  /* User who is currently logged in */
  opaque type Me = Int
  object Me extends TotalWrapper[Me, Int]:

    extension (me: Me)
      def user: Int   = me.value


case class Mod(user: Int) extends AnyVal:
  def mod = s"mod $user"

@main def main =

  import Models.Me

  given Conversion[Me, Mod] = (me: Me) => Mod(me.user)

  // this works
  // given Conversion[Me, Mod] = (me: Me) => Mod(me.value)

  val me: Me = Me(12)

  println(me.mod)
