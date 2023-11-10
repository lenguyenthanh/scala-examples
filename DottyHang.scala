//> using scala 3.3.0


opaque type Hash = Array[Byte]
object Hash:
  def apply(value: Array[Byte]): Hash = value
  val empty: Hash                     = Array.empty

  extension (p: Hash)
    def value: Array[Byte]                                = p
    def isEmpty: Boolean                                  = p.isEmpty
