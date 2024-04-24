package MainTest

import controller.ImageController
import org.scalatest.FunSuite


class MainTest extends FunSuite {
  test("Calling processArguments should not throw an exception") {

    val controller = new ImageController()
    val args: Array[String] = Array("--image-random", "--output-console")

    try {
      controller.processArguments(args)
    } catch {
      case _: Throwable =>
        fail("An unexpected exception was thrown as expected")
    }
  }

  test("Calling processArguments with null should throw an exception") {

    val controller = new ImageController()

    assertThrows[Exception] {
      controller.processArguments(null)
    }
  }
}
