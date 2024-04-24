package filter.artFilter

import dataModel.asciiImage.GrayScaleImage
import dataModel.gridImage.GrayScaleGrid
import dataModel.pixelImage.GrayScalePixel
import filter.Filter
import userInterface.argumentsRelatedMessagePresenter.console.ConsoleArgumentPresenter

class RotateFilter(angle: Double) extends Filter[GrayScaleImage] {

  /**
   * applying filters method
   *
   * @param image input
   * @return
   */
  override def filters(image: GrayScaleImage): GrayScaleImage = {

    if (angle % 90 != 0){
      new ConsoleArgumentPresenter().throwingError("Only modulus of 90 rotate values allowed")
    }

    val newAngle = (angle % 360 + 360) % 360 // Normalize angle to be in [0, 360)


    newAngle match {
      case 90.0 => transformVertical90(image)
      case 270.0 => transformVertical270(image)
      case 180.0 => transformHorizontal(image)
      case _ => image
    }
  }


  private def transformVertical90(image: GrayScaleImage): GrayScaleImage = {

    GrayScaleImage(GrayScaleGrid((0 until image.getWidth).map(i =>
      (0 until image.getHeight).map(j =>
        image.getPixelValues(j, i)
      )
    ).map(_.reverse)))
  }

  private def transformVertical270(image: GrayScaleImage): GrayScaleImage = {
    GrayScaleImage(GrayScaleGrid((image.getWidth - 1 to 0 by -1).map(i =>
      (0 until image.getHeight).map(j =>
        image.getPixelValues(j, i)
      )
    )))
  }

  private def transformHorizontal(image: GrayScaleImage): GrayScaleImage = {
    GrayScaleImage(GrayScaleGrid((image.getHeight - 1 to 0 by -1).map(i =>
      (image.getWidth - 1 to 0 by -1).map(j =>
        image.getPixelValues(i, j)
      )
    )))
  }
}
