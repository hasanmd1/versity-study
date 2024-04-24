package dataModel.gridImage

import dataModel.pixelImage.ASCIIPixel

case class ASCIIGrid(grid: Seq[Seq[ASCIIPixel]]) extends AnyGrid[ASCIIPixel] {
  /**
   * Used getting rows, columns
   *
   * @param row input
   * @param column input
   * @return
   */
  override def getRowColumnValue(row: Int, column: Int): ASCIIPixel = {
    grid(row)(column)
  }

  /**
   * Used for getting total rows
   *
   * @return
   */
  override def getNumberOfRow: Int = {
    grid.length
  }

  /**
   * Used for getting total columns
   *
   * @return
   */
  override def getNumberOfColumn: Int = {
    grid.head.length
  }
}
