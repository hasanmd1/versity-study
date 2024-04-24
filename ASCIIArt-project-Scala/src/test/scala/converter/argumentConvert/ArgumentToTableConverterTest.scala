package converter.argumentConvert

import converter.argumentConversion.ArgumentToTableConverter
import dataModel.tableOptions.Table
import dataModel.tableOptions.possibleOptions.{BourkeSmallTable, BourkeTable, CustomTable, NonLinearTable}
import org.scalatest.FunSuite

class ArgumentToTableConverterTest extends FunSuite{

  test("successfully converting --custom-table argument to Table") {
    val converter = new ArgumentToTableConverter
    val tableString = "*&-+=#$"
    val mappedValues: Map[String, Any] = Map("--custom-table" -> tableString)

    val table: Table[String] = converter.convertArgumentTo(mappedValues)

    assert(table.isInstanceOf[CustomTable])
    assert(table.asInstanceOf[CustomTable].tableContainingString == tableString)
  }

  test("successfully converting --table argument to Table (BourkeTable)") {
    val converter = new ArgumentToTableConverter
    val mappedValues: Map[String, Any] = Map("--table" -> "bourke")

    val table: Table[String] = converter.convertArgumentTo(mappedValues)

    assert(table.isInstanceOf[BourkeTable])
  }

  test("successfully converting --table argument to Table (BourkeSmallTable)") {
    val converter = new ArgumentToTableConverter
    val mappedValues: Map[String, Any] = Map("--table" -> "bourke-small")

    val table: Table[String] = converter.convertArgumentTo(mappedValues)

    assert(table.isInstanceOf[BourkeSmallTable])
  }

  test("successfully converting --table argument to Table (NonLinearTable)") {
    val converter = new ArgumentToTableConverter
    val mappedValues: Map[String, Any] = Map("--table" -> "non-linear")

    val table: Table[String] = converter.convertArgumentTo(mappedValues)

    assert(table.isInstanceOf[NonLinearTable])
  }

  test("converting both --custom-table and --table arguments throws an exception") {
    val converter = new ArgumentToTableConverter
    val tableString = "*&-+=#$"
    val mappedValues: Map[String, Any] = Map("--custom-table" -> tableString, "--table" -> "bourke")

    assertThrows[IllegalArgumentException] {
      converter.convertArgumentTo(mappedValues)
    }
  }

  test("converting neither --custom-table nor --table arguments uses predefined table") {
    val converter = new ArgumentToTableConverter
    val mappedValues: Map[String, Any] = Map.empty[String, Any]

    val predefinedTable: Table[String] = converter.convertArgumentTo(mappedValues)

    assert(predefinedTable.equals(converter.convertArgumentTo(Map.empty[String, Any])) )
  }
}
