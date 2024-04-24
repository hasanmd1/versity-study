package dataModel.tableOptions

/**
 * used for table
 *
 * @tparam A generic
 *
 */
trait Table[A] {

  /**
   * used for table name
   *
   * @return
   */
  def tableName: A

  /**
   * used for table Sequence of String
   *
   * @return
   */
  def tableContainingString: A
}
