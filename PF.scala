//> using scala 2.13.12
////> using plugin org.typelevel:::kind-projector:0.13.2
//> using options -Vtyper -Vdebug -Vlog:typer -verbose -Vprint

object Foo {

  type Id[A] = A

  trait Extract[T, F[_]] {
    def apply(json: AnyRef): F[T]
  }

  object Extract {
    implicit def fromId[T](id: Id[T]): T = id
    implicit val extractString: Extract[String, Id] =
      (json: AnyRef) => "baz"
  }

  final class PartiallyAppliedExtract[T](val entity: Entity) extends AnyVal {
    def apply[F[_]](key: String)(implicit extract: Extract[T, F]): F[T] =
      extract(null)
  }

  trait Entity {

    final def extract[T]: PartiallyAppliedExtract[T] =
      new PartiallyAppliedExtract(this)
  }

  val e: Entity = new Entity {}

  import Extract._
  printf(e.extract[String]("foo"))

}
