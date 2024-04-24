package importer

/**
 * for loading image / randomImage
 * @tparam X generic type
 */
trait ImageImporter[X] {
  /**
   * Loads image/randomImage
   * @return
   */
  def importImage(): X
}
