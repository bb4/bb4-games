package com.barrybecker4.game.twoplayer.common.search.strategy.testcase

import com.barrybecker4.common.xml.DomUtil
import com.barrybecker4.game.twoplayer.common.search.TwoPlayerMoveStub

import java.io.File

/**
 * Reads in the test case definition from the xml case file and
 * runs the search methods specified and verifies the expected results.
 * @author Barry Becker
 */
class SearchTestExample(file: File):

  private val doc = DomUtil.parseXMLFile(file)
  assert(doc != null, "Check contents of " + file.getAbsolutePath)
  private val root = doc.getDocumentElement
  private val name_ = DomUtil.getAttribute(root, "name")
  private val children = root.getChildNodes
  private val testCases_ = new SearchTestCases(children.item(0))
  private val gameTree = new GameTree(children.item(1))

  def getName: String = name_

  def getTestCases: SearchTestCases = testCases_

  def getTree(rootPlayer1: Boolean): TwoPlayerMoveStub =
    gameTree.getCloneWithRootPlayer1(rootPlayer1)
