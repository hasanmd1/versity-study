package model.dataModel

import importer.FileImporter
import org.scalatest.FunSuite

class TableTest extends FunSuite{
  test("Creating an EachTable with empty rows") {
    val eachTable = EachTable(List.empty[EachRow[Int]])
    assert(eachTable.getRowCount === 0)
    assert(eachTable.getWidth === 0)
  }
  test("Creating an EachTable with non-empty rows") {
    val eachTable = new FileImporter("./src/assets/input.csv").importF

    assert(eachTable.getRowCount === 6)
    assert(eachTable.getWidth === 3)
  }
  test("Creating an EachTable with non-empty rows and testing row collection") {
    val eachTable = new FileImporter("./src/assets/input.csv").importF
    val row = eachTable.rows(0)

    assert(eachTable.getRowCount === 6)
    assert(eachTable.getWidth === 3)
    assert(eachTable.getRow(0) == row)
  }
}
