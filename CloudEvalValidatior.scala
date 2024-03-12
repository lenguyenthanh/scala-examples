//> using scala 3.4.0
//> using toolkit typelevel:latest
//> using dep de.lhns::fs2-compress-zstd:1.0.0
//> using repository https://raw.githubusercontent.com/lichess-org/lila-maven/master
//> using dep org.lichess::scalachess:15.7.0

import cats.data.NonEmptyList
import cats.effect.{ IO, IOApp }
import cats.syntax.all.*
import de.lhns.fs2.compress.ZstdDecompressor
import fs2.text
import fs2.io.file.{ Files, Path }
import org.http4s.*
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.implicits.*

object Validator extends IOApp.Simple:

  val uri = uri"https://database.lichess.org/lichess_db_eval.json.zst"

  lazy val request = Request[IO](
    method = Method.GET,
    uri = uri
  )

  def execute(client: Client[IO]) =
    client
      .stream(request)
      .switchMap(_.body)
      .through(ZstdDecompressor.make[IO]().decompress)
      .through(text.utf8.decode)
      .through(fs2.text.lines)
      .take(1)
      // .evalTap(IO.println)
      .filter(_.nonEmpty)
      .map(Evals.validate)
      .collect:
        case Left(x) => x.toString
      .through(Files[IO].writeUtf8Lines(Path("errors.txt")))
      .compile
      .drain

  val run: IO[Unit] = EmberClientBuilder
    .default[IO]
    .build
    .use(execute)

object Evals:

  import chess.variant.Variant
  import chess.format.{ Fen, Uci }
  import chess.Situation
  import chess.MoveOrDrop.*

  import io.circe.{ Decoder, DecodingFailure, HCursor }
  import io.circe.jawn.decode

  enum Score:
    case Cp(c: Int)
    case Mate(m: Int)

  case class Pv(score: Score, line: NonEmptyList[Uci]) derives Decoder
  case class Eval(pvs: NonEmptyList[Pv], knodes: Int, depth: Int) derives Decoder
  case class Entry(fen: Fen.Epd, evals: List[Eval]) derives Decoder

  given Decoder[Pv] = new Decoder[Pv]:
    final def apply(c: HCursor): Decoder.Result[Pv] =
      for
        cp    <- c.downField("cp").as[Option[Int]].map(_.map(Score.Cp(_)))
        mate  <- c.downField("mate").as[Option[Int]].map(_.map(Score.Mate(_)))
        score <- cp.orElse(mate).toRight(DecodingFailure("Invalid score, need either cp or mate", c.history))
        line  <- c.downField("line").as[NonEmptyList[Uci]]
      yield Pv(score, line)

  given Decoder[Fen.Epd] = Decoder.decodeString.map(Fen.Epd(_))
  given Decoder[Uci]     = Decoder.decodeString.emap(Uci(_).toRight("Invalid UCI"))
  given Decoder[Variant] = Decoder.decodeString.emap(Variant.byName(_).toRight("Invalid variant"))
  given Decoder[NonEmptyList[Uci]] =
    Decoder.decodeString.emap(Uci.readList(_).flatMap(NonEmptyList.fromList(_)).toRight("Invalid ucis"))

  def validate(input: String) =
    for
      entry <- parse(input)
      // _ = println(s"Validating entry: $entry")
      _ <- validateEntry(entry)
    yield ()

  def parse(line: String): Either[io.circe.Error, Entry] =
    decode[Entry](line)

  def validateEntry(entry: Entry) =
    for
      sit <- Fen.readWithMoveNumber(entry.fen).toRight(RuntimeException(s"Invalid fen: ${entry.fen}"))
      _ <- entry.evals
        .flatMap(_.pvs.toList.map(_.line.toList))
        .traverse(validateLines(entry.fen, sit.situation, _))
    yield ()

  def validateLines(fen: Fen.Epd, sit: Situation, ucis: List[Uci]) =
    ucis
      .foldM(sit)((sit, uci) => uci(sit).map(_.situationAfter))
      .leftMap(e => s"Invalid line: ${ucis.mkString(" ")} for fen :${fen.value} with error: $e")
