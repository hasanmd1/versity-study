package filter

import dataModel.asciiImage.GrayScaleImage
import dataModel.gridImage.GrayScaleGrid
import dataModel.pixelImage.GrayScalePixel
import filter.artFilter.FlipYFilter
import org.scalatest.FunSuite

class FlipYFilterTest extends FunSuite{
  test("Testing flipY successfully") {
    val inputArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(100), GrayScalePixel(150)),
      Seq(GrayScalePixel(200), GrayScalePixel(50)),
    )))
    val expectedArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(150), GrayScalePixel(100)),
      Seq(GrayScalePixel(50), GrayScalePixel(200)),
    )))
    val flipYTest = new FlipYFilter()

    assert(flipYTest.filters(inputArt) == expectedArt)
  }
  test("Testing flipY for 1 column successfully") {
    val inputArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(100)),
      Seq(GrayScalePixel(150)),
    )))
    val expectedArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(100)),
      Seq(GrayScalePixel(150))
    )))
    val flipYTest = new FlipYFilter()

    assert(flipYTest.filters(inputArt) == expectedArt)
  }
  test("Testing flipY for empty image successfully") {
    val inputArt = GrayScaleImage(GrayScaleGrid(Seq.empty[Seq[GrayScalePixel]])) //empty
    val expectedArt = GrayScaleImage(GrayScaleGrid(Seq.empty[Seq[GrayScalePixel]]))
    val flipYTest = new FlipYFilter()

    assert(flipYTest.filters(inputArt) == expectedArt)
  }

}
