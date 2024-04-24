package filter.artFilter

import dataModel.asciiImage.GrayScaleImage
import dataModel.gridImage.GrayScaleGrid
import dataModel.pixelImage.GrayScalePixel
import filter.Filter
import userInterface.argumentsRelatedMessagePresenter.console.ConsoleArgumentPresenter

class ScaleFilter (scaleFactor: Double) extends Filter[GrayScaleImage] {
  /**
   * applying filters method
   *
   * @param image input
   * @return
   */
  override def filters(image: GrayScaleImage): GrayScaleImage = {
    val consoleArgumentPresenter = new ConsoleArgumentPresenter

    val newScaleFactor: Double =
      if (scaleFactor.<(0)) {
        consoleArgumentPresenter.throwingError("Invalid argument value.\n")
        scaleFactor
      } else if (scaleFactor != 1.0) {
        if (scaleFactor.<(1)) scaleFactor * 2 else scaleFactor / 2
      } else scaleFactor



    val scaledWidth = (image.getWidth * newScaleFactor).toInt
    val scaledHeight = (image.getHeight * newScaleFactor).toInt

    val newGrid = (0 until scaledHeight).map { i =>
      (0 until scaledWidth).map { j =>
        val origI = (i / newScaleFactor).toInt
        val origJ = (j / newScaleFactor).toInt
        image.getPixelValues(origI, origJ)
      }
    }
    GrayScaleImage(GrayScaleGrid(newGrid))

  }
}
