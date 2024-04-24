package dataModel.asciiImage

import dataModel.pixelImage.AnyPixel

/**
 * used for pixel related operations subtyping
 *
 * @tparam T generic type
 */
trait AnyImage[T <: AnyPixel] {

  /**
   * used to get a certain co-ordinate of pixel
   * @param xAxis input
   * @param yAxis input
   * @return generic T
   */
  def getPixelValues(xAxis: Int, yAxis: Int): T

  /**
   * used to get a row of pixels ------
   * @param row input
   * @return Sequence of Generic T
   */
  def getWholeRow(row: Int): Seq[T]

  /**
   * used to get total height of image
   * @return
   */
  def getHeight: Int

  /**
   * used to get total width
   * @return
   */
  def getWidth: Int
}
