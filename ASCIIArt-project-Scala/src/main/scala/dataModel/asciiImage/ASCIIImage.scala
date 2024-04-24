package dataModel.asciiImage

import dataModel.gridImage.ASCIIGrid
import dataModel.pixelImage.ASCIIPixel

case class ASCIIImage(grid: ASCIIGrid) extends AnyImage[ASCIIPixel] {
  /**
   * used to get a certain co-ordinate of pixel
   *
   * @param xAxis input
   * @param yAxis input
   * @return generic T
   */
  override def getPixelValues(xAxis: Int, yAxis: Int): ASCIIPixel = {
    grid.getRowColumnValue(xAxis, yAxis)
  }

  /**
   * used to get a row of pixels ------
   *
   * @param row input
   * @return Sequence of Generic T
   */
  override def getWholeRow(row: Int): Seq[ASCIIPixel] = {
    grid.grid(row)
  }

  /**
   * used to get total height of image
   *
   * @return
   */
  override def getHeight: Int = {
    grid.getNumberOfRow
  }

  /**
   * used to get total width
   *
   * @return
   */
  override def getWidth: Int = {
    grid.getNumberOfColumn
  }
}
