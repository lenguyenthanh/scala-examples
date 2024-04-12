//> using scala 3.4.0
//> using toolkit typelevel:0.1.23
//> using repository https://raw.githubusercontent.com/lichess-org/lila-maven/master
//> using dep org.lichess::scalachess:15.9.0

import cats.data.{ Validated, ValidatedNel }
import cats.effect.{ ExitCode, IO, IOApp }
import cats.syntax.all.*
import chess.*
import chess.MoveOrDrop.*
import chess.format.Fen
import chess.format.Uci
import chess.variant.*
import com.monovore.decline.*
import com.monovore.decline.effect.*
import chess.format.pgn.SanStr

object Converter
    extends CommandIOApp(name = "Ucis to sans", header = "Convert ucis to san moves", version = "0.0.1"):

  override def main: Opts[IO[ExitCode]] = CLI.parse
    .map(execute(_).as(ExitCode.Success))

  private def execute(args: CLI.Args): IO[Unit] =
    val replay = for
      ucis <- args.moves.split(" ").toList.traverse(Uci.apply).toRight("Invalid UCI moves")
      situation = args.fen.flatMap(Fen.read(args.variant, _)).getOrElse(Situation(args.variant))
      x <- validateMoves(situation, ucis)
    yield x
    // IO.println(replay.map(_.mkString(" ")))
    IO.println(replay)

  def uciToSan(sit: Situation, moves: List[Uci]): Either[ErrorStr, List[SanStr]] =
    moves
      .foldM(sit -> Nil): (x, move) =>
        move(x._1).map(md => Situation(md.finalizeAfter, !x._1.color) -> (md.toSanStr :: x._2))
      .map(_._2.reverse)

  def validateMoves(sit: Situation, moves: List[Uci]): Either[ErrorStr, Situation] =
    moves.foldM(sit): (x, move) =>
      move(x).map(md => Situation(md.finalizeAfter, !x.color))

object CLI:

  case class Args(moves: String, variant: Variant, fen: Option[Fen.Epd])

  given Argument[Variant] with
    def read(string: String): ValidatedNel[String, Variant] =
      validate(string).toValidatedNel

    def defaultMetavar: String = "variant"

  given Argument[Fen.Epd] with
    def read(string: String): ValidatedNel[String, Fen.Epd] =
      Validated.validNel(Fen.Epd(string))
    def defaultMetavar: String = "fen"

  private val variantOpt: Opts[Variant] = Opts
    .option[Variant]("variant", "Variant to generate positions for", "v")
    .withDefault(Standard)

  private val fenOpt: Opts[Option[Fen.Epd]] = Opts
    .option[Fen.Epd]("Fen", "Initial Fen position, can be optional", "f")
    .orNone

  private val movesOpt = Opts.option[String]("moves", "List of moves in UCI format")

  val parse = (movesOpt, variantOpt, fenOpt).mapN(Args.apply)

  private def validate(variant: String): Either[String, Variant] =
    variant match
      case "crazyhouse"    => Right(Crazyhouse)
      case "atomic"        => Right(Atomic)
      case "horde"         => Right(Horde)
      case "racingkings"   => Right(RacingKings)
      case "antichess"     => Right(Antichess)
      case "3check"        => Right(ThreeCheck)
      case "threecheck"    => Right(ThreeCheck)
      case "kingofthehill" => Right(KingOfTheHill)
      case _ =>
        Left(
          s"Unknown or unsupported variant: $variant.\nSupported variants: crazyhouse, atomic, horde, racingkings, antichess, threecheck, kingofthehill"
        )
