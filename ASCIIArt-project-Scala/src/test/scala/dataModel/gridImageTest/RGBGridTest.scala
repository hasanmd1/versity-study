package dataModel.gridImageTest

import dataModel.gridImage.RGBGrid
import dataModel.pixelImage.RGBPixel
import org.scalatest.FunSuite

class RGBGridTest extends FunSuite{
  test("Test RGBGrid should return the correct pixel at a given row and column") {
    val pixel1 = RGBPixel(255, 0, 0)
    val pixel2 = RGBPixel(0, 255, 0)
    val pixel3 = RGBPixel(0, 0, 255)
    val grid = Seq(
      Seq(pixel1, pixel2),
      Seq(pixel3)
    )

    val rgbGrid = RGBGrid(grid)

    assert(rgbGrid.getRowColumnValue(0, 0) == pixel1)
    assert(rgbGrid.getRowColumnValue(0, 1) == pixel2)
    assert(rgbGrid.getRowColumnValue(1, 0) == pixel3)
  }

  test("Test RGBGrid should return the correct number of rows") {
    val grid = Seq(
      Seq(RGBPixel(255, 0, 0), RGBPixel(0, 255, 0)),
      Seq(RGBPixel(0, 0, -87)) //can have - initially, which will be removed while processing
    )

    val rgbGrid = RGBGrid(grid)

    assert(rgbGrid.getNumberOfRow == 2)
  }

  test("Test RGBGrid should return the correct number of columns") {
    val grid = Seq(
      Seq(RGBPixel(255, 0, 0), RGBPixel(0, 255, 0)),
      Seq(RGBPixel(0, 0, 255))
    )

    val rgbGrid = RGBGrid(grid)

    assert(rgbGrid.getNumberOfColumn == 2)
  }

}
