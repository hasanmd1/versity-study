package exporter.art

import dataModel.asciiImage.ASCIIImage
import exporter.Exporter

class ExportToConsole extends Exporter[ASCIIImage] {
  /**
   * export image Art
   *
   * @param image which image will be exported
   */
  override def exportArt(image: ASCIIImage): Unit = {
    val output = (0 until image.getHeight).map { i =>
      (0 until image.getWidth).map { j =>
        image.getPixelValues(i, j).character
      }.mkString
    }.mkString("\n")

    print(output)
  }
}
