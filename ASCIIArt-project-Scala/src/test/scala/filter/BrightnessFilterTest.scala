package filter

import dataModel.asciiImage.GrayScaleImage
import dataModel.gridImage.GrayScaleGrid
import dataModel.pixelImage.GrayScalePixel
import filter.artFilter.BrightnessFilter
import org.scalatest.FunSuite

class BrightnessFilterTest extends FunSuite{

  test("Test Brightness filter for value - positive Int"){
    val inputArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(100), GrayScalePixel(150)),
      Seq(GrayScalePixel(200), GrayScalePixel(50)),
    )))

    val filter = new BrightnessFilter(50)
    val resultArt = filter.filters(inputArt)

    val expectedArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(150), GrayScalePixel(200)),
      Seq(GrayScalePixel(250), GrayScalePixel(100))
    )))

    assert(resultArt == expectedArt)
  }
  test("Test Brightness filter for value - negative Int") {
    val inputArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(100), GrayScalePixel(150)),
      Seq(GrayScalePixel(200), GrayScalePixel(50)),
    )))

    val filter = new BrightnessFilter(-50)
    val resultArt = filter.filters(inputArt)

    val expectedArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(50), GrayScalePixel(100)),
      Seq(GrayScalePixel(150), GrayScalePixel(0))
    )))

    assert(resultArt == expectedArt)
  }
  test("Test Brightness filter for value - > 255 Int") {
    val inputArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(100), GrayScalePixel(150)),
      Seq(GrayScalePixel(210), GrayScalePixel(50)),
    )))

    val filter = new BrightnessFilter(50)
    val resultArt = filter.filters(inputArt)

    val expectedArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(150), GrayScalePixel(200)),
      Seq(GrayScalePixel(255), GrayScalePixel(100))
    )))

    assert(resultArt == expectedArt)
  }
  test("Test Brightness filter for value - < 0 Int") {
    val inputArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(100), GrayScalePixel(150)),
      Seq(GrayScalePixel(200), GrayScalePixel(40)),
    )))

    val filter = new BrightnessFilter(-50)
    val resultArt = filter.filters(inputArt)

    val expectedArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(50), GrayScalePixel(100)),
      Seq(GrayScalePixel(150), GrayScalePixel(0))
    )))

    assert(resultArt == expectedArt)
  }
}
