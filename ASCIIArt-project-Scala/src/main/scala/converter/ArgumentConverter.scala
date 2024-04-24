package converter

/**
 * will handle argument to image, table, outputImage
 * converter
 *
 * @tparam A input
 * @tparam B input
 */
trait ArgumentConverter[A, B] {

  /**
   * abstract method to handle the argument conversion
   *
   * @param mappedValues passed args
   * @return
   */
  def convertArgumentTo(mappedValues: A): B
}
