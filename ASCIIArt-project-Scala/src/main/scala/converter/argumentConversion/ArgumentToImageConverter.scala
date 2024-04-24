package converter.argumentConversion

import converter.ArgumentConverter
import dataModel.asciiImage.RGBImage
import dataModel.gridImage.RGBGrid
import dataModel.pixelImage.RGBPixel
import importer.imageImporter.{InputImageImporter, RandomImageImporter}
import userInterface.argumentsRelatedMessagePresenter.console.ConsoleArgumentPresenter

class ArgumentToImageConverter extends ArgumentConverter[Map[String, Any], RGBImage] {
  /**
   * abstract method to handle the argument conversion
   *
   * @param mappedValues map of args
   * @return
   */
  override def convertArgumentTo(mappedValues: Map[String, Any]): RGBImage = {

    val consoleArgumentPresenter = new ConsoleArgumentPresenter
    val imagePath = mappedValues.get("--image")
    val randomImage = mappedValues.get("--image-random")
    var rawImage: RGBImage = RGBImage(RGBGrid(Seq.empty[Seq[RGBPixel]]))

    if ((imagePath.isDefined && randomImage.isDefined) || (imagePath.isEmpty && randomImage.isEmpty)) {
      consoleArgumentPresenter.throwingError("Error. Arguments contains --image and --image-random at the same time")
    }
    else if (imagePath.isDefined) {
      rawImage = new InputImageImporter(imagePath.get.toString).importImage()
    }
    else if (randomImage.isDefined) {
      rawImage = new RandomImageImporter().importImage()
    }
    rawImage
  }
}
