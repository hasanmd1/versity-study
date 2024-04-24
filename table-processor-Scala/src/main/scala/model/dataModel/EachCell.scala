package model.dataModel

import model.Cell


case class EachCell[T] (value: T, cellType: String) extends Cell[T] {

  override def getValue: T = value
}
