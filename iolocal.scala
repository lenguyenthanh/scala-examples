//> using scala 3.7.3
//> using dep "org.typelevel::cats-effect:3.6.3"

import cats.effect.*

object main extends IOApp.Simple:

  val effectWithoutRace = for {
    local <- IOLocal[Option[String]](None)
    _ <- local.set(Some("uhh... hi!"))
    _ <- local.get.flatMap(IO.println)
  } yield ()


  def withRace[A](effect: IO[A]): IO[A] = effect.race(IO.never).map(_.merge)

  val effectWithRace = for {
    local <- withRace(IOLocal[Option[String]](None))
    _ <- withRace(local.set(Some("uhh... hi!")))
    _ <- withRace(local.get.flatMap(IO.println))
  } yield ()

  def withLocal =
    for {
      _ <- IO.println("withLocal")
      local <- IO.local[Option[String]](None)
      _ <- local.scope(IO.println("hi1") *> local.ask.flatMap(IO.println))(Some("uhh... hi!")) *> local.ask.flatMap(IO.println)
      _ <- local.local(IO.println("hi2") *> local.ask.flatMap(x => IO.println(s"$x + 1")))(_ => Some("... hi!")) *> local.ask.flatMap(IO.println)
      _ <- local.ask.flatMap(IO.println)
    } yield ()

  def run =
    effectWithoutRace *> effectWithRace *>
      withLocal
