package dataModel.tableOptionsTest

import dataModel.tableOptions.possibleOptions.CustomTable
import org.scalatest.FunSuite

class CustomTableTest extends FunSuite{
  test("Testing table name - customTable") {
    val table = CustomTable()
    assert(table.tableName == "custom-table")
  }
  test("Testing table string - customTable") {
    val table = CustomTable()
    table.setTableContainingString("(*&^%")
    val expectedString = "(*&^%"
    assert(table.tableContainingString == expectedString)
  }
  test("Testing table default string - customTable") {
    val table = CustomTable()
    val expectedString = String.valueOf("")
    assert(table.tableContainingString == expectedString)
  }

}
