package exporter

/**
 * for handling export
 *
 * @tparam T input type
 */
trait Exporter[T] {
  /**
   * export image Art
   *
   * @param image which image will be exported
   */
  def exportArt(image: T): Unit
}
