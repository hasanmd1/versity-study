package dataModel.asciiImageTest

import dataModel.asciiImage.GrayScaleImage
import dataModel.gridImage.GrayScaleGrid
import dataModel.pixelImage.GrayScalePixel
import org.scalatest.FunSuite

class grayScaleArtTest extends FunSuite{
  test("Testing GrayScaleArt should return the correct pixel values at a given co-ordinate") {
    val pixel1 = GrayScalePixel(100)
    val pixel2 = GrayScalePixel(200)
    val pixel3 = GrayScalePixel(150)
    val grid = GrayScaleGrid(Seq(
      Seq(pixel1, pixel2),
      Seq(pixel3)
    ))

    val grayScaleArt = GrayScaleImage(grid)

    assert(grayScaleArt.getPixelValues(0, 0) == pixel1)
    assert(grayScaleArt.getPixelValues(0, 1) == pixel2)
    assert(grayScaleArt.getPixelValues(1, 0) == pixel3)
  }

  test("Testing GrayScaleArt should return the correct whole row of pixels") {
    val pixel1 = GrayScalePixel(100)
    val pixel2 = GrayScalePixel(200)
    val pixel3 = GrayScalePixel(150)
    val grid = GrayScaleGrid(Seq(
      Seq(pixel1, pixel2),
      Seq(pixel3)
    ))

    val grayScaleArt = GrayScaleImage(grid)

    assert(grayScaleArt.getWholeRow(0) == Seq(pixel1, pixel2))
    assert(grayScaleArt.getWholeRow(1) == Seq(pixel3))
  }

  test("Testing GrayScaleArt should return the correct height of the image") {
    val grid = GrayScaleGrid(Seq(
      Seq(GrayScalePixel(100)),
      Seq(GrayScalePixel(200)),
      Seq(GrayScalePixel(150))
    ))

    val grayScaleArt = GrayScaleImage(grid)

    assert(grayScaleArt.getHeight == 3)
  }

  test("Testing GrayScaleArt should return the correct width of the image") {
    val grid = GrayScaleGrid(Seq(
      Seq(GrayScalePixel(100), GrayScalePixel(200)),
      Seq(GrayScalePixel(150), GrayScalePixel(180))
    ))

    val grayScaleArt = GrayScaleImage(grid)

    assert(grayScaleArt.getWidth == 2)
  }
}
