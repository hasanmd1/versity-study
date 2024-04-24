package filter

import dataModel.asciiImage.GrayScaleImage
import dataModel.gridImage.GrayScaleGrid
import dataModel.gridImageTest.GrayScaleGridTest
import dataModel.pixelImage.GrayScalePixel
import filter.artFilter.FlipXFilter
import org.scalatest.FunSuite

class FlipXFilterTest extends FunSuite{

  test("Testing flipX successfully"){
    val inputArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(100), GrayScalePixel(150)),
      Seq(GrayScalePixel(200), GrayScalePixel(50)),
    )))
    val expectedArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(200), GrayScalePixel(50)),
      Seq(GrayScalePixel(100), GrayScalePixel(150)),
    )))
    val flipXTest = new FlipXFilter()

    assert(flipXTest.filters(inputArt)==expectedArt)
  }
  test("Testing flipX for 1 row successfully") {
    val inputArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(100), GrayScalePixel(150)),
    )))
    val expectedArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(100), GrayScalePixel(150)),
    )))
    val flipXTest = new FlipXFilter()

    assert(flipXTest.filters(inputArt) == expectedArt)
  }
  test("Testing flipX for empty image successfully") {
    val inputArt = GrayScaleImage(GrayScaleGrid(Seq.empty[Seq[GrayScalePixel]])) //empty
    val expectedArt = GrayScaleImage(GrayScaleGrid(Seq.empty[Seq[GrayScalePixel]]))
    val flipXTest = new FlipXFilter()

    assert(flipXTest.filters(inputArt) == expectedArt)
  }


}
