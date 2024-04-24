package filters

trait AllFilter[T] {
  def apply(): T
}
