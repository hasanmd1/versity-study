package filters.filter

import filters.AllFilter
import model.dataModel.{EachRow, EachTable}

import scala.collection.mutable.ListBuffer

class FilterNonEmpty(table: EachTable[Any], filters: String) extends AllFilter[EachTable[Any]] {

  override def apply(): EachTable[Any] = {
    try{
      val column = filters.charAt(0) - 'A'
      if (column > table.getWidth){
        throw new Exception("Column cannot be bigger than table itself.\n")
      }

      val filteredRowsBuffer = new ListBuffer[EachRow[Any]]()
      for (row <- table.rows) {
        val cell = row.getCell(column).getValue.toString
        if (cell.nonEmpty) {
          filteredRowsBuffer += row
        }
      }
      EachTable[Any](filteredRowsBuffer.toList)
    }
    catch{
      case e: Exception =>
        throw new Exception("Wrong parameter.\n")
    }
  }
}
