package filter

import dataModel.asciiImage.GrayScaleImage
import dataModel.gridImage.GrayScaleGrid
import dataModel.gridImageTest.GrayScaleGridTest
import dataModel.pixelImage.GrayScalePixel
import filter.artFilter.ScaleFilter
import org.scalatest.FunSuite

class ScaleFilterTest extends FunSuite{

  test("Test scale with factor 1.0"){
    val grayScaleArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(1), GrayScalePixel(7)),
      Seq(GrayScalePixel(3), GrayScalePixel(4)),
    )))
    val scaleTest = new ScaleFilter(1.0)
    val transformed = scaleTest.filters(grayScaleArt)

    assert(transformed.getPixelValues(0, 0).grayScaleValue == 1)
    assert(transformed.getPixelValues(0, 1).grayScaleValue == 7)
    assert(transformed.getPixelValues(1, 0).grayScaleValue == 3)
    assert(transformed.getPixelValues(1, 1).grayScaleValue == 4)
  }

  test("Test scale with factor 0.25") {
    val grayScaleArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(1), GrayScalePixel(7)),
      Seq(GrayScalePixel(3), GrayScalePixel(4)),
    )))
    val scaleTest = new ScaleFilter(0.25)
    val transformed = scaleTest.filters(grayScaleArt)

    assert(transformed.getPixelValues(0, 0).grayScaleValue == 1)
  }

  test("Test scale with factor 4") {
    val grayScaleArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(1), GrayScalePixel(7)),
      Seq(GrayScalePixel(3), GrayScalePixel(4)),
    )))
    val scaleTest = new ScaleFilter(4)
    val transformed = scaleTest.filters(grayScaleArt)

    assert(transformed.getPixelValues(0, 0).grayScaleValue == 1)
    assert(transformed.getPixelValues(0, 1).grayScaleValue == 1)
    assert(transformed.getPixelValues(0, 2).grayScaleValue == 7)
    assert(transformed.getPixelValues(0, 3).grayScaleValue == 7)
    assert(transformed.getPixelValues(1, 0).grayScaleValue == 1)
    assert(transformed.getPixelValues(1, 1).grayScaleValue == 1)
    assert(transformed.getPixelValues(1, 2).grayScaleValue == 7)
    assert(transformed.getPixelValues(1, 3).grayScaleValue == 7)
    assert(transformed.getPixelValues(2, 0).grayScaleValue == 3)
    assert(transformed.getPixelValues(2, 1).grayScaleValue == 3)
    assert(transformed.getPixelValues(2, 2).grayScaleValue == 4)
    assert(transformed.getPixelValues(2, 3).grayScaleValue == 4)
    assert(transformed.getPixelValues(3, 0).grayScaleValue == 3)
    assert(transformed.getPixelValues(3, 1).grayScaleValue == 3)
    assert(transformed.getPixelValues(3, 2).grayScaleValue == 4)
    assert(transformed.getPixelValues(3, 3).grayScaleValue == 4)
  }

  test("Test scale with factor value not <0 throws error") {
    val grayScaleArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(1), GrayScalePixel(7)),
      Seq(GrayScalePixel(3), GrayScalePixel(4)),
    )))
    val scaleTest = new ScaleFilter(-0.1)
    assertThrows[Exception]{
      scaleTest.filters(grayScaleArt)
    }
  }
}
