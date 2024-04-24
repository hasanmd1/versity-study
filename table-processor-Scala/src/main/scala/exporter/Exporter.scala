package exporter

trait Exporter[T] {
  def export() : T
}
