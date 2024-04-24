package argumentParser

trait Parser[X, Y, Z] {
  def collectInput : X
  def collectOutput : Z
  def collectFilter : Y
  def getSeparator : X
}
