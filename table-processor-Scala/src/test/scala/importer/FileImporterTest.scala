package importer

import org.scalatest.FunSuite

class FileImporterTest extends FunSuite{

  test("Successfully import a valid CSV file") {
    val filePath = "./src/assets/input.csv"
    val importer = new FileImporter(filePath)
    val result = importer.importF

    // Add assertions to check the correctness of the imported table
    // For example, you can check the number of rows, values in specific cells, etc.
    assert(result.getRowCount > 0)
  }

  test("Importing a non-existent file should throw an Exception") {
    val filePath = "./src/assets/file.csv"
    val importer = new FileImporter(filePath)

    assertThrows[Exception] {
      importer.importF
    }
  }
  test("Importing a file with an unsupported format should throw an Exception") {
    val filePath = "./src/assets/input.md"
    val importer = new FileImporter(filePath)

    assertThrows[Exception] {
      importer.importF
    }
  }

}
