package model.dataModel

import model.{Row, Table}

case class EachTable[T](rows: List[EachRow[T]]) extends Table[T] {

  override def getRow(rowIndex: Int): Row[T] = rows(rowIndex)
  override def getRowCount: Int = rows.length

  override def getWidth: Int = rows.headOption.map(_.getLength).getOrElse(0)
}
