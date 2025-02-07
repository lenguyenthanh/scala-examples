//> using scala 3.6.3
//> using toolkit typelevel:default
//> using dependency org.rocksdb:rocksdbjni:9.10.0
//> using dependency org.scodec::scodec-core:2.3.2
//> using options -deprecation
///> using options -Wall

import cats.*
import cats.syntax.all.*
import cats.effect.*
import org.rocksdb.{ Options as JOptions, RocksDB => JRocksDB }
import cats.MonadThrow
import scodec.bits._
import scodec.{ Codec, Decoder, Encoder }
import scodec.codecs.*

object Main extends IOApp.Simple {
  def run: IO[Unit] =
    RocksDB.make[IO]("rocksdb", RocksDB.Options(createIfMissing = true)).use { db =>
      // bytes(db) >> codec(db) >> example(db)
      example(db)
    }

  def bytes(db: RocksDB[IO]): IO[Unit] =
    for
      _ <- db.putBytes(hex"01", hex"02")
      _ <- db.putBytes(hex"03", hex"04")
      _ <- db.getBytes(hex"01").flatMap(v => IO(println(v)))
      _ <- db.getBytes(hex"03").flatMap(v => IO(println(v)))
      _ <- db.deleteBytes(hex"01")
      _ <- db.getBytes(hex"01").flatMap(v => IO(println(v)))
    yield ()

  def example(db: RocksDB[IO]): IO[Unit] =
    for
      _ <- db.put(Example(1, Some("a")), Example(2, Some("b")))
      _ <- db.put(Example(3, None), Example(4, None))
      _ <- db.get[Example, Example](Example(1, Some("a"))).flatMap(v => IO(println(v)))
      _ <- db.get[Example, Example](Example(3, None)).flatMap(v => IO(println(v)))
      _ <- db.delete(Example(1, Some("a")))
      _ <- db.get[Example, Example](Example(1, Some("a"))).flatMap(v => IO(println(v)))
    yield ()

  def codec(db: RocksDB[IO]): IO[Unit] =
    given Codec[Int] = scodec.codecs.int32
    for
      _ <- db.put(1, 2)
      _ <- db.put(3, 4)
      _ <- db.get(1).flatMap(v => IO(println(v)))
      _ <- db.get(3).flatMap(v => IO(println(v)))
      _ <- db.delete(1)
      _ <- db.get(1).flatMap(v => IO(println(v)))
    yield ()
}

case class Example(a: Int, b: Option[String])
object Example:
  given Codec[Example] = (int32 :: optional(bool, utf8_32)).as[Example]

trait RocksDB[F[_]: Monad] {

  // fundamental operations
  // another set for bits
  def getBytes(key: ByteVector): F[Option[ByteVector]]
  def putBytes(key: ByteVector, value: ByteVector): F[Unit]
  def deleteBytes(key: ByteVector): F[Unit]

  // scodec operations

  // return IllegalArgumentException: if codec is failed
  def get[A, B](key: A)(using c1: Encoder[A], c2: Decoder[B]): F[Option[B]] =
    getBytes(c1.encode(key).require.bytes).map(_.flatMap(x => c2.decodeValue(x.bits).toOption))

  def put[A, B](key: A, value: B)(using c1: Encoder[A], c2: Encoder[B]): F[Unit] =
    putBytes(c1.encode(key).require.bytes, c2.encode(value).require.bytes)

  def delete[A](key: A)(using c: Encoder[A]): F[Unit] =
    deleteBytes(c.encode(key).require.bytes)
}

object RocksDB:

  case class Options(
      createIfMissing: Boolean = false,
      paranoidChecks: Boolean = false,
      writeBufferSize: Long = 0,
      maxWriteBufferNumber: Int = 0,
      maxOpenFiles: Int = 0,
      blockCacheSize: Long = 0
  )

  private def make[F[_]: Sync](o: Options): Resource[F, JOptions] =
    Resource.make(
      Sync[F].blocking(
        new JOptions()
          .setCreateIfMissing(o.createIfMissing)
          .setParanoidChecks(o.paranoidChecks)
          .setWriteBufferSize(o.writeBufferSize)
          .setMaxWriteBufferNumber(o.maxWriteBufferNumber)
          .setMaxOpenFiles(o.maxOpenFiles)
          .optimizeForPointLookup(o.blockCacheSize)
      )
    )(o => Sync[F].blocking(o.close()))

  def make[F[_]: Sync](path: String, option: Options): Resource[F, RocksDB[F]] =
    Resource.eval(Sync[F].blocking(JRocksDB.loadLibrary())) >>
      make(option)
        .flatMap: option =>
          Resource
            .make(Sync[F].blocking(JRocksDB.open(option, path)))(db => Sync[F].blocking(db.close()))
            .map: db =>
              new RocksDB[F]:
                def getBytes(key: ByteVector): F[Option[ByteVector]] =
                  Sync[F].blocking(Option(db.get(key.toArray)).map(ByteVector(_)))

                def putBytes(key: ByteVector, value: ByteVector): F[Unit] =
                  Sync[F].blocking(db.put(key.toArray, value.toArray))

                def deleteBytes(key: ByteVector): F[Unit] =
                  Sync[F].blocking(db.delete(key.toArray))
