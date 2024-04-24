package dataModel.gridImage

import dataModel.pixelImage.RGBPixel

case class RGBGrid(grid: Seq[Seq[RGBPixel]]) extends AnyGrid[RGBPixel] {
  /**
   * Used getting rows, columns
   *
   * @param row input
   * @param column input
   * @return
   */
  override def getRowColumnValue(row: Int, column: Int): RGBPixel = {
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
   * Used for getting total columns(grid.head will get us
   * Seq[RGBPixel] )
   *
   * @return
   */
  override def getNumberOfColumn: Int = {
    grid.head.length
  }
}
