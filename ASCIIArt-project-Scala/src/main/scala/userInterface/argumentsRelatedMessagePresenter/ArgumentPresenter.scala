package userInterface.argumentsRelatedMessagePresenter

/**
 * for all presenters
 * @tparam T generic
 */

trait ArgumentPresenter[T] {
  /**
   * Presents the results
   * @param arguments input
   */
  def presentArguments(arguments: T): Unit

  /**
   * used for throwing error
   * @param arguments input
   */
  def throwingError(arguments: T): Unit
}
