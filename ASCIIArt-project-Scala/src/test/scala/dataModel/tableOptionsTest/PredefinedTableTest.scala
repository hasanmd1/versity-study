package dataModel.tableOptionsTest

import dataModel.tableOptions.possibleOptions.PredefinedTable
import org.scalatest.FunSuite

class PredefinedTableTest extends FunSuite {
  test("Testing table name - PredefinedTable") {
    val table = PredefinedTable()
    assert(table.tableName == "predefinedTable")
  }
  test("Testing table string - PredefinedTable") {
    val table = PredefinedTable()
    val expectedString = " .:-=+*#%@"
    assert(table.tableContainingString == expectedString)
  }
}
