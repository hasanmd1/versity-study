package dataModel.tableOptions.possibleOptions

import dataModel.tableOptions.Table

case class BourkeTable() extends Table[String] {
  /**
   * used for table name
   *
   * @return
   */
  override def tableName: String = "bourke"

  /**
   * used for table Sequence of String
   *
   * @return
   */
  override def tableContainingString: String = "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\"^`'. "
}
