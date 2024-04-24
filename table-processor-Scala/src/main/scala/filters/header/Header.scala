package filters.header

import filters.AllFilter
import model.dataModel.{EachCell, EachRow, EachTable}

class Header(table: EachTable[Any]) extends AllFilter[EachTable[Any]] {

  override def apply(): EachTable[Any] = {
    val numColumns = table.getWidth

    val headerCells = new scala.collection.mutable.ArrayBuffer[EachCell[Any]]()
    for (i <- 0 until numColumns) {
      headerCells += EachCell[Any](s"${('A' + i).toChar}", "String")
    }

    val headerRow = EachRow(headerCells.toVector)
    val rowsWithHeader = headerRow +: table.rows

    EachTable[Any](rowsWithHeader)
  }
}
