//> using scala 3.nightly

class Inner(body: Int ?=> Int) extends AnyVal:
  // this needs to be context function to makes compiler crashes
  def rescue: Int ?=> Int = ???
