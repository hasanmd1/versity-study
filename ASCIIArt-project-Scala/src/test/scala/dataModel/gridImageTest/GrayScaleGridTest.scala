package dataModel.gridImageTest

import dataModel.gridImage.GrayScaleGrid
import dataModel.pixelImage.GrayScalePixel
import org.scalatest.FunSuite

class GrayScaleGridTest extends FunSuite{
  test("Testing GrayScaleGrid should return the correct pixel at a given row and column") {
    val pixel1 = GrayScalePixel(100)
    val pixel2 = GrayScalePixel(200)
    val pixel3 = GrayScalePixel(150)
    val grid = Seq(
      Seq(pixel1, pixel2),
      Seq(pixel3)
    )

    val grayScaleGrid = GrayScaleGrid(grid)

    assert(grayScaleGrid.getRowColumnValue(0, 0) == pixel1)
    assert(grayScaleGrid.getRowColumnValue(0, 1) == pixel2)
    assert(grayScaleGrid.getRowColumnValue(1, 0) == pixel3)
  }

  test("Testing GrayScaleGrid should return the correct number of rows") {
    val grid = Seq(
      Seq(GrayScalePixel(100), GrayScalePixel(200)),
      Seq(GrayScalePixel(150))
    )

    val grayScaleGrid = GrayScaleGrid(grid)

    assert(grayScaleGrid.getNumberOfRow == 2)
  }

  test("Testing GrayScaleGrid should return the correct number of columns") {
    val grid = Seq(
      Seq(GrayScalePixel(100), GrayScalePixel(200)),
      Seq(GrayScalePixel(150))
    )

    val grayScaleGrid = GrayScaleGrid(grid)

    assert(grayScaleGrid.getNumberOfColumn == 2)
  }
}
