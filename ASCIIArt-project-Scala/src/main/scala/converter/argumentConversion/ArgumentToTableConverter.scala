package converter.argumentConversion

import converter.ArgumentConverter
import dataModel.tableOptions.Table
import dataModel.tableOptions.possibleOptions.{BourkeSmallTable, BourkeTable, CustomTable, NonLinearTable, PredefinedTable}
import userInterface.argumentsRelatedMessagePresenter.console.ConsoleArgumentPresenter

class ArgumentToTableConverter extends ArgumentConverter[Map[String, Any], Table[String]] {
  /**
   * abstract method to handle the argument conversion
   *
   * @param mappedValues map of args
   * @return
   */
  override def convertArgumentTo(mappedValues: Map[String, Any]): Table[String] = {

    val consoleArgumentPresenter = new ConsoleArgumentPresenter
    val tableString: Option[Any] = mappedValues.get("--custom-table")
    val tableName: Option[Any] = mappedValues.get("--table")
    var table: Table[String] = PredefinedTable()


    if (tableString.isDefined && tableName.isDefined) {
      consoleArgumentPresenter.throwingError("Error. Passing table and custom table arguments is not allowed")
    }
    else if (tableString.isEmpty && tableName.isEmpty) {
      table = new PredefinedTable
    }
    else if (tableString.isDefined) {
      val customTable = new CustomTable
      customTable.setTableContainingString(tableString.get.toString)
      table = customTable
    }
    else if (tableName.isDefined) {
      if (tableName.get.toString.toLowerCase.matches("bourke")) {
        table = new BourkeTable
      } else if (tableName.get.toString.toLowerCase.matches("bourke-small")) {
        table = new BourkeSmallTable
      }
      else if (tableName.get.toString.toLowerCase.matches("non-linear")){
        table = new NonLinearTable
      }
    } else {
      consoleArgumentPresenter.throwingError("Table could not be processed. Try defining --custom-table")
    }
    table
  }
}
