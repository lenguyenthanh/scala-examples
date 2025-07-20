//> using scala 3.7.1
//> using dep org.typelevel::cats-effect:3.6.1
//> using dep co.fs2::fs2-io:3.12.0
//> using dep io.github.kirill5k::mongo4cats-core:0.7.13

import cats.effect.*
import cats.syntax.all.*
import fs2.io.file.{ Files, Path }
import java.time.Instant
import mongo4cats.client.MongoClient
import mongo4cats.database.MongoDatabase
import mongo4cats.operations.Filter
import scala.concurrent.duration.*

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    args.lift(2).flatMap(_.toIntOption) match {
      case Some(n) if n >= 2010 && n < 2026 =>
        run(args(0), args(1), n).as(ExitCode.Success)
      case _ =>
        IO.println("Usage: export-studies <mongo-uri> <database> <year>").as(ExitCode.Error)
    }

  def run(mongoUri: String, database: String, year: Int): IO[Unit] =
    makeMongoClient(mongoUri, database)
      .use(exportAll(_, year))
      .handleErrorWith { e =>
        IO.println(s"Error exporting studies: ${e.getMessage}").as(ExitCode.Error)
      }

  def exportAll(db: MongoDatabase[IO], year: Int): IO[Unit] =
    for
      ref          <- Ref.of[IO, List[String]](List.empty)
      _            <- exportStudies(db, year)(ref)
      studiesCount <- ref.get
      _            <- IO.println(s"Exported ${studiesCount.size} studies for year $year")
      _            <- exportChapters(db, year)(ref)
    yield ()

  def exportStudies(db: MongoDatabase[IO], year: Int)(ref: Ref[IO, List[String]]): IO[Unit] =
    val filter =
      Filter
        .gte("createdAt", Instant.parse(s"$year-01-01T00:00:00Z"))
        .and(Filter.lt("createdAt", Instant.parse(s"$year-01-08T23:59:59Z")))
        .and(Filter.eq("visibility", "public"))
    db.getCollection("study")
      .flatMap: coll =>
        coll
          .find(filter)
          .stream
          .evalTap(doc => ref.update(xs => doc.getString("_id").fold(xs)(_ :: xs)))
          .map(_.toJson)
          .through(Files[IO].writeUtf8Lines(Path(s"studies-$year.ndjson")))
          .compile
          .drain

  def exportChapters(db: MongoDatabase[IO], year: Int)(ref: Ref[IO, List[String]]): IO[Unit] =
    (db.getCollection("study_chapter_flat"), ref.get).flatMapN: (coll, ids) =>
      IO.println(s"Exporting chapters for $ids studies in $year") *>
        fs2.Stream
          .emits(ids)
          .map(id => Filter.eq("studyId", id))
          .flatMap(coll.find(_).boundedStream(100))
          .map(_.toJson)
          .through(Files[IO].writeUtf8Lines(Path(s"chapters-$year.ndjson")))
          .compile
          .drain

  private def makeMongoClient(uri: String, database: String) =
    MongoClient
      .fromConnectionString[IO](uri)
      .evalMap(_.getDatabase(database))

}
