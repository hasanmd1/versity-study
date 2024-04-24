package processor

trait Processor[T] {

  def applyFilters(): T

}
