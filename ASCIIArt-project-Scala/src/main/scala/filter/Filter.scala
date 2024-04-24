package filter

/**
 * all filters
 *
 * @tparam A image
 */
trait Filter[A] {

  /**
   * applying filters method
   *
   * @param image input
   * @return
   */
  def filters(image: A): A
}
