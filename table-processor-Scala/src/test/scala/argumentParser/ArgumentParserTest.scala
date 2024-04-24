package argumentParser

import org.scalatest.FunSuite

class ArgumentParserTest extends FunSuite{
  test("Collect input file") {
    val args = List("--input-file", "input.csv")
    val parser = new ArgumentParser(args)
    assert(parser.collectInput == "input.csv")
  }
  test("Throw exception when input file is missing") {
    val args = List("--input-file", "--filter")
    val parser = new ArgumentParser(args)
    intercept[IllegalArgumentException] {
      parser.collectInput
    }
  }
  test("Successfully collect outputFile name") {
    val args = List("--output-file", "output.txt")
    val parser = new ArgumentParser(args)
    assert(parser.collectOutput == ("output.txt", false))

  }
  test("Throw exception when output file is missing") {
    val args = List("--output-file", "--stdout")
    val parser = new ArgumentParser(args)
    assertThrows[Exception] (
      parser.collectOutput
    )

  }
  test("Collect output file and stdout flag") {
    val args = List("--output-file", "output.txt", "--stdout")
    val parser = new ArgumentParser(args)
    val result = parser.collectOutput
    assert(result._1 == "output.txt")
    assert(result._2)
  }
  test("Success for only --stdout") {
    val args = List("--stdout")
    val parser = new ArgumentParser(args)
    assert(parser.collectOutput == ("", true))
  }
  test("Collect filters") {
    val args = List("--filter", "COLUMN > 10", "--headers" , "--range", "A1 B2")
    val parser = new ArgumentParser(args)
    val result = parser.collectFilter
    assert(result == List("--filter", "COLUMN > 10", "--headers", "--range", "A1 B2"))
  }
  test("Collect filters last header") {
    val args = List("--filter", "COLUMN > 10" , "--range", "A1 B2", "--headers")
    val parser = new ArgumentParser(args)
    val result = parser.collectFilter
    assert(result == List("--filter", "COLUMN > 10", "--range", "A1 B2", "--headers"))
  }
  test("Throw exception when filter value is missing") {
    val args = List("--filter")
    val parser = new ArgumentParser(args)
    intercept[IllegalArgumentException] {
      parser.collectFilter
    }
  }
  test("Collect separator") {
    val args = List("--output-separator", ";")
    val parser = new ArgumentParser(args)
    assert(parser.getSeparator == ";")
  }
  test("Default separator is comma") {
    val args = List("--stdout")
    val parser = new ArgumentParser(args)
    assert(parser.getSeparator == ",")
  }
  test("Throw exception when separator value is missing") {
    val args = List("--output-separator")
    val parser = new ArgumentParser(args)
    intercept[Exception] {
      parser.getSeparator
    }
  }

}
