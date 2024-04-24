package MainTest

import Main.Main
import org.scalatest.FunSuite


class MainTest extends FunSuite {
  test("Running the application with valid arguments should not throw an exception") {
    val args = Array(
      "--input-file", "./src/assets/input.csv",
      "--output-file", "./src/assets/output.csv",
      "--filter", "B > 25",
      "--output-separator", ","
    )

    assertDoesNotThrow[Exception] {
      Main.runFromArgs(args)
    }
  }
  test("Running the application with valid arguments should throw an exception") {
    val args = Array(
      "--input-file", "--output-file", "./src/assets/output.csv",
      "--filter", "B > 25",
      "--output-separator", ","
    )

    assertThrows[Exception] {
      Main.runFromArgs(args)
    }
  }
  test("Running the application with --help option should print help messages") {
    val args = Array(
      "--input-file", "./src/assets/input.csv",
      "--output-file", "./src/assets/output.csv",
      "--filter", "B > 25",
      "--output-separator", ",",
      "--help"
    )

    val printedOutput = captureOutput {
      Main.runFromArgs(args)
    }

    assert(printedOutput.contains("--input-file [FILE] -> file should be only .csv format and only 1 workbook."))
    assert(printedOutput.contains("--output-file [FILE] -> file should be only .csv or .md format and only 1 workbook."))
    //etc etc
  }
  private def captureOutput(block: => Unit): String = {
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      block
    }
    stream.toString.trim
  }
  private def assertDoesNotThrow[E <: Throwable](block: => Any): Unit = {
    try {
      block
    } catch {
      case e: Throwable => fail(s"Expected code not to throw an exception, but it threw: $e")
    }
  }
}
