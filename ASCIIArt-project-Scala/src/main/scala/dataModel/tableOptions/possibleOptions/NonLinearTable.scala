package dataModel.tableOptions.possibleOptions

import dataModel.tableOptions.Table

case class NonLinearTable () extends Table[String] {
  /**
   * used for table name
   *
   * @return
   */
  override def tableName: String = "non-linear"

  /**
   * used for table Sequence of String
   *
   * @return
   */
  override def tableContainingString: String = ".=+*#@"
}
