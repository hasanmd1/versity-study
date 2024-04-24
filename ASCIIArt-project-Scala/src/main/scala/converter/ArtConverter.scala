package converter

/**
 * to convert images
 *
 * @tparam A input
 * @tparam B output
 */
trait ArtConverter[A, B] {

  /**
   * convertsImage A to B
   *
   * @param image input
   * @return converted image
   */
  def convertArt(image: A): B
}
