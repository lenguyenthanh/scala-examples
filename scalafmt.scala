//> using scala 3.6.2
//> using options -language:postfixOps

@main def hello: Unit =
  val chars1 = ('2' to '9') ++ (('a' to 'z').toSet - 'l') mkString
  val chars2 = (('2' to '9') ++ (('a' to 'z').toSet - 'l')).mkString
  println(chars1)
  println(chars2)
  println(chars1 == chars2)

  val castleInt = 13
  val lastMoveInt = 0x7f
  val x = (castleInt << 4) + (lastMoveInt >> 8) toByte
  val y = ((castleInt << 4) + (lastMoveInt >> 8)).toByte
  println(x)
  println(y)

  val white = ('0' to '4') ++ ('A' to 'Z') mkString
  val white2 = (('0' to '4') ++ ('A' to 'Z')).mkString
  println(white)
  println(white2)
