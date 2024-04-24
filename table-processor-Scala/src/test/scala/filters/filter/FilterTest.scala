package filters.filter

import importer.FileImporter
import org.scalatest.FunSuite

class FilterTest extends FunSuite{
  private val table = new FileImporter("./src/assets/input.csv").importF
  test("Applying filter with less than operation") {

    val resultTable = new Filter(table, "B < 30").apply()

    assert(resultTable.getRowCount === 4)
    assert(resultTable.getWidth === 3)
    assert(resultTable.rows.head.cells.map(_.getValue) === Vector(12, 21, "String"))
  }
  test("Applying filter with less than equal operation") {

    val resultTable = new Filter(table, "B <= 30").apply()

    assert(resultTable.getRowCount === 4)
    assert(resultTable.getWidth === 3)
    assert(resultTable.rows.head.cells.map(_.getValue) === Vector(12, 21, "String"))
  }
  test("Applying filter with greater than operation") {

    val resultTable = new Filter(table, "B > 25").apply()

    // Check if the result table contains the expected rows
    assert(resultTable.getRowCount === 2)
    assert(resultTable.getWidth === 3)
    assert(resultTable.rows.head.cells.map(_.getValue) === Vector("=12+1 * 5", 200, "String2"))
  }
  test("Applying filter with greater than equal operation") {

    val resultTable = new Filter(table, "B >= 25").apply()

    // Check if the result table contains the expected rows
    assert(resultTable.getRowCount === 2)
    assert(resultTable.getWidth === 3)
    assert(resultTable.rows.head.cells.map(_.getValue) === Vector("=12+1 * 5", 200, "String2"))
  }
  test("Applying filter with equal to operation") {

    val resultTable = new Filter(table, "B == 20").apply()

    // Check if the result table contains the expected rows
    assert(resultTable.getRowCount === 2)
    assert(resultTable.getWidth === 3)
    assert(resultTable.rows.head.cells.map(_.getValue) === Vector("=12+1", 20, "String"))
  }
  test("Applying filter with not equal to operation") {

    val resultTable = new Filter(table, "B != 20").apply()

    // Check if the result table contains the expected rows
    assert(resultTable.getRowCount === 4)
    assert(resultTable.getWidth === 3)
    assert(resultTable.rows.head.cells.map(_.getValue) === Vector(12, 21, "String"))
  }
}
