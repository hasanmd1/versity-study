package filter.artFilter

import dataModel.asciiImage.GrayScaleImage
import dataModel.gridImage.GrayScaleGrid
import dataModel.pixelImage.GrayScalePixel
import filter.Filter
import userInterface.argumentsRelatedMessagePresenter.console.ConsoleArgumentPresenter

class BrightnessFilter(brightnessVal: Int) extends Filter[GrayScaleImage] {
  /**
   * applying filters method
   *
   * @param image input
   * @return
   */
  override def filters(image: GrayScaleImage): GrayScaleImage = {
    if(!brightnessVal.isValidInt){
      new ConsoleArgumentPresenter().throwingError("Invalid brightness value.\n")
    }

    val newGrid = (0 until image.getHeight).map { i =>
      (0 until image.getWidth).map { j =>
        val grayScaleValue = brightnessCalculation(image.getPixelValues(i, j).grayScaleValue)
        GrayScalePixel(grayScaleValue)
      }
    }

    GrayScaleImage(GrayScaleGrid(newGrid))
  }

  private def brightnessCalculation(grayScaleValue: Int): Int = {
    (grayScaleValue + brightnessVal) match {
      case newVal if newVal < 0 => 0
      case newVal if newVal > 255 => 255
      case newVal => newVal
    }
  }
}
