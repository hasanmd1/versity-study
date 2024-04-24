package model.dataModel

import model.{Cell, Row}

case class EachRow[T](cells: Vector[Cell[T]]) extends Row[T] {
  override def getLength: Int = cells.length

  override def getCell(index: Int): Cell[T] = cells(index)
}
