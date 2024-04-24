package controller

/**
 * used to process the image
 *
 * @tparam T args that will be passed
 */

trait Controller[T] {

  /**
   * used for method which will determine workflow
   *
   * @param commandOptions name defined for passed args
   */
  def processArguments(commandOptions: T): Unit
}
