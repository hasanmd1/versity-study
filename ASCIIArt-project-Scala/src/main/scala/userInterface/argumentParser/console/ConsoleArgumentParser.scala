package userInterface.argumentParser.console
import userInterface.argumentParser.ArgumentParser
import userInterface.argumentsRelatedMessagePresenter.console.ConsoleArgumentPresenter

class ConsoleArgumentParser extends ArgumentParser[Array[String], Map[String, Any]] {

  private var parser: Map[String, Any] = Map.empty[String, Any]

  private val errorPresenter = new ConsoleArgumentPresenter

  /**
   * It is a generic type defined which will parse input
   * and return parsed values
   *
   * @param arguments received args
   * @return
   */
  override def parseArguments(arguments: Array[String]): Map[String, Any] = {
    argumentsValidator(arguments)
    var index = 0
    while (index < arguments.length) {
      if (arguments(index) == "--image") {
        addingArgs(arguments(index), arguments, index + 1)
        index = index + 1
      }
      else if (arguments(index) == "--image-random") {
        parser += (arguments(index) -> true)
      }
      else if (arguments(index) == "--output-console") {
        parser += (arguments(index) -> true)
      }
      else if (arguments(index) == "--output-file") {
        addingArgs(arguments(index), arguments, index + 1)
        index = index + 1
      }
      else if (arguments(index) == "--table") {
        addingArgs(arguments(index), arguments, index + 1)
        index = index + 1
      }
      else if (arguments(index) == "--custom-table") {
        addingArgs(arguments(index), arguments, index + 1)
        index = index + 1
      }
      else if (arguments(index) == "--rotate") {
        addingArgs(arguments(index), arguments, index + 1)
        index = index + 1
      }
      else if (arguments(index) == "--scale") {
        addingArgs(arguments(index), arguments, index + 1)
        index = index + 1
      }
      else if (arguments(index) == "--invert") {
        parser += (arguments(index) -> true)
      }
      else if (arguments(index) == "--brightness") {
        addingArgs(arguments(index), arguments, index + 1)
        index = index + 1
      }
      else if (arguments(index) == "--flip" && arguments(index + 1) == "x") {
        parser += (arguments(index).concat("X") -> true)
      }
      else if (arguments(index) == "--flip" && arguments(index + 1) == "y") {
        parser += (arguments(index).concat("Y") -> true)
      }
      index = index + 1
    }
    parser
  }

  /**
   * it is used to append the var of parser when an input/output with path
   * is encountered
   * @param argument one arg
   * @param arguments whole args
   * @param index index of arg
   */

  private def addingArgs(argument: String, arguments: Array[String],  index: Int):Unit = {
    if(arguments.length <= index){
      errorPresenter.throwingError("Please input argument " + argument + " with correct value")
    }
    parser += (argument -> arguments(index))
  }

  /**
   * validated imagePath arguments
   *
   */
  private def argumentsValidator(arguments: Array[String]): Unit = {

    val allowedArguments = Set(
      "--image",
      "--image-random",
      "--output-console",
      "--output-file",
      "--table",
      "--custom-table",
      "--rotate",
      "--scale",
      "--invert",
      "--brightness",
      "--flip",
    )
    val requiredValueArguments = Set(
      "--image",
      "--output-file",
      "--table",
      "--custom-table",
      "--rotate",
      "--scale",
      "--brightness",
      "--flip",
    )
    val invalidArguments = arguments.filter(arg => arg.contains("--"))filterNot(arg => allowedArguments.contains(arg))
    if (invalidArguments.nonEmpty) {
      errorPresenter.throwingError(s"Invalid argument(s) found: ${invalidArguments.mkString(", ")}")
    }

    val missingValues = requiredValueArguments.filter(arg => arguments.contains(arg) && arguments(arguments.indexOf(arg) + 1).contains("--"))
    if (missingValues.nonEmpty) {
      errorPresenter.throwingError(s"Missing value(s) for argument(s): ${missingValues.mkString(", ")}")
    }
  }
}
