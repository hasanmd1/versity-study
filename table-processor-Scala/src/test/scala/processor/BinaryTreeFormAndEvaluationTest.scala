package processor

import importer.FileImporter
import model.dataModel.{EachCell, EachRow, EachTable}
import org.scalatest.FunSuite

class BinaryTreeFormAndEvaluationTest extends FunSuite{
  test("formBinaryTree should correctly form a binary tree from a mathematical expression") {

    val table = new FileImporter("./src/assets/input.csv").importF
    val binaryTreeProcessor = new BinaryTreeFormAndEvaluation(table)

    val expression = "12+1"
    val binaryTree = binaryTreeProcessor.formBinaryTree(expression)

    assert(binaryTree.toString == "OperatorNode(+,NumberNode(12),NumberNode(1))")
  }
  test("formBinaryTree should correctly form a binary tree from a mathematical expression /") {

    val table = new FileImporter("./src/assets/input.csv").importF
    val binaryTreeProcessor = new BinaryTreeFormAndEvaluation(table)

    val expression = "12/1"
    val binaryTree = binaryTreeProcessor.formBinaryTree(expression)

    assert(binaryTree.toString == "OperatorNode(/,NumberNode(12),NumberNode(1))")
  }
  test("formBinaryTree should correctly form a binary tree from a reference") {

    val table = new FileImporter("./src/assets/input.csv").importF
    val binaryTreeProcessor = new BinaryTreeFormAndEvaluation(table)

    val expression = "B1/B5"
    val binaryTree = binaryTreeProcessor.formBinaryTree(expression)

    assert(binaryTree.toString == "OperatorNode(/,NumberNode(21),NumberNode(120))")
  }
  test("formBinaryTree should correctly form a binary tree from a reference if circular throws error") {

    val table = new FileImporter("./src/assets/input.csv").importF
    val binaryTreeProcessor = new BinaryTreeFormAndEvaluation(table)

    val expression = "A3/B5"
    val binaryTree = binaryTreeProcessor
      assertThrows[Exception] (
        binaryTree.formBinaryTree(expression)
      )
  }
}
