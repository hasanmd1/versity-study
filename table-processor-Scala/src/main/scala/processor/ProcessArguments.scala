package processor

import filters.filter.{Filter, FilterEmpty, FilterNonEmpty}
import filters.header.Header
import filters.range.RangeSpecifier
import model.dataModel.{EachCell, EachRow, EachTable}


class ProcessArguments(table: EachTable[Any], filters: List[String]) extends Processor[EachTable[Any]] {

  override def applyFilters(): EachTable[Any] = {
    var processedTable = formulateTable()
    try {

      if (filters.contains("--filter")){
        processedTable = new Filter(processedTable, filters(filters.indexOf("--filter")+1)).apply()
      }
      if (filters.contains("--filter-is-empty")){
        processedTable = new FilterEmpty(processedTable, filters(filters.indexOf("--filter-is-empty")+1)).apply()

      }
      if (filters.contains("--filter-is-not-empty")){
        processedTable = new FilterNonEmpty(processedTable, filters(filters.indexOf("--filter-is-not-empty")+1)).apply()
      }
      if(filters.contains("--range")){
        processedTable = new RangeSpecifier(processedTable, filters(filters.indexOf("--range")+1)).apply()
      }
      if(filters.contains("--headers")){
        processedTable = new Header(processedTable).apply()
      }
      processedTable
    }
    catch {
      case e: Exception =>
        throw new Exception(e.getMessage)
    }
  }

  private def formulateTable(): EachTable[Any] = {
    val numRows = table.getRowCount
    val numColumns = table.getWidth


    val newRows = (0 until numRows).map { rowIndex =>
      val currentRow = table.getRow(rowIndex)


      val newCells = (0 until numColumns).map { columnIndex =>
        val currentCell = currentRow.getCell(columnIndex)


        val processedCell = currentCell match {
          case cell: EachCell[Any] if isFormula(cell) =>
            processFormula(cell)
          case _ =>
            currentCell
        }
        processedCell
      }

      EachRow[Any](newCells.toVector)
    }

    EachTable[Any](newRows.toList)
  }

  private def isFormula(cell: EachCell[Any]): Boolean = {
    cell.value.toString.startsWith("=")
  }

  private def processFormula(cell: EachCell[Any]): EachCell[Any] = {
    val bTree = new BinaryTreeFormAndEvaluation(table)
    EachCell[Any](bTree.evaluateTree(bTree.formBinaryTree(cell.value.toString.trim.replaceAll(" ", "").replaceAll("=", ""))), "String")
  }
}
