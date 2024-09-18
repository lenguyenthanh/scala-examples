//> using scala 3.5.0
// //> using dep org.reactivemongo::reactivemongo-bson-api:1.1.0-RC13.SNAPSHOT
//> using dep "org.reactivemongo::reactivemongo-bson-api:1.1.0-RC12"
// //> using options -source:3.7
//> using options -Xprint:typer

import reactivemongo.api.bson.{ collectionWriter as _, BSONHandler }


// val x = summon[BSONHandler[Map[String, String]]]
// val y = summon[BSONHandler[List[String]]]

class General
class Specific extends General

class LowPriority:
  given a:General()

object NormalPriority extends LowPriority:
  given b:Specific()

def run =
  import NormalPriority.given
  val x = summon[General]
  val _: Specific = x // <- b was picked
