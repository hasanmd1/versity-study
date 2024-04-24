package dataModel.asciiImageTest

import dataModel.asciiImage.ASCIIImage
import dataModel.gridImage.ASCIIGrid
import dataModel.pixelImage.ASCIIPixel
import org.scalatest.FunSuite

class asciiArtTest extends FunSuite{
  test("Testing ASCIIArt should return the correct pixel values at a given co-ordinate") {
    val pixel1 = ASCIIPixel('#')
    val pixel2 = ASCIIPixel(' ')
    val pixel3 = ASCIIPixel('*')
    val grid = ASCIIGrid(Seq(
      Seq(pixel1, pixel2),
      Seq(pixel3)
    ))

    val asciiArt = ASCIIImage(grid)

    assert(asciiArt.getPixelValues(0, 0) == pixel1)
    assert(asciiArt.getPixelValues(0, 1) == pixel2)
    assert(asciiArt.getPixelValues(1, 0) == pixel3)
  }

  test("Testing ASCIIArt should return the correct whole row of pixels") {
    val pixel1 = ASCIIPixel('#')
    val pixel2 = ASCIIPixel(' ')
    val pixel3 = ASCIIPixel('*')
    val grid = ASCIIGrid(Seq(
      Seq(pixel1, pixel2),
      Seq(pixel3)
    ))

    val asciiArt = ASCIIImage(grid)

    assert(asciiArt.getWholeRow(0) == Seq(pixel1, pixel2))
    assert(asciiArt.getWholeRow(1) == Seq(pixel3))
  }

  test("Testing ASCIIArt should return the correct height of the image") {
    val grid = ASCIIGrid(Seq(
      Seq(ASCIIPixel('#')),
      Seq(ASCIIPixel(' ')),
      Seq(ASCIIPixel('*'))
    ))

    val asciiArt = ASCIIImage(grid)

    assert(asciiArt.getHeight == 3)
  }

  test("Testing ASCIIArt should return the correct width of the image") {
    val grid = ASCIIGrid(Seq(
      Seq(ASCIIPixel('#'), ASCIIPixel(' ')),
      Seq(ASCIIPixel('*'), ASCIIPixel('+'))
    ))

    val asciiArt = ASCIIImage(grid)

    assert(asciiArt.getWidth == 2)
  }
}
