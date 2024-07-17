//> using scala 3.5.0-RC3
// //> using dep org.reactivemongo::reactivemongo-bson-api:1.1.0-RC13.SNAPSHOT
//> using dep "org.reactivemongo::reactivemongo-bson-api:1.1.0-RC12"
//> using options -source:3.6-migration
//> using options -Xprint:typer

import reactivemongo.api.bson.{ collectionWriter as _, BSONHandler }


// val x = summon[BSONHandler[Map[String, String]]]
// val y = summon[BSONHandler[List[String]]]
