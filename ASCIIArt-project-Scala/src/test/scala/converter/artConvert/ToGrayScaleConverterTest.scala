package converter.artConvert

import converter.artConversion.ToGrayScaleConverter
import dataModel.asciiImage.{GrayScaleImage, RGBImage}
import dataModel.gridImage.RGBGrid
import dataModel.pixelImage.RGBPixel
import org.scalatest.FunSuite

import scala.collection.immutable.Seq
import scala.util.Random

class ToGrayScaleConverterTest extends FunSuite{

  test("Successfully Conversion to GrayScaleImage"){
    val converter = new ToGrayScaleConverter()
    val randomizer = Random
    val image = RGBImage(RGBGrid(Seq(
      Seq(RGBPixel(randomizer.nextInt(255), randomizer.nextInt(255), randomizer.nextInt(255))),
      Seq(RGBPixel(randomizer.nextInt(255), randomizer.nextInt(255), randomizer.nextInt(255))),
      Seq(RGBPixel(randomizer.nextInt(255), randomizer.nextInt(255), randomizer.nextInt(255))),
      Seq(RGBPixel(randomizer.nextInt(255), randomizer.nextInt(255), randomizer.nextInt(255))),

    )))
    val convertedImage: GrayScaleImage = converter.convertArt(image)
    for (i <- 0 until convertedImage.getHeight){
      for(j <- 0 until convertedImage.getWidth){
        assert(getGrayScaleValueCalculation(image.getPixelValues(i,j)).equals(convertedImage.getPixelValues(i,j).grayScaleValue))

      }
    }
  }

  private def getGrayScaleValueCalculation(rgbPixel: RGBPixel): Int = {
    ((0.3 * rgbPixel.red) + (0.59 * rgbPixel.green) + (0.11 * rgbPixel.blue)).toInt
  }

}
