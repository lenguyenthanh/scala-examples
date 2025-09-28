//> using scala 3.7.3
// //> using options -Xprint:postInlining

trait Otel4s[F[_]]:
  def run: F[Unit]

trait Route[F[_]]:
  def route: F[String]

sealed trait ExportMode
object ExportMode:
  case object Push extends ExportMode
  case object Pull extends ExportMode

// type ExportMode = 0 | 1
// object ExportMode:
//   inline val Push = true
//   inline val Pull = false

trait Builder:
  type Out[F[_]]
  type In
  def build[F[_]](in: In): Out[F]

object PushBuilder extends Builder:
  type Out[F[_]] = Otel4s[F]
  type In = Unit
  override def build[F[_]](in: Unit): Otel4s[F] = ???

object PullBuilder extends Builder:
  type In = String
  type Out[F[_]] = (Otel4s[F], Route[F])
  override def build[F[_]](s: String): Out[F] = ???

def push[F[_]](a: Int): Otel4s[F] = ???
def pull[F[_]](a: Int, b: String): (Otel4s[F], Route[F]) = ???

// type ModeBuilder[X <: ExportMode] =
//   X match
//     case ExportMode.Push.type => PushBuilder
//     case ExportMode.Pull.type => Int

transparent inline def test[F[_]](inline mode: ExportMode) =
  inline mode match
    case ExportMode.Push => push[F]
    case ExportMode.Pull => pull[F]

val x: Otel4s[Option] = test[Option](ExportMode.Push)(2)
val y: (Otel4s[Option], Route[Option]) = test[Option](ExportMode.Pull)(2, "hello")
