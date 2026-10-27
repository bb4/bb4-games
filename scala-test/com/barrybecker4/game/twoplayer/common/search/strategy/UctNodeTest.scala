/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy

import com.barrybecker4.common.format.FormatUtil
import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.common.search.TwoPlayerMoveStub
import com.barrybecker4.game.twoplayer.common.search.options.MaximizationStyle
import com.barrybecker4.game.twoplayer.common.search.tree.NodeAttributes
import org.junit.Assert.{assertEquals, assertFalse, assertTrue}
import org.junit.Test

/**
 * Test node in in-memory UCT tree.
 * @author Barry Becker
 */
class UctNodeTest:

  /** instance under test */
  private var uctNode: UctNode[TwoPlayerMove] = scala.compiletime.uninitialized

  @Test
  def testConstructionOfNodeWithNoChildren(): Unit =
    uctNode = new UctNode(UctNodeTest.P2_MOVE)
    assertEquals("Unexpected move", UctNodeTest.P2_MOVE, uctNode.getMove)
    assertEquals("Unexpected bestNode", null, uctNode.findBestChildMove(MaximizationStyle.WIN_RATE))
    assertFalse("Unexpected children", uctNode.hasChildren)
    assertEquals("Unexpected numVisits", 0, uctNode.getNumVisits)
    // The winrate is 0.5 (tie) + 10/WINNING = 0.50122
    assertEquals("Unexpected winRate", 0.5012207f, uctNode.getWinRate, UctNodeTest.TOL)
    val expectedAttributes = new NodeAttributes()
    expectedAttributes.put("visits", "0")
    expectedAttributes.put("wins", "0.0")
    expectedAttributes.put("winRate", "0.50122")
    assertEquals("Unexpected attrs", expectedAttributes, uctNode.getAttributes)
    assertEquals("Unexpected uctValue", 1000.50122, uctNode.calculateUctValue(1.0, 1), UctNodeTest.TOL)

  @Test
  def testUpdateWinP1(): Unit =
    uctNode = new UctNode(UctNodeTest.P1_MOVE)
    uctNode.update(1.0)
    assertEquals("Unexpected numWins", 1.0f, uctNode.getWinRate, UctNodeTest.TOL)
    uctNode.update(0.0)
    assertEquals("Unexpected numWins", 0.5f, uctNode.getWinRate, UctNodeTest.TOL)

  @Test
  def testUpdateWinP2(): Unit =
    uctNode = new UctNode(UctNodeTest.P2_MOVE)
    uctNode.update(1.0)
    assertEquals("Unexpected numWins", 0.0f, uctNode.getWinRate, UctNodeTest.TOL)
    uctNode.update(0.0)
    assertEquals("Unexpected numWins", 0.5f, uctNode.getWinRate, UctNodeTest.TOL)

  @Test
  def testSetBestNodeWithAllZeroWinrate(): Unit =
    uctNode = new UctNode(UctNodeTest.P2_MOVE)
    val moves = new MoveList[TwoPlayerMove]()
    val firstMove: TwoPlayerMove = new TwoPlayerMoveStub(5, false, new ByteLocation(1, 1), null)
    val goodMove: TwoPlayerMove = new TwoPlayerMoveStub(10, false, new ByteLocation(1, 2), null)
    moves.add(firstMove)
    moves.add(goodMove)
    moves.add(new TwoPlayerMoveStub(9, false, new ByteLocation(1, 3), null))
    uctNode.addChildren(moves)
    // good move is selected first because it has a better winrate using static evaluation.
    assertEquals("Unexpected bestMove", goodMove, uctNode.findBestChildMove(MaximizationStyle.WIN_RATE))

  @Test
  def testSetBestNode(): Unit =
    uctNode = new UctNode(UctNodeTest.P2_MOVE)
    val moves = new MoveList[TwoPlayerMove]()
    val firstMove: TwoPlayerMove = new TwoPlayerMoveStub(5, false, new ByteLocation(1, 1), null)
    val goodMove: TwoPlayerMove = new TwoPlayerMoveStub(10, false, new ByteLocation(1, 2), null)
    moves.add(firstMove)
    moves.add(goodMove)
    moves.add(new TwoPlayerMoveStub(9, false, new ByteLocation(1, 3), null))
    uctNode.addChildren(moves)
    val secondNode = uctNode.getChildren.get(1)
    secondNode.update(0.0)
    secondNode.update(1.0)
    secondNode.update(0.0)
    // secondMove is selected since it has a winrate of 0.6667
    assertEquals("Unexpected bestMove", secondNode.getMove, uctNode.findBestChildMove(MaximizationStyle.WIN_RATE))

  /** It should be a large value in this case so it gets selected to be visited first. */
  @Test
  def testCalcUctValueWhenNoVisits(): Unit =
    uctNode = new UctNode(UctNodeTest.P2_MOVE)
    assertTrue("Unexpected uctValue", uctNode.calculateUctValue(1.0, 1) > 100)

  @Test
  def testCalcUctValueWithOneVisitNoWinsOneParentVisitP1(): Unit =
    uctNode = new UctNode(UctNodeTest.P1_MOVE)
    uctNode.update(0.0)
    assertEquals("Unexpected uctValue", 0.0, uctNode.calculateUctValue(1.0, 1), UctNodeTest.TOL)

  @Test
  def testCalcUctValueWithOneVisitNoWinsOneParentVisitP2(): Unit =
    uctNode = new UctNode(UctNodeTest.P2_MOVE)
    uctNode.update(1.0)
    assertEquals("Unexpected uctValue", 0.0, uctNode.calculateUctValue(1.0, 1), UctNodeTest.TOL)

  @Test
  def testCalcUctValueWithOneVisitNoWinsTwoParentVisits(): Unit =
    uctNode = new UctNode(UctNodeTest.P2_MOVE)
    uctNode.update(1.0)
    assertEquals("Unexpected uctValue", 0.3723297411, uctNode.calculateUctValue(1.0, 2), UctNodeTest.TOL)

  @Test
  def testCalcUctValueWithOneVisitNoWins32ParentVisitsP1Won(): Unit =
    uctNode = new UctNode(UctNodeTest.P2_MOVE)
    uctNode.update(1.0)
    assertEquals("Unexpected uctValue", 0.83255461, uctNode.calculateUctValue(1.0, 32), UctNodeTest.TOL)

  @Test
  def testCalcUctValueWithOneVisitNoWins32ParentVisitsP2Won(): Unit =
    uctNode = new UctNode(UctNodeTest.P2_MOVE)
    uctNode.update(0.0)
    assertEquals("Unexpected uctValue", 1.83255461, uctNode.calculateUctValue(1.0, 32), UctNodeTest.TOL)

  @Test
  def testCalcUctValueWith10Visits1Win32ParentVisitsP1WonAll(): Unit =
    uctNode = new UctNode(UctNodeTest.P2_MOVE)
    var i = 0
    while i < 10 do
      uctNode.update(1.0)
      i += 1
    verifyUctValues(0.026328, 0.26328, 2.6328)

  @Test
  def testCalcUctValueWith10Visits1Win32ParentVisitsP2WonAll(): Unit =
    uctNode = new UctNode(UctNodeTest.P2_MOVE)
    var i = 0
    while i < 10 do
      uctNode.update(0.0)
      i += 1
    verifyUctValues(1.026328, 1.26328, 3.6328)

  /** Check a mix of calculated UCT values. */
  private def verifyUctValues(
      expMoreExploitValue: Double,
      expBalancedValue: Double,
      expMoreExploreValue: Double
  ): Unit =
    assertEquals(
      "Unexpected uctValue (more exploit)",
      expMoreExploitValue,
      uctNode.calculateUctValue(0.1, 32),
      UctNodeTest.TOL
    )
    assertEquals(
      "Unexpected uctValue (balanced)",
      expBalancedValue,
      uctNode.calculateUctValue(1.0, 32),
      UctNodeTest.TOL
    )
    assertEquals(
      "Unexpected uctValue (more explore)",
      expMoreExploreValue,
      uctNode.calculateUctValue(10.0, 32),
      UctNodeTest.TOL
    )

  private def calcUctTable(parentVisits: Int): Unit =
    var numVisits = 5
    while numVisits <= 25 do
      uctNode = new UctNode(UctNodeTest.P2_MOVE)
      uctNode.update(1.0)
      var winRate = 0.0
      while winRate <= 1.0 do
        while Math.round(uctNode.getWinRate * 100.0) < Math.round(100.0 * winRate) do
          uctNode.update(0.0)
        var eeRatio = 0.0
        while eeRatio <= 2.0 do
          val v = uctNode.calculateUctValue(eeRatio, parentVisits)
          System.out.println(
            "t" + parentVisits + "\tt" + numVisits + "\tt" + uctNode.getWinRate +
              "\tt" + eeRatio + "\t" + FormatUtil.formatNumber(v)
          )
          eeRatio += 0.5
        winRate += 0.20
      numVisits += 5

object UctNodeTest:
  /** a Player1 move to put in the node */
  private val P1_MOVE: TwoPlayerMove =
    new TwoPlayerMoveStub(10, true, new ByteLocation(1, 1), null)

  /** a Player2 move to put in the node */
  private val P2_MOVE: TwoPlayerMove =
    new TwoPlayerMoveStub(10, false, new ByteLocation(1, 1), null)

  private val TOL = 0.0001
