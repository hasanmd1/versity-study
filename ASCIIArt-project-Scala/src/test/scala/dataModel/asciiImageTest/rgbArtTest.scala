package dataModel.asciiImageTest

import dataModel.asciiImage.RGBImage
import dataModel.gridImage.RGBGrid
import dataModel.pixelImage.RGBPixel
import org.scalatest.FunSuite

class rgbArtTest extends FunSuite{
  test("Testing RGBArt should return the correct pixel values at a given co-ordinate") {
    val pixel1 = RGBPixel(255, 0, 0)
    val pixel2 = RGBPixel(0, 255, 0)
    val pixel3 = RGBPixel(0, 0, 255)
    val grid = RGBGrid(Seq(
      Seq(pixel1, pixel2),
      Seq(pixel3)
    ))

    val rgbArt = RGBImage(grid)

    assert(rgbArt.getPixelValues(0, 0) == pixel1)
    assert(rgbArt.getPixelValues(0, 1) == pixel2)
    assert(rgbArt.getPixelValues(1, 0) == pixel3)
  }

  test("Testing RGBArt should return the correct whole row of pixels") {
    val pixel1 = RGBPixel(255, 0, 0)
    val pixel2 = RGBPixel(0, 255, 0)
    val pixel3 = RGBPixel(0, 0, 255)
    val grid = RGBGrid(Seq(
      Seq(pixel1, pixel2),
      Seq(pixel3)
    ))

    val rgbArt = RGBImage(grid)

    assert(rgbArt.getWholeRow(0) == Seq(pixel1, pixel2))
    assert(rgbArt.getWholeRow(1) == Seq(pixel3))
  }

  test("Testing RGBArt should return the correct height of the image") {
    val grid = RGBGrid(Seq(
      Seq(RGBPixel(255, 0, 0)),
      Seq(RGBPixel(0, 255, 0)),
      Seq(RGBPixel(0, 0, 255))
    ))

    val rgbArt = RGBImage(grid)

    assert(rgbArt.getHeight == 3)
  }

  test("Testing RGBArt should return the correct width of the image") {
    val grid = RGBGrid(Seq(
      Seq(RGBPixel(255, 0, 0), RGBPixel(0, 255, 0)),
      Seq(RGBPixel(0, 0, 255), RGBPixel(128, 128, 128))
    ))

    val rgbArt = RGBImage(grid)

    assert(rgbArt.getWidth == 2)
  }
}
