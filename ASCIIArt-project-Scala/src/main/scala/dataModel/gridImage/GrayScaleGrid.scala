package dataModel.gridImage

import dataModel.pixelImage.GrayScalePixel

case class GrayScaleGrid(grid: Seq[Seq[GrayScalePixel]]) extends AnyGrid[GrayScalePixel] {
  /**
   * Used getting rows, columns
   *
   * @param row input
   * @param column input
   * @return
   */
  override def getRowColumnValue(row: Int, column: Int): GrayScalePixel = {
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
