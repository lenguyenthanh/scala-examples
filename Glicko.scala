//> using scala 3.5.2

type Result = Int
type Rating = Int
// rewrite from java https://github.com/goochjs/glicko2
type RatingPeriodResults[R <: Result] = Map[Rating, List[R]]

val x: RatingPeriodResults[Result] = Map[Rating, List[Result]]()
