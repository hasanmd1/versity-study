package filters.filter

import filters.AllFilter
import model.dataModel.{EachRow, EachTable}

import scala.collection.mutable.ListBuffer

class Filter(table: EachTable[Any], filters: String) extends AllFilter[EachTable[Any]] {

  override def apply(): EachTable[Any] = {
    val filterComponents = filters.split(" ")
    if (filterComponents.length != 3) {
      throw new IllegalArgumentException(s"Invalid filter format: " + filters + s"${filterComponents.length}")
    }

    try{

      val column = filterComponents(0).charAt(0).toUpper - 'A'
      val operation = filterComponents(1)
      val value = filterComponents(2).toInt

      val filteredRowsBuffer = new ListBuffer[EachRow[Any]]()
      for (row <- table.rows) {
        if (checkCondition(row, column, operation, value)) {
          filteredRowsBuffer += row
        }
      }
      EachTable[Any](filteredRowsBuffer.toList)
    }
    catch {
      case e: Exception =>
        throw new IllegalArgumentException(e.getMessage)
    }
  }

  private def checkCondition(row: EachRow[Any], column: Int, operation: String, value: Int): Boolean = {
    val cellValue = getValue(row, column)

    operation match {
      case "<"  => cellValue < value
      case ">"  => cellValue > value
      case ">=" => cellValue >= value
      case "<=" => cellValue <= value
      case "==" => cellValue == value
      case "!=" => cellValue != value
      //can be extended
      case _    => throw new IllegalArgumentException("Unsupported operation: " + operation)
    }
  }

  private def getValue(row: EachRow[Any], column: Int): Int = {
    try{
      row.getCell(column).getValue.asInstanceOf[Int]
    }
    catch{
      case e: Exception =>
        if(row.getCell(column).getValue.toString.isEmpty){
          return 0
        }
        throw new Exception(e.getMessage)
    }
  }
}
