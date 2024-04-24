package filters.header

import importer.FileImporter
import org.scalatest.FunSuite

class HeaderTest extends FunSuite{
  private val table = new FileImporter("./src/assets/input.csv").importF

  test("Applying Header filter to a table without headers") {

    val resultTable = new Header(table).apply()

    assert(resultTable.getRowCount === 7)
    assert(resultTable.getWidth === 3)

    assert(resultTable.rows.head.cells.map(_.getValue) === Vector("A", "B", "C"))

  }

  test("Applying Header filter to a table with existing headers") {

    val headerFilter = new Header(table)
    val resultTable = headerFilter.apply()

    // Check if the result table is unchanged since it already has headers
    assert(!(resultTable === table))
  }

}
