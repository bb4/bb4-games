package com.barrybecker4.game.twoplayer.common.search.strategy.testcase

import org.w3c.dom.Node

/**
 * The set of cases in the test-cases node of the xml case file.
 * @author Barry Becker
 */
class SearchTestCases(testCasesNode: Node) extends java.util.ArrayList[SearchTestCase]:

  private val children = testCasesNode.getChildNodes
  private var i = 0
  while i < children.getLength do
    this.add(new SearchTestCase(children.item(i)))
    i += 1
