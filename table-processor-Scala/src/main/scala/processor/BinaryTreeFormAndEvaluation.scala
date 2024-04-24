package processor

import model.BinaryTreeCase
import model.Tree.{NumberNode, OperatorNode}
import model.dataModel.EachTable

class BinaryTreeFormAndEvaluation(table: EachTable[Any]) extends BinaryTreeFormation[Int, BinaryTreeCase] {

  override def formBinaryTree(value: String): BinaryTreeCase = {

    val operators = Set('+', '-', '*', '/')

    val index = findLowestPrecedenceOperator(value, operators)
    if (index != -1) {

      val leftPart = value.substring(0, index)
      val operator = value.charAt(index)
      val rightPart = value.substring(index + 1)

      val leftTree = formBinaryTree(leftPart)
      val rightTree = formBinaryTree(rightPart)

      OperatorNode(operator.toString, leftTree, rightTree)
    } else {
      if (value.charAt(0) >= 'A' && value.charAt(0) <= 'Z'){
        val col = value.charAt(0) - 'A'
        val row = value.charAt(1) - '1'

        try{
          NumberNode(table.getRow(row).getCell(col).getValue.asInstanceOf[Int])
        }
        catch{
          case e: Exception =>
            throw new Exception(s"Cyclic dependency detected. ${e.getMessage}\n")
        }
      }
      else{
        try{
          NumberNode(value.toInt)
        }
        catch{
          case e: Exception=>
            throw new Exception(s"${e.getMessage}\n")
        }
      }

    }
  }

  private def operatorPrecedence(operator: Char): Int = {
    operator match {
      case '+' | '-' => 1
      case '*' | '/' => 2
      case _ => 0
    }
  }

  private def findLowestPrecedenceOperator(value: String, operators: Set[Char]): Int = {
    var index = -1
    var minPrecedence = Int.MaxValue

    var i = value.length - 1
    while (i >= 0) {
      val char = value.charAt(i)
      if (operators.contains(char)) {
        val precedence = operatorPrecedence(char)
        if (precedence <= minPrecedence) {
          minPrecedence = precedence
          index = i
        }
      }
      i -= 1
    }

    index
  }

  override def evaluateTree(tree: BinaryTreeCase): Int = {
    tree match {
      case NumberNode(value) => value
      case OperatorNode("+", left, right) => evaluateTree(left) + evaluateTree(right)
      case OperatorNode("-", left, right) => evaluateTree(left) - evaluateTree(right)
      case OperatorNode("*", left, right) => evaluateTree(left) * evaluateTree(right)
      case OperatorNode("/", left, right) => evaluateTree(left) / evaluateTree(right)
      case _ => throw new IllegalArgumentException(s"Invalid binary tree node: $tree")
    }
  }
}
