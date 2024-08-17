//> using scala 3.3.3
//> using toolkit typelevel:0.1.27
//> using dep org.rocksdb:rocksdbjni:9.4.0
//> using dep org.scodec::scodec-core:2.3.1

import cats.*
import cats.syntax.all.*
import cats.effect.*
import org.rocksdb.Options
import org.rocksdb.RocksDB as JniRocksDB
import cats.MonadThrow
import scodec.bits._
import scodec.Codec

object Main extends IOApp.Simple {
  def run: IO[Unit] =
    RocksDB.make[IO]("rocksdb", new Options().setCreateIfMissing(true)).use { db =>
      for
        _ <- db.putBytes(hex"01", hex"02")
        _ <- db.putBytes(hex"03", hex"04")
        _ <- db.getBytes(hex"01").flatMap(v => IO(println(v)))
        _ <- db.getBytes(hex"03").flatMap(v => IO(println(v)))
        _ <- db.deleteBytes(hex"01")
        _ <- db.getBytes(hex"01").flatMap(v => IO(println(v)))
      yield ()
    }
}

trait RocksDB[F[_]: Monad] {

  // fundamental operations
  def getBytes(key: ByteVector): F[Option[ByteVector]]
  def putBytes(key: ByteVector, value: ByteVector): F[Unit]
  def deleteBytes(key: ByteVector): F[Unit]

  // scodec operations
  // return IllegalArgumentException: if codec is failed
  def get[A](key: ByteVector)(using c: Codec[A]): F[Option[A]] =
    for bytes <- getBytes(key)
    yield bytes.map(x => c.decodeValue(x.bits).require)
}

object RocksDB:
  def make[F[_]: Sync: MonadThrow](path: String, option: Options): Resource[F, RocksDB[F]] =
    Resource.eval(Sync[F].blocking(JniRocksDB.loadLibrary())) >>
      Resource
        .make(Sync[F].blocking(JniRocksDB.open(option, path)))(db => Sync[F].blocking(db.close()))
        .map: db =>
          new RocksDB[F]:
            def getBytes(key: ByteVector): F[Option[ByteVector]] =
              Sync[F].blocking(Option(db.get(key.toArray)).map(ByteVector(_)))

            def putBytes(key: ByteVector, value: ByteVector): F[Unit] =
              Sync[F].blocking(db.put(key.toArray, value.toArray))

            def deleteBytes(key: ByteVector): F[Unit] =
              Sync[F].blocking(db.delete(key.toArray))
