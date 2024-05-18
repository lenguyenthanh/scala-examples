//> using scala "3.4.1"
//> using toolkit typelevel:0.1.25
//> using dep de.lhns::fs2-compress-zip:2.0.0

import cats.effect.{ IO, IOApp }
import cats.syntax.all.*
import org.http4s.*
import org.http4s.client.Client
import org.http4s.implicits.*
import org.http4s.ember.client.EmberClientBuilder

object Main extends IOApp.Simple:
  val downloadUrl = uri"http://ratings.fide.com/download/players_list.zip"
  lazy val request = Request[IO](
    method = Method.GET,
    uri = downloadUrl
  )

  def run =
    EmberClientBuilder
      .default[IO]
      .build
      .use: client =>

        def fetch: IO[Set[String]] =
          client
            .stream(request)
            .switchMap(_.body)
            .through(Decompressor.decompress)
            .through(fs2.text.utf8.decode)
            .through(fs2.text.lines)
            .drop(1) // first line is header
            .collect:
              case line if line.trim.nonEmpty => line
            .evalMap(parseOtherTitle)
            .filter(_.nonEmpty)
            .evalTap(IO.println)
            .fold(Set.empty[String])(_ ++ _)
            .compile
            .lastOrError

        def string(start: Int, end: Int)(line: String) =
          line.substring(start, end).trim.some.filter(_.nonEmpty)

        def number(start: Int, end: Int)(line: String) = string(start, end)(line).flatMap(_.toIntOption)

        def parseOtherTitle(line: String): IO[Set[String]] =
          IO(
            string(94, 109)(line)
              .map(_.split(",").toSet)
              .getOrElse(Set.empty)
          )
            .handleErrorWith(e => IO.println(s"Error while parsing line: $line, error: $e").as(Set.empty))

        IO.realTimeInstant.flatMap(now => IO.println(s"Start crawling at $now"))
          *> fetch.handleErrorWith(e => IO.println(s"Error while crawling: $e")).flatMap(IO.println)
          *> IO.realTimeInstant.flatMap(now => IO.println(s"Finished crawling at $now"))

    // // shamelessly copied (with some minor modificaton) from: https://github.com/lichess-org/lila/blob/8033c4c5a15cf9bb2b36377c3480f3b64074a30f/modules/fide/src/main/FidePlayerSync.scala#L131
    // private def parseLine(line: String): IO[Option[(NewPlayer, Option[NewFederation])]] =
    //   def parse(line: String): Option[(NewPlayer, Option[NewFederation])] =
    //     for
    //       id   <- number(0, 15)
    //       name <- string(15, 76).map(_.filterNot(_.isDigit).trim)
    //       if name.sizeIs > 2
    //       title        = string(84, 89) >>= Title.apply
    //       wTitle       = string(89, 94) >>= Title.apply
    //       otherTitle   = string(94, 97) >>= OtherTitle.apply
    //       sex          = string(79, 82) >>= Sex.apply
    //       year         = number(152, 156).filter(_ > 1000)
    //       flags        = string(158, 159)
    //       federationId = string(76, 79)
    //     yield NewPlayer(
    //       id = id,
    //       name = name,
    //       title = title,
    //       womenTitle = wTitle,
    //       otherTitle = otherTitle,
    //       standard = number(113, 117),
    //       rapid = number(126, 132),
    //       blitz = number(139, 145),
    //       sex = sex,
    //       year = year,
    //       active = !flags.contains("i")
    //     ) -> federationId.map(id => NewFederation(id, Federation.nameById(id)))
    //   IO(parse(line))
    //     .handleErrorWith(e => error"Error while parsing line: $line, error: $e".as(none))

object Decompressor:

  import de.lhns.fs2.compress.*
  import fs2.Pipe
  val defaultChunkSize = 1024 * 4

  def decompress: Pipe[IO, Byte, Byte] =
    _.through(ArchiveSingleFileDecompressor(ZipUnarchiver.make[IO](defaultChunkSize)).decompress)

type PlayerId     = Int
type Rating       = Int
type FederationId = String

case class NewPlayer(
    id: PlayerId,
    name: String,
    title: Option[Title] = None,
    womenTitle: Option[Title] = None,
    otherTitle: Option[OtherTitle] = None,
    standard: Option[Rating] = None,
    rapid: Option[Rating] = None,
    blitz: Option[Rating] = None,
    sex: Option[Sex] = None,
    year: Option[Int] = None,
    active: Boolean
)

object FederationId:
  def apply(value: String): FederationId = value

enum Title(val value: String):
  case GM  extends Title("GM")
  case IM  extends Title("IM")
  case FM  extends Title("FM")
  case WGM extends Title("WGM")
  case WIM extends Title("WIM")
  case WFM extends Title("WFM")
  case CM  extends Title("CM")
  case WCM extends Title("WCM")
  case NM  extends Title("NM")
  case WNM extends Title("WNM")

object Title:
  def apply(value: String): Option[Title] =
    Title.values.find(_.value == value)

enum OtherTitle(val value: String):
  case IA  extends OtherTitle("IA")  // International Arbiter
  case FA  extends OtherTitle("FA")  // FIDE Arbiter
  case NA  extends OtherTitle("NA")  // National Arbiter
  case IO  extends OtherTitle("IO")  // International Organizer
  case FST extends OtherTitle("FST") // FIDE Senior Trainer
  case FT  extends OtherTitle("FT")  // FIDE Trainer
  case FI  extends OtherTitle("FI")  // FIDE Instructor
  case DI  extends OtherTitle("DI")  // Developmental Instructor
  case NI  extends OtherTitle("NI")  // National Instructor
  case SI  extends OtherTitle("SI")  // School Instructor

object OtherTitle:
  def apply(value: String): Option[OtherTitle] =
    OtherTitle.values.find(_.value == value)

enum Sex(val value: String):
  case Female extends Sex("F")
  case Male   extends Sex("M")

object Sex:
  def apply(value: String): Option[Sex] =
    value match
      case "F" => Some(Female)
      case "M" => Some(Male)
      case _   => None
