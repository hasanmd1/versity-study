package filter

import dataModel.asciiImage.GrayScaleImage
import dataModel.gridImage.GrayScaleGrid
import dataModel.pixelImage.GrayScalePixel
import filter.artFilter.InvertFilter
import org.scalatest.FunSuite

class InvertFilterTest extends FunSuite{

  test("Test applying InvertFilter to GrayScaleArt") {
    val filter = new InvertFilter()

    val grayScaleArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(100), GrayScalePixel(150), GrayScalePixel(200)),
      Seq(GrayScalePixel(50), GrayScalePixel(75), GrayScalePixel(125)),
      Seq(GrayScalePixel(25), GrayScalePixel(0), GrayScalePixel(255))
    )))

    val expectedArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(255 - 100), GrayScalePixel(255 - 150), GrayScalePixel(255 - 200)),
      Seq(GrayScalePixel(255 - 50), GrayScalePixel(255 - 75), GrayScalePixel(255 - 125)),
      Seq(GrayScalePixel(255 - 25), GrayScalePixel(255 - 0), GrayScalePixel(255 - 255))
    )))

    val filteredArt = filter.filters(grayScaleArt)

    assert(filteredArt == expectedArt)
  }

  test("Test applying InvertFilter to GrayScaleArt with all white pixels") {
    val filter = new InvertFilter()

    val grayScaleArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(255), GrayScalePixel(255)),
      Seq(GrayScalePixel(255), GrayScalePixel(255))
    )))

    val expectedArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(255 - 255), GrayScalePixel(255 - 255)),
      Seq(GrayScalePixel(255 - 255), GrayScalePixel(255 - 255))
    )))

    val filteredArt = filter.filters(grayScaleArt)

    assert(filteredArt == expectedArt)
  }

  test("Test applying InvertFilter to GrayScaleArt with all black pixels") {
    val filter = new InvertFilter()

    val grayScaleArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(0)),
      Seq(GrayScalePixel(0)),
    )))

    val expectedArt = GrayScaleImage(GrayScaleGrid(Seq(
      Seq(GrayScalePixel(255 - 0)),
      Seq(GrayScalePixel(255 - 0)),
    )))

    val filteredArt = filter.filters(grayScaleArt)

    assert(filteredArt == expectedArt)
  }
}
