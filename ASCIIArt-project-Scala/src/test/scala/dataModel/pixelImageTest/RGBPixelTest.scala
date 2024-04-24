package dataModel.pixelImageTest

import dataModel.pixelImage.RGBPixel
import org.scalatest.FunSuite

class RGBPixelTest extends FunSuite{
  test("Test RGBPixel should store red, green, and blue values correctly") {
    val redValue = 255
    val greenValue = 128
    val blueValue = 64

    val pixel = RGBPixel(redValue, greenValue, blueValue)

    assert(pixel.red == redValue)
    assert(pixel.green == greenValue)
    assert(pixel.blue == blueValue)
  }

  test("Test RGBPixel should return the correct RGB values") {
    val redValue = 200
    val greenValue = 0
    val blueValue = -50 //for pixel it can be - but we check those in particular filter

    val pixel = RGBPixel(redValue, greenValue, blueValue)

    val expectedRGB = (redValue, greenValue, blueValue)
    assert(pixel.red == expectedRGB._1)
    assert(pixel.green == expectedRGB._2)
    assert(pixel.blue == expectedRGB._3)
  }

}
