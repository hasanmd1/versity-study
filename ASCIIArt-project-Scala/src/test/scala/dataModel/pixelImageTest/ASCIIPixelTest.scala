package dataModel.pixelImageTest

import dataModel.pixelImage.ASCIIPixel
import org.scalatest.FunSuite

class ASCIIPixelTest extends FunSuite{
  test("Test ASCIIPixel should store char value correctly") {
    val asciiPixelTest = '&'

    val pixel = ASCIIPixel(asciiPixelTest)

    assert(pixel.character == '&')
  }

  test("Test ASCIIPixel should return the correct RGB values") {
    val asciiPixelTest = '2'

    val pixel = ASCIIPixel(asciiPixelTest)

    val expectedRGB = '2'
    assert(pixel.character == expectedRGB)
  }

}
