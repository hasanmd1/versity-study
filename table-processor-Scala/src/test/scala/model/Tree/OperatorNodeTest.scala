package model.Tree

import org.scalatest.FunSuite

class OperatorNodeTest extends FunSuite{
  test("Creating an OperatorNode with addition operator") {
    val leftNode = NumberNode(5)
    val rightNode = NumberNode(3)
    val operatorNode = OperatorNode("+", leftNode, rightNode)
    assert(operatorNode.operator === "+")
    assert(operatorNode.left === leftNode)
    assert(operatorNode.right === rightNode)
  }

  test("Creating an OperatorNode with subtraction operator") {
    val leftNode = NumberNode(10)
    val rightNode = NumberNode(4)
    val operatorNode = OperatorNode("-", leftNode, rightNode)
    assert(operatorNode.operator === "-")
    assert(operatorNode.left === leftNode)
    assert(operatorNode.right === rightNode)
  }

  test("Creating an OperatorNode with multiplication operator") {
    val leftNode = NumberNode(7)
    val rightNode = NumberNode(9)
    val operatorNode = OperatorNode("*", leftNode, rightNode)
    assert(operatorNode.operator === "*")
    assert(operatorNode.left === leftNode)
    assert(operatorNode.right === rightNode)
  }

  test("Creating an OperatorNode with division operator") {
    val leftNode = NumberNode(20)
    val rightNode = NumberNode(4)
    val operatorNode = OperatorNode("/", leftNode, rightNode)
    assert(operatorNode.operator === "/")
    assert(operatorNode.left === leftNode)
    assert(operatorNode.right === rightNode)
  }

  test("Creating an OperatorNode with a complex expression") {
    val leftNode = NumberNode(5)
    val rightNode = OperatorNode("*", NumberNode(3), NumberNode(2))
    val operatorNode = OperatorNode("+", leftNode, rightNode)
    assert(operatorNode.operator === "+")
    assert(operatorNode.left === leftNode)
    assert(operatorNode.right === rightNode)
  }

}
