package exporter

import dataModel.asciiImage.ASCIIImage
import dataModel.gridImage.ASCIIGrid
import dataModel.pixelImage.ASCIIPixel
import exporter.art.ExportToConsole
import org.scalatest.FunSuite

import java.io.{ByteArrayOutputStream, PrintStream} //for capturing console output

class ExportToConsoleTest extends FunSuite{

  test("Test exporting to console successfully") {
    val exporter = new ExportToConsole()

    val asciiArt = ASCIIImage(ASCIIGrid(Seq(
      Seq(ASCIIPixel('%')),
      Seq(ASCIIPixel('#')),
      Seq(ASCIIPixel('^')),
      Seq(ASCIIPixel('^')),
    )))

    val expectedOutput =
      """%
        |#
        |^
        |^""".stripMargin
    val outputStream = new ByteArrayOutputStream()
    val printStream = new PrintStream(outputStream)

    //Console.withOut block, which redirects the console output to the provided PrintStream
    //as it is hard to get output of console via any other way
    Console.withOut(new PrintStream(printStream)){
      exporter.exportArt(asciiArt)
    }
    /*
    output is "%
    #
    ^
    ^[]"
     */
    val consoleOutput = outputStream.toString.trim
    /*
    after trim "%
    #
    ^
    ^"
     */
    assert(consoleOutput == expectedOutput)
  }
}
