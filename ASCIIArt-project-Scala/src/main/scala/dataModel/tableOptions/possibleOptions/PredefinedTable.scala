package dataModel.tableOptions.possibleOptions

import dataModel.tableOptions.Table

case class PredefinedTable () extends Table[String]{
  /**
   * used for table name
   *
   * @return
   */
  override def tableName: String = "predefinedTable"

  /**
   * used for table Sequence of String
   *
   * @return
   */
  override def tableContainingString: String = " .:-=+*#%@"
}
