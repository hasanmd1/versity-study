package dataModel.gridImage

/**
 *
 * @tparam T generic Type
 */
trait AnyGrid[T] {

  /**
   * Used getting rows, columns
   * @param row input
   * @param column input
   * @return
   */
  def getRowColumnValue(row: Int, column: Int): T

  /**
   * Used for getting total rows
   * @return
   */
  def getNumberOfRow: Int

  /**
   * Used for getting total columns
   * @return
   */
  def getNumberOfColumn: Int

}
