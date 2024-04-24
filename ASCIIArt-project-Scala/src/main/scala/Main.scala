package Main
import controller.ImageController

object Main extends App {
  /**
  * Here we use args options to count
  * or manipulate the passed arguments
  * via sbt shell
  */

  private val masterController = new ImageController()

  /**
   * We collect everything and pass it to controller class
   */
  masterController.processArguments(args)
}