package exporter

import importer.FileImporter
import org.scalatest.FunSuite

import java.io.ByteArrayOutputStream

class ConsoleExporterTest extends FunSuite{
  private val separator = ","
  private val outputSeqStdOut = ("", true)
  private val table = new FileImporter("./src/assets/input.csv").importF
  test("Export to StdOut for exception") {
    // Redirect stdout to capture the output
    val outputStream = new ByteArrayOutputStream()
    Console.withOut(outputStream) {
      val fileExporter = new ConsoleExporter(table, outputSeqStdOut, separator)
      fileExporter.`export`()

    }
  }
}
