package filter

import dataModel.asciiImage.GrayScaleImage
import dataModel.gridImage.GrayScaleGrid
import dataModel.gridImageTest.GrayScaleGridTest
import dataModel.pixelImage.GrayScalePixel
import filter.artFilter.RotateFilter
import org.scalatest.FunSuite

class RotateFilterTest extends FunSuite{
  test("Test rotate with 90 degree rotate"){
    val grayScaleArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(1), GrayScalePixel(7)),
      Seq(GrayScalePixel(3), GrayScalePixel(4)),
    )))
    val expectedArt: GrayScaleImage = new RotateFilter(90.toDouble).filters(grayScaleArt)

    assert(expectedArt.getPixelValues(0, 0).grayScaleValue == 3)
    assert(expectedArt.getPixelValues(0, 1).grayScaleValue == 1)
    assert(expectedArt.getPixelValues(1, 0).grayScaleValue == 4)
    assert(expectedArt.getPixelValues(1, 1).grayScaleValue == 7)
  }
  test("Test rotate with -270 degree rotate") {
    val grayScaleArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(1), GrayScalePixel(7)),
      Seq(GrayScalePixel(3), GrayScalePixel(4)),
    )))
    val expectedArt: GrayScaleImage = new RotateFilter(-270.toDouble).filters(grayScaleArt)

    assert(expectedArt.getPixelValues(0, 0).grayScaleValue == 3)
    assert(expectedArt.getPixelValues(0, 1).grayScaleValue == 1)
    assert(expectedArt.getPixelValues(1, 0).grayScaleValue == 4)
    assert(expectedArt.getPixelValues(1, 1).grayScaleValue == 7)
  }
  test("Test rotate with 180 degree rotate") {
    val grayScaleArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(1), GrayScalePixel(7)),
      Seq(GrayScalePixel(3), GrayScalePixel(4)),
    )))
    val expectedArt: GrayScaleImage = new RotateFilter(180.toDouble).filters(grayScaleArt)

    assert(expectedArt.getPixelValues(0, 0).grayScaleValue == 4)
    assert(expectedArt.getPixelValues(0, 1).grayScaleValue == 3)
    assert(expectedArt.getPixelValues(1, 0).grayScaleValue == 7)
    assert(expectedArt.getPixelValues(1, 1).grayScaleValue == 1)
  }
  test("Test rotate with -180 degree rotate") {
    val grayScaleArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(1), GrayScalePixel(7)),
      Seq(GrayScalePixel(3), GrayScalePixel(4)),
    )))
    val expectedArt: GrayScaleImage = new RotateFilter(-180.toDouble).filters(grayScaleArt)

    assert(expectedArt.getPixelValues(0, 0).grayScaleValue == 4)
    assert(expectedArt.getPixelValues(0, 1).grayScaleValue == 3)
    assert(expectedArt.getPixelValues(1, 0).grayScaleValue == 7)
    assert(expectedArt.getPixelValues(1, 1).grayScaleValue == 1)
  }
  test("Test rotate with 270 degree rotate") {
    val grayScaleArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(1), GrayScalePixel(7)),
      Seq(GrayScalePixel(3), GrayScalePixel(4)),
    )))
    val expectedArt: GrayScaleImage = new RotateFilter(270.toDouble).filters(grayScaleArt)

    assert(expectedArt.getPixelValues(0, 0).grayScaleValue == 7)
    assert(expectedArt.getPixelValues(0, 1).grayScaleValue == 4)
    assert(expectedArt.getPixelValues(1, 0).grayScaleValue == 1)
    assert(expectedArt.getPixelValues(1, 1).grayScaleValue == 3)
  }
  test("Test rotate with -90 degree rotate") {
    val grayScaleArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(1), GrayScalePixel(7)),
      Seq(GrayScalePixel(3), GrayScalePixel(4)),
    )))
    val expectedArt: GrayScaleImage = new RotateFilter(-90.toDouble).filters(grayScaleArt)

    assert(expectedArt.getPixelValues(0, 0).grayScaleValue == 7)
    assert(expectedArt.getPixelValues(0, 1).grayScaleValue == 4)
    assert(expectedArt.getPixelValues(1, 0).grayScaleValue == 1)
    assert(expectedArt.getPixelValues(1, 1).grayScaleValue == 3)
  }
  test("Test rotate with 360 degree rotate") {
    val grayScaleArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(1), GrayScalePixel(7)),
      Seq(GrayScalePixel(3), GrayScalePixel(4)),
    )))
    val expectedArt: GrayScaleImage = new RotateFilter(360.toDouble).filters(grayScaleArt)

    assert(expectedArt.getPixelValues(0, 0).grayScaleValue == 1)
    assert(expectedArt.getPixelValues(0, 1).grayScaleValue == 7)
    assert(expectedArt.getPixelValues(1, 0).grayScaleValue == 3)
    assert(expectedArt.getPixelValues(1, 1).grayScaleValue == 4)
  }
  test("Test rotate with -360 degree rotate") {
    val grayScaleArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(1), GrayScalePixel(7)),
      Seq(GrayScalePixel(3), GrayScalePixel(4)),
    )))
    val expectedArt: GrayScaleImage = new RotateFilter(-360.toDouble).filters(grayScaleArt)

    assert(expectedArt.getPixelValues(0, 0).grayScaleValue == 1)
    assert(expectedArt.getPixelValues(0, 1).grayScaleValue == 7)
    assert(expectedArt.getPixelValues(1, 0).grayScaleValue == 3)
    assert(expectedArt.getPixelValues(1, 1).grayScaleValue == 4)
  }

  test("Test rotate with 45 degree rotate") {
    val grayScaleArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(1), GrayScalePixel(7)),
      Seq(GrayScalePixel(3), GrayScalePixel(4)),
    )))

    assertThrows[Exception] {
      new RotateFilter(45.toDouble).filters(grayScaleArt)
    }
  }

}
