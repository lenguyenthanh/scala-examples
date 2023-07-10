//> using scala "3.3.0"
//> using repository "https://raw.githubusercontent.com/lichess-org/lila-maven/master"
//> using dep "com.github.ornicar::scalalib:9.5.4"
//> using options -Ysafe-init, -feature

import ornicar.scalalib.newtypes.*
import scala.language.implicitConversions

opaque type LimitSeconds = Int
object LimitSeconds extends OpaqueInt[LimitSeconds]

opaque type LimitMinutes = Int
object LimitMinutes extends OpaqueInt[LimitMinutes]

trait UserIdOf[U]:
  def apply(u: U): UserId
  extension (u: U)
    inline def id: UserId                        = apply(u)
    inline def is[T: UserIdOf](other: T)         = u.id == other.id
    inline def isnt[T: UserIdOf](other: T)       = u.id != other.id
    inline def is[T: UserIdOf](other: Option[T]) = other.exists(_.id == u.id)

opaque type UserId = String
object UserId extends OpaqueString[UserId]:
  given UserIdOf[UserId] = _.value

case class User(id: UserId, name: String)

object Models:
  /* User who is currently logged in */
  opaque type Me = User
  object Me extends TotalWrapper[Me, User]:
    given UserIdOf[Me]                           = _.id
    given (using me: Me): Option[Me]             = Some(me)

    // given Conversion[Me, User]                   = _.value
    // given Conversion[Me, UserId]                 = _.id
    // given Conversion[Option[Me], Option[UserId]] = _.map(_.id)
    // given [M[_]]: Conversion[M[Me], M[User]]     = Me.raw(_)

    extension (me: Me)
      def userId: UserId      = me.id
      def user: User   = me.value
      // inline def modId: ModId = userId.into[ModId]
      // inline def myId: MyId   = userId.into[MyId]


opaque type MyId = String
object MyId extends TotalWrapper[MyId, String]:
  given UserIdOf[MyId]                         = u => u
  // given (using me: Me): MyId                   = Me.myId(me.userId)
  given (using me: MyId): Option[MyId]         = Some(me)
  // extension (me: MyId)
  //   inline def modId: ModId   = me into ModId
  //   inline def userId: UserId = me into UserId


case class Mod(user: User) extends AnyVal:
  def id = user.id into ModId
  def mod = s"mod $user"

object Mod:
  def me(me: Models.Me): Mod = Mod(me.user)

// specialized UserIds like Coach.Id
trait OpaqueUserId[A] extends OpaqueString[A]:
  given UserIdOf[A]                          = _.userId
  extension (a: A) inline def userId: UserId = a into UserId

opaque type ModId = String
object ModId extends OpaqueUserId[ModId]

@main def main =

  import Models.Me

  given Conversion[Me, Mod] = (me: Me) => Mod(me.user)

  val me: Me = Me(User("me", "me"))

  println(me.mod)
