package converter.artConversion

import converter.ArtConverter
import dataModel.asciiImage.{GrayScaleImage, RGBImage}
import dataModel.gridImage.GrayScaleGrid
import dataModel.pixelImage.{GrayScalePixel, RGBPixel}

class ToGrayScaleConverter extends ArtConverter[RGBImage, GrayScaleImage] {
  /**
   * convertsImage A to B
   *
   * @param image input
   * @return converted image
   */
//  override def convertArt(image: RGBArt): GrayScaleArt = {
//
//    var newGrid = Seq[Seq[GrayScalePixel]]()
//    for(i <- 0 until image.getHeight){
//      var row = Seq[GrayScalePixel]()
//      for(j <- 0 until image.getWidth){
//        row = row.appended(GrayScalePixel(grayScaleFormula(image.getPixelValues(i, j))))
//      }
//      newGrid = newGrid.appended(row)
//    }
//    GrayScaleArt(GrayScaleGrid(newGrid))
//  }
  override def convertArt(image: RGBImage): GrayScaleImage = {
    val newGrid = (0 until image.getHeight).map { i =>
      (0 until image.getWidth).map { j =>
        GrayScalePixel(grayScaleFormula(image.getPixelValues(i, j)))
      }
    }

    GrayScaleImage(GrayScaleGrid(newGrid))
  }

  private def grayScaleFormula(pixel: RGBPixel): Int = {
    ((0.3 * pixel.red) + (0.59 * pixel.green) + (0.11 * pixel.blue)).toInt
  }
}
