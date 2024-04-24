package dataModel.tableOptionsTest

import dataModel.tableOptions.possibleOptions.NonLinearTable
import org.scalatest.FunSuite

class NonLinearTableTest extends FunSuite{
  test("Testing table name - NonLinearTable") {
    val table = NonLinearTable()
    assert(table.tableName == "non-linear")
  }
  test("Testing table string - NonLinearTable") {
    val table = NonLinearTable()
    val expectedString = ".=+*#@"
    assert(table.tableContainingString == expectedString)
  }

}
