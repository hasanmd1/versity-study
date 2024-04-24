package userInterface.parser

import org.scalatest.FunSuite
import userInterface.argumentsRelatedMessagePresenter.console.ConsoleArgumentPresenter

/**
 * this is just for testing each module
 * theres hardly any case in our program where our presenter/throwingError actually faces any error
 *
 */
class ConsoleArgumentPresenterTest extends FunSuite{

  test("successfully present arguments") {
    val presenter = new ConsoleArgumentPresenter()
    val message = "Arguments parsed successfully!"

    val output = captureOutput(presenter.presentArguments(message))
    assert(output == message + "\n")
  }

  test("throwingError throws IllegalArgumentException with the given message") {
    val presenter = new ConsoleArgumentPresenter()
    val errorMessage = "Invalid argument!"

    val exception = intercept[IllegalArgumentException] {
      presenter.throwingError(errorMessage)
    }

    assert(exception.getMessage == errorMessage)
  }

  private def captureOutput(block: => Unit): String = {
    val outputStream = new java.io.ByteArrayOutputStream()
    Console.withOut(outputStream) {
      block
    }
    outputStream.toString.stripLineEnd
  }
}
