package userInterface.parser

import org.scalatest.FunSuite
import userInterface.argumentParser.console.ConsoleArgumentParser

class ConsoleArgumentParserTest extends FunSuite{

  private val parser = new ConsoleArgumentParser()
  private val successfulMap: Map[String, Any] = Map(
    "--rotate"->"-270",
    "--output-file"->"output.txt",
    "--brightness"->"0.8",
    "--invert"->"true",
    "--table"->"bourke",
    "--image"->"input.jpg",
    "--flip"->"x",
  )
  private val arguments = Array(
    "--image", "input.jpg",
    "--output-file", "output.txt",
    "--table", "bourke",
    "--rotate", "-270",
    "--brightness", "0.8",
    "--flip", "x",
    "--invert"
  )
  private val errorInArguments = Array(
    "--image", "input.jpg",
    "--robot", "65748",
    "--rotate", "90",
    "--brightness", "50",
    "--flip", "x",
  )

  private val errorMissingParameter = Array(
    "--image",
    "--rotate", "-90",
    "--brightness", "50",
    "--flip", "x",
  )

  test("Successfully Parse valid arguments"){
    val parsedArguments = parser.parseArguments(arguments)
    assert(parsedArguments.size == successfulMap.size)
    assert(parsedArguments("--image") == successfulMap("--image").toString)
    assert(parsedArguments("--output-file") == successfulMap("--output-file").toString)
    assert(parsedArguments("--table") == successfulMap("--table").toString)
    assert(parsedArguments("--rotate") == "-270")
    assert(parsedArguments("--brightness") == "0.8")
    assert(parsedArguments("--flipX") == true)
  }

  test("Wrong arguments throws exception") {
    assertThrows[IllegalArgumentException]{
      parser.parseArguments(errorInArguments)
    }
  }
  test("Correct arguments with missing parameters throws exception") {
    assertThrows[IllegalArgumentException] {
      parser.parseArguments(errorMissingParameter)
    }
  }

}
