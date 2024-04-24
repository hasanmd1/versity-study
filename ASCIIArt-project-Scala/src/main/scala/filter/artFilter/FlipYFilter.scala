package filter.artFilter

import dataModel.asciiImage.GrayScaleImage
import dataModel.gridImage.GrayScaleGrid
import dataModel.pixelImage.GrayScalePixel
import filter.Filter

class FlipYFilter extends Filter[GrayScaleImage] {
  /**
   * applying filters method
   *
   * @param image input
   * @return
   */
  override def filters(image: GrayScaleImage): GrayScaleImage = {
    val newGrid = (0 until image.getHeight).map(i => image.getWholeRow(i).reverse)
    GrayScaleImage(GrayScaleGrid(newGrid))
  }
}
