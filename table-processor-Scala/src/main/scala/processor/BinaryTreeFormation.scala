package processor

trait BinaryTreeFormation[T, S] {
  def evaluateTree(tree: S): T
  def formBinaryTree(value: String): S
}
