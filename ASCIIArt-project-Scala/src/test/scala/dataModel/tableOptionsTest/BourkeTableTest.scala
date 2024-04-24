package dataModel.tableOptionsTest

import dataModel.tableOptions.possibleOptions.BourkeTable

import org.scalatest.FunSuite

class BourkeTableTest extends FunSuite{
  test("Testing table name - BourkeTable"){
    val table = BourkeTable()
    assert(table.tableName == "bourke")
  }
  test("Testing table string - BourkeTable"){
    val table = BourkeTable()
    val expectedString = "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\"^`'. "
    assert(table.tableContainingString == expectedString)
  }

}
