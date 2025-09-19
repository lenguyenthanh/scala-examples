//> using scala 3.7.2

def callByName(x: => Int): Int = ???
def callByValue(x: Int): Int = ???

val x = callByName(1 + 1)
val y = callByValue(1 + 1)
