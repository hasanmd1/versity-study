package filters.range

import filters.AllFilter
import model.dataModel.{EachRow, EachTable}

class RangeSpecifier(table: EachTable[Any], range: String) extends AllFilter[EachTable[Any]] {

  override def apply(): EachTable[Any] = {

    try{
      val rangeComponents = range.split(" ")

      if (rangeComponents.length != 2) {
        throw new IllegalArgumentException("Invalid range format. Expected start and end components.")
      }

      val start = rangeComponents(0)
      val end = rangeComponents(1)

      val startC = start.charAt(0).toUpper - 'A'
      val endC = end.charAt(0).toUpper - 'A'
      val startR = start.charAt(1) - '1'
      val endR = end.charAt(1) - '1'

      // Extract rows between startRow and endRow
      val rowsInRange = table.rows.slice(startR, endR+1)

      // Extract cells within the specified columns
      val cellsInRange = rowsInRange.map(row => EachRow(row.cells.slice(startC, endC+1)))

      EachTable(cellsInRange)
    }
    catch{
      case e: Exception =>
        throw new Exception(e.getMessage)
    }
  }
}
