package dataModel.pixelImageTest

import dataModel.pixelImage.GrayScalePixel
import org.scalatest.FunSuite

class GrayScalePixelTest extends FunSuite{
  test("Test GrayScalePixel should store grayScale value correctly") {
    val grayScalePixelTest = 175

    val pixel = GrayScalePixel(grayScalePixelTest)

    assert(pixel.grayScaleValue == grayScalePixelTest)
  }

  test("Test GrayScalePixel should return the correct GrayScale value") {
    val grayScalePixelTest = -65

    val pixel = GrayScalePixel(grayScalePixelTest)

    val expectedRGB = -65
    assert(pixel.grayScaleValue == expectedRGB)
  }

}
