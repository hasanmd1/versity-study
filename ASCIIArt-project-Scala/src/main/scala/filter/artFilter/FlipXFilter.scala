package filter.artFilter

import dataModel.asciiImage.GrayScaleImage
import dataModel.gridImage.GrayScaleGrid
import dataModel.pixelImage.GrayScalePixel
import filter.Filter

class FlipXFilter extends Filter[GrayScaleImage] {
  /**
   * applying filters method
   *
   * @param image input
   * @return
   */
  override def filters(image: GrayScaleImage): GrayScaleImage = {
    val newGrid = (0 until image.getHeight).map(i => image.getWholeRow(image.getHeight - 1 - i))
    GrayScaleImage(GrayScaleGrid(newGrid))
  }
}
