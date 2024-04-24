package userInterface.argumentParser


/**
 * Interface for all ArgumentParser
 * @tparam X input
 * @tparam Y input
 */
trait ArgumentParser[X, Y] {

  //generic type method to handle parse

  /**
   * It is a generic type defined
   * which will parse input and return
   * parsed values
   * @param arguments input
   * @return
   */
  def parseArguments(arguments: X): Y
}
