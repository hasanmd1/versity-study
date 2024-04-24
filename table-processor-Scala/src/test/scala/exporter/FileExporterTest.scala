package exporter

import importer.FileImporter
import org.scalatest.FunSuite

import java.io.File

class FileExporterTest extends FunSuite{
  private val separator = ","
  private val outputSeqCsv = ("output.csv", false)
  private val outputSeqMd = ("output.md", false)
  private val table = new FileImporter("./src/assets/input.csv").importF

  test("Export to CSV file") {
    val fileExporter = new FileExporter(table, outputSeqCsv, separator)
    fileExporter.`export`()

    val outputFile = new File(outputSeqCsv._1)
    assert(outputFile.exists())
    assert(outputFile.length() > 0)

    // Clean up: Delete the created file
    outputFile.delete()
  }

  test("Export to MD file") {
    val fileExporter = new FileExporter(table, outputSeqMd, separator)
    fileExporter.`export`()

    val outputFile = new File(outputSeqMd._1)
    assert(outputFile.exists())
    assert(outputFile.length() > 0)

    // Clean up: Delete the created file
    outputFile.delete()
  }

  test("Exporting to an unsupported file format should throw an exception") {
    val outputSeq = ("output.txt", false)
    val separator = ","


    val exporter = new FileExporter(table, outputSeq, separator)

    // Add an assertion to check if exporting to an unsupported file format throws an exception
    assertThrows[Exception] {
      exporter.`export`()
    }
  }

}
