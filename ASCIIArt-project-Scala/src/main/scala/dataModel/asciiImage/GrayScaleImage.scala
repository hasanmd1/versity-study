package dataModel.asciiImage

import dataModel.gridImage.GrayScaleGrid
import dataModel.pixelImage.GrayScalePixel

case class GrayScaleImage(grid: GrayScaleGrid) extends AnyImage[GrayScalePixel] {
  /**
   * used to get a certain co-ordinate of pixel
   *
   * @param xAxis input
   * @param yAxis input
   * @return generic T
   */
  override def getPixelValues(xAxis: Int, yAxis: Int): GrayScalePixel = {

    grid.getRowColumnValue(xAxis, yAxis)
  }

  /**
   * used to get a row of pixels ------
   *
   * @param row input
   * @return Sequence of Generic T
   */
  override def getWholeRow(row: Int): Seq[GrayScalePixel] = grid.grid(row)

  /**
   * used to get total height of image
   *
   * @return
   */
  override def getHeight: Int = grid.getNumberOfRow

  /**
   * used to get total width
   *
   * @return
   */
  override def getWidth: Int = grid.getNumberOfColumn
}
