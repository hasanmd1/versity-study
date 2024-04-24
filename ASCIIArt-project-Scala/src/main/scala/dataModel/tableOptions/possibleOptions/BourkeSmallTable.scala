package dataModel.tableOptions.possibleOptions

import dataModel.tableOptions.Table

case class BourkeSmallTable() extends Table[String] {
  /**
   * used for table name
   *
   * @return
   */
  override def tableName: String = "bourke-small"

  /**
   * used for table Sequence of String
   *
   * @return
   */
  override def tableContainingString: String = " .:-=+*#%@"
}
