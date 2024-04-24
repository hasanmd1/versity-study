package filters.range

import importer.FileImporter
import org.scalatest.FunSuite

class RangeSpecifierTest extends FunSuite{
  test("Applying RangeSpecifier with a valid range") {
    val table = new FileImporter("./src/assets/input.csv").importF

    val resultTable = new RangeSpecifier(table, "B2 C4").apply()

    assert(resultTable.getRowCount === 3)
    assert(resultTable.getWidth === 2)

    assert(resultTable.rows.head.cells.map(_.getValue) === Vector(20, "String"))
    assert(resultTable.rows.tail.head.cells.map(_.getValue) === Vector(200, "String2"))
    assert(resultTable.rows.tail.tail.head.cells.map(_.getValue) === Vector(20, "String3"))
  }
  test("Applying RangeSpecifier with an invalid range format should throw an exception") {
    val table = new FileImporter("./src/assets/input.csv").importF

    val resultTable = new RangeSpecifier(table, "B2 C4").apply()

    val rangeSpecifier = new RangeSpecifier(table, "B2-C3-D4")
    assertThrows[Exception] {
      rangeSpecifier.apply()
    }
  }
  test("Applying RangeSpecifier with a range that goes beyond table boundaries should give us from that column to end column") {
    val table = new FileImporter("./src/assets/input.csv").importF

    val resultTable = new RangeSpecifier(table, "B2 F6").apply()

    assert(resultTable.getRowCount === 5)
    assert(resultTable.getWidth === 2)

    assert(resultTable.rows.head.cells.map(_.getValue) === Vector(20, "String"))
    assert(resultTable.rows.tail.head.cells.map(_.getValue) === Vector(200, "String2"))
    assert(resultTable.rows.tail.tail.head.cells.map(_.getValue) === Vector(20, "String3"))
  }

}
