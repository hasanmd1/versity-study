package dataModel.tableOptions.possibleOptions

import dataModel.tableOptions.Table

case class CustomTable () extends Table[String] {
  private var _tableContainingString: String = ""
  /**
   * used for table name
   *
   * @return
   */
  override def tableName: String = "custom-table"

  /**
   * used for table Sequence of String
   *
   * @return
   */
  override def tableContainingString: String = _tableContainingString

  def setTableContainingString(value: String): Unit = {
    _tableContainingString = value
  }
}
