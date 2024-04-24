package model.dataModel

import model.Cell
import org.scalatest.FunSuite

class RowTest extends FunSuite{
  test("Creating an EachRow with empty cells") {
    val eachRow = EachRow(Vector.empty[Cell[Int]])
    assert(eachRow.getLength === 0)
  }
  test("Creating an EachRow with non-empty cells") {
    val cell1 = EachCell(1, "Int")
    val cell2 = EachCell(2, "Int")
    val cell3 = EachCell(3, "Int")

    val eachRow = EachRow(Vector(cell1, cell2, cell3))

    assert(eachRow.getLength === 3)
  }

  test("Getting a specific cell from EachRow") {
    val cell1 = EachCell(1, "Int")
    val cell2 = EachCell(2, "Int")
    val cell3 = EachCell(3, "Int")

    val eachRow = EachRow(Vector(cell1, cell2, cell3))

    assert(eachRow.getCell(0) === cell1)
    assert(eachRow.getCell(1) === cell2)
    assert(eachRow.getCell(2) === cell3)
  }

}
