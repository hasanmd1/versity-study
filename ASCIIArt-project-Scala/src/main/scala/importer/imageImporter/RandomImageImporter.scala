package importer.imageImporter

import dataModel.asciiImage.RGBImage
import dataModel.gridImage.RGBGrid
import dataModel.pixelImage.RGBPixel
import importer.ImageImporter

import scala.util.Random

class RandomImageImporter extends ImageImporter[RGBImage] {
  /**
   * Generates random values of int for grid and RGB values
   * and loads randomImage
   *
   * @return
   */
  override def importImage(): RGBImage = {
    val random = Random

    val height = random.nextInt(500)
    val width = random.nextInt(500)

    val newGrid = (0 until height).map { _ =>
      (0 until width).map { _ =>
        RGBPixel(random.nextInt(255), random.nextInt(255), random.nextInt(255))
      }
    }

    RGBImage(RGBGrid(newGrid))
  }
}
