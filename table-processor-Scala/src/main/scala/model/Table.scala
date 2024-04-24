package model

trait Table[T] {
  def getRow(rowIndex: Int): Row[T]
  def getRowCount: Int
  def getWidth: Int
}
