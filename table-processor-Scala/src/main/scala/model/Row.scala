package model

trait Row[T] {
  def getLength: Int
  def getCell(index: Int): Cell[T]
}
