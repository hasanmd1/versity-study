package exporter

import dataModel.asciiImage.ASCIIImage
import dataModel.gridImage.ASCIIGrid
import dataModel.pixelImage.ASCIIPixel
import exporter.art.ExportToFile
import org.scalatest.FunSuite

import java.io.FileReader
import scala.collection.Iterator.continually

class ExportToFileTest extends FunSuite{

  test("Test exporting successfully") {
    val outputPath = "src/test/assets/outputFiles/output.txt"
    val exporter = new ExportToFile(outputPath)

    val asciiArt = ASCIIImage(ASCIIGrid(Seq(
      Seq(ASCIIPixel('%')),
      Seq(ASCIIPixel('#')),
      Seq(ASCIIPixel('^')),
      Seq(ASCIIPixel('^'))
    )))
    exporter.exportArt(asciiArt)
    val fileReader = new FileReader(outputPath)
    val bufferedReader = new java.io.BufferedReader(fileReader)
    val fileContent = continually(bufferedReader.readLine()).takeWhile(_ != null).mkString("\n")
    bufferedReader.close()

    val expectedContent =
      """%
        |#
        |^
        |^""".stripMargin

    assert(fileContent.equals(expectedContent))
  }

  test("Test exporting when the output stream path is wrong - not .txt") {
    val outputPath = "src/test/assets/outputFiles/output.ong"
    val exporter = new ExportToFile(outputPath)

    val asciiArt = ASCIIImage(ASCIIGrid(Seq(
      Seq(ASCIIPixel('%')),
      Seq(ASCIIPixel('#')),
      Seq(ASCIIPixel('^')),
      Seq(ASCIIPixel('^'))
    )))

    assertThrows[Exception] {
      exporter.exportArt(asciiArt)
    }
  }

  test("Test exporting when the output stream path is wrong - whole path is wrong") {
    val outputPath = "src/test/outputFiles/output.ong"
    val exporter = new ExportToFile(outputPath)

    val asciiArt = ASCIIImage(ASCIIGrid(Seq(
      Seq(ASCIIPixel('%')),
      Seq(ASCIIPixel('#')),
      Seq(ASCIIPixel('^')),
      Seq(ASCIIPixel('^'))
    )))

    assertThrows[Exception] {
      exporter.exportArt(asciiArt)
    }
  }

  test("Test exporting when the output stream is closed") {
    val outputPath = "src/test/assets/outputFiles/output.txt"
    val exporter = new ExportToFile(outputPath)

    val asciiArt = ASCIIImage(ASCIIGrid(Seq(
      Seq(ASCIIPixel('%')),
      Seq(ASCIIPixel('#')),
      Seq(ASCIIPixel('^')),
      Seq(ASCIIPixel('^'))
    )))

    exporter.closeOutputFile() //we closed the stream manually

    assertThrows[Exception] {
      exporter.exportArt(asciiArt)
    }
  }
}
