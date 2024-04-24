package userInterface.argumentsRelatedMessagePresenter.console

import userInterface.argumentsRelatedMessagePresenter.ArgumentPresenter

class ConsoleArgumentPresenter extends ArgumentPresenter[String] {


  /**
   * Presents the results in UI
   * @param arguments passed message
   */
  override def presentArguments(arguments: String): Unit = {

    println(arguments+"\n")
  }

  /**
   * used for throwing error
   *
   * @param arguments passed error message
   */
  override def throwingError(arguments: String): Unit = {

    throw new IllegalArgumentException(arguments)
  }
}
