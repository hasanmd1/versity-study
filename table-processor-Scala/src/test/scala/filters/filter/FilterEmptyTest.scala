package filters.filter

import importer.FileImporter
import org.scalatest.FunSuite

class FilterEmptyTest extends FunSuite{
  private val table = new FileImporter("./src/assets/input.csv").importF
  test("Successfully parsing using correct column number"){
    val resultExpected = new FilterEmpty(table , "B").apply()

    assert(resultExpected.getRowCount === 1)
    assert(resultExpected.rows.head.cells.map(_.getValue) === Vector("=12-1", "", "String5"))

  }

  test("Failed parsing using correct column number but greater than table"){
    val resultExpected = new FilterEmpty(table , "D")

    assertThrows[Exception](
      resultExpected.apply()
    )
  }

  test("Failed parsing using incorrect column number"){
    val resultExpected = new FilterEmpty(table , "----")

    assertThrows[Exception](
      resultExpected.apply()
    )
  }

}
