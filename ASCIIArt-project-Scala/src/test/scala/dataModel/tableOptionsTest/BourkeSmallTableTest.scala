package dataModel.tableOptionsTest

import dataModel.tableOptions.possibleOptions.BourkeSmallTable
import org.scalatest.FunSuite

class BourkeSmallTableTest extends FunSuite{
  test("Testing table name - BourkeSmallTable") {
    val table = BourkeSmallTable()
    assert(table.tableName == "bourke-small")
  }
  test("Testing table string - BourkeSmallTable") {
    val table = BourkeSmallTable()
    val expectedString = " .:-=+*#%@"
    assert(table.tableContainingString == expectedString)
  }

}
