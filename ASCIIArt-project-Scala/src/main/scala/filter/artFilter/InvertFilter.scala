package filter.artFilter

import dataModel.asciiImage.GrayScaleImage
import dataModel.gridImage.GrayScaleGrid
import dataModel.pixelImage.GrayScalePixel
import filter.Filter

class InvertFilter extends Filter[GrayScaleImage] {
  /**
   * applying filters method
   *
   * @param image input
   * @return
   */

  override def filters(image: GrayScaleImage): GrayScaleImage = {
    val newGrid = (0 until image.getHeight).map { i =>
      (0 until image.getWidth).map { j =>
        val grayScaleValue = 255 - image.getPixelValues(i, j).grayScaleValue
        GrayScalePixel(grayScaleValue)
      }
    }

    GrayScaleImage(GrayScaleGrid(newGrid))
  }

}
