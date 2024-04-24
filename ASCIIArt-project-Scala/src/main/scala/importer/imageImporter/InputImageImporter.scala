package importer.imageImporter

import dataModel.asciiImage.RGBImage
import dataModel.gridImage.RGBGrid
import dataModel.pixelImage.RGBPixel
import importer.ImageImporter
import userInterface.argumentsRelatedMessagePresenter.console.ConsoleArgumentPresenter

import java.awt.Color
import java.io.File
import javax.imageio.ImageIO

class InputImageImporter(imagePath: String) extends ImageImporter[RGBImage] {
  /**
   * Loads image of type .jpg, .gif, .png
   * otherwise throws an error
   *
   * @return
   */
  override def importImage(): RGBImage = {

    val presentationDirection = new ConsoleArgumentPresenter


    val imageFile = new File(imagePath)
    if(!imageFile.exists()){
      presentationDirection.throwingError(s"File does not exist on this path: $imagePath")
    }
    val supportedExtensions = List(".jpg", ".png", ".gif")
    val extensions = imageFile.getName.substring(imageFile.getName.lastIndexOf("."))
    if(!supportedExtensions.contains(extensions.toLowerCase)){
      presentationDirection.throwingError("Invalid extension of image. Only .jpg, .png, .gif format are supported.")
    }
    extensions match {
      case ".jpg" => importJPG(imageFile)
      case ".png" => importPNG(imageFile)
      case ".git" => importGIF(imageFile)
      /*
      * can be extended for other formats with related methods
       */
    }

  }

  private def importPNG(imageFile: File): RGBImage = {
    loadImage(imageFile)
  }
  private def importJPG(imageFile: File): RGBImage = {
    loadImage(imageFile)
  }
  private def importGIF(imageFile: File): RGBImage = {
    loadImage(imageFile)
  }

  private def loadImage(imageFile: File) : RGBImage = {
    val buffer = ImageIO.read(imageFile)
    if (buffer.getWidth == 0 && buffer.getHeight == 0) {
      new ConsoleArgumentPresenter().throwingError("Image is empty")
    }
    val newGrid = (0 until buffer.getHeight).map { i =>
      (0 until buffer.getWidth).map { j =>
        val color = new Color(buffer.getRGB(j, i))
        RGBPixel(color.getRed, color.getGreen, color.getBlue)
      }
    }

    RGBImage(RGBGrid(newGrid))
  }
}
