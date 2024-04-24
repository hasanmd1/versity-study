package model.dataModel

import org.scalatest.FunSuite

class CellTest extends FunSuite{
  test("Creating an EachCell with an integer value") {
    val eachCell = EachCell(42, "Int")
    assert(eachCell.getValue === 42)
  }

  test("Creating an EachCell with a string value") {
    val eachCell = EachCell("Hello, World!", "String")
    assert(eachCell.getValue === "Hello, World!")
  }

  test("Creating an EachCell with a double value") {
    val eachCell = EachCell(3.14, "Double")
    assert(eachCell.getValue === 3.14)
  }

  test("Getting the cell type from EachCell") {
    val eachCellInt = EachCell(42, "Int")
    val eachCellString = EachCell("Hello, World!", "String")
    val eachCellDouble = EachCell(3.14, "Double")

    assert(eachCellInt.cellType === "Int")
    assert(eachCellString.cellType === "String")
    assert(eachCellDouble.cellType === "Double")
  }
}
