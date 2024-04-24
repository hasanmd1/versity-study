package processor

/**
 * used for processing of all other arguments
 * @tparam A user input
 * @tparam B user input
 * @tparam C user input
 * @tparam D user input
 */
trait ArgumentProcessor[A, B, C, D] {

  /**
   * abstract defined method to handle rest of arguments
   *
   * @param mappedValues name of input
   * @param commandOptions name of input
   * @param rawImage name of input
   * @param table type of table
   */
  def processHandler(mappedValues:  A, commandOptions: B, rawImage: C, table: D): Unit
}
