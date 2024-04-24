package dataModel.gridImageTest

import dataModel.gridImage.ASCIIGrid
import dataModel.pixelImage.ASCIIPixel
import org.scalatest.FunSuite

class ASCIIGridTest extends FunSuite{
  test("Testing ASCIIGrid should return the correct pixel at a given row and column") {
    val pixel1 = ASCIIPixel('@')
    val pixel2 = ASCIIPixel('#')
    val pixel3 = ASCIIPixel('.')
    val grid = Seq(
      Seq(pixel1, pixel2),
      Seq(pixel3)
    )

    val asciiGrid = ASCIIGrid(grid)

    assert(asciiGrid.getRowColumnValue(0, 0) == pixel1)
    assert(asciiGrid.getRowColumnValue(0, 1) == pixel2)
    assert(asciiGrid.getRowColumnValue(1, 0) == pixel3)
  }

  test(" Testing ASCIIGrid should return the correct number of rows") {
    val grid = Seq(
      Seq(ASCIIPixel('@'), ASCIIPixel('#')),
      Seq(ASCIIPixel('.'))
    )

    val asciiGrid = ASCIIGrid(grid)

    assert(asciiGrid.getNumberOfRow == 2)
  }

  test("Testing ASCIIGrid should return the correct number of columns") {
    val grid = Seq(
      Seq(ASCIIPixel('@'), ASCIIPixel('#')),
      Seq(ASCIIPixel('.'))
    )

    val asciiGrid = ASCIIGrid(grid)

    assert(asciiGrid.getNumberOfColumn == 2)
  }
}
