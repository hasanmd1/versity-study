package model.Tree

import org.scalatest.FunSuite

class NumberNodeTest extends FunSuite{
  test("Creating a NumberNode with a positive integer value") {
    val numberNode = NumberNode(42)
    assert(numberNode.value === 42)
  }
  test("Creating a NumberNode with a negative integer value") {
    val numberNode = NumberNode(-10)
    assert(numberNode.value === -10)
  }
  test("Creating a NumberNode with zero value") {
    val numberNode = NumberNode(0)
    assert(numberNode.value === 0)
  }

}
