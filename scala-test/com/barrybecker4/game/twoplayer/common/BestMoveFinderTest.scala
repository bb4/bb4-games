/* Copyright by Barry G. Becker, 2000-2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.math.Range
import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.twoplayer.common.search.options.BestMovesSearchOptions
import org.junit.{Before, Test}
import org.junit.Assert.assertEquals

/**
 * @author Barry Becker
 */
class BestMoveFinderTest:

  private var finder: BestMoveFinder[TwoPlayerMove] = scala.compiletime.uninitialized
  private var options: BestMovesSearchOptions = scala.compiletime.uninitialized
  private var mList: MoveList[TwoPlayerMove] = scala.compiletime.uninitialized

  /** common initialization for all test cases. */
  @Before
  def setUp(): Unit =
    options = new BestMovesSearchOptions()
    finder = new BestMoveFinder(options)

  /** MinBestMoves must be greater than 0. */
  @Test(expected = classOf[IllegalArgumentException])
  def testMinBestMoves0(): Unit =
    options.setMinBestMoves(0)

  /**
   * if all the thresholds are set to 0, we still get one move (teh best one)
   * because 0 percent less than the best is the best
   */
  @Test
  def testNoMovesReturnedWhenThresholds0(): Unit =
    options.setPercentLessThanBestThresh(0)
    options.setPercentageBestMoves(0)
    options.setMinBestMoves(1)
    mList = finder.getBestMoves(createSimpleThreeMoveList(BestMoveFinderTest.PLAYER2))
    assertEquals("Unexpected number of best moves found", 1, mList.getNumMoves)

  /** If there are no moves to select from, then 0 will be returned. */
  @Test
  def testNoMovesReturnedWhenNoMovesToSelectFrom(): Unit =
    options.setPercentLessThanBestThresh(1)
    options.setPercentageBestMoves(1)
    options.setMinBestMoves(1)
    mList = finder.getBestMoves(new MoveList[TwoPlayerMove]())
    assertEquals("Unexpected number of best moves found", 0, mList.getNumMoves)

  @Test
  def testMinBestOfOneP1(): Unit =
    options.setPercentLessThanBestThresh(0)
    options.setPercentageBestMoves(0)
    options.setMinBestMoves(1)
    mList = finder.getBestMoves(createSimpleThreeMoveList(BestMoveFinderTest.PLAYER1))
    assertEquals("Unexpected number of best moves found", 1, mList.getNumMoves)
    assertEquals("Unexpected value", 100, mList.getFirstMove.getValue)

  @Test
  def testMinBestOfOneP2(): Unit =
    options.setPercentLessThanBestThresh(0)
    options.setPercentageBestMoves(0)
    options.setMinBestMoves(1)
    mList = finder.getBestMoves(createSimpleThreeMoveList(BestMoveFinderTest.PLAYER2))
    assertEquals("Unexpected number of best moves found", 1, mList.getNumMoves)
    assertEquals("Unexpected value", -10, mList.getFirstMove.getValue)

  @Test
  def testMinBestTwoP1(): Unit =
    options.setPercentLessThanBestThresh(0)
    options.setPercentageBestMoves(0)
    options.setMinBestMoves(2)
    mList = finder.getBestMoves(createSimpleThreeMoveList(BestMoveFinderTest.PLAYER1))
    assertEquals("Unexpected number of best moves found", 2, mList.getNumMoves)
    assertEquals("Unexpected value", 100, mList.getFirstMove.getValue)

  @Test
  def testMinBestTwoP2(): Unit =
    options.setPercentLessThanBestThresh(0)
    options.setPercentageBestMoves(0)
    options.setMinBestMoves(2)
    val list = finder.getBestMoves(createSimpleThreeMoveList(BestMoveFinderTest.PLAYER2))
    assertEquals("Unexpected number of best moves found", 2, list.getNumMoves)
    assertEquals("Unexpected value", -10, list.getFirstMove.getValue)

  @Test
  def testBestMinBiggerThanTotalP1(): Unit =
    options.setPercentLessThanBestThresh(0)
    options.setPercentageBestMoves(0)
    options.setMinBestMoves(5)
    mList = finder.getBestMoves(createSimpleThreeMoveList(BestMoveFinderTest.PLAYER1))
    assertEquals("Unexpected number of best moves found", 3, mList.getNumMoves)

  @Test
  def testBestMinBiggerThanTotalP2(): Unit =
    options.setPercentLessThanBestThresh(0)
    options.setPercentageBestMoves(0)
    options.setMinBestMoves(5)
    mList = finder.getBestMoves(createSimpleThreeMoveList(BestMoveFinderTest.PLAYER2))
    assertEquals("Unexpected number of best moves found", 3, mList.getNumMoves)

  @Test
  def testBestMinSmallerThanTotalP1(): Unit =
    options.setPercentLessThanBestThresh(0)
    options.setPercentageBestMoves(0)
    options.setMinBestMoves(5)
    mList = finder.getBestMoves(createSixMoveListWithNegatives(BestMoveFinderTest.PLAYER1))
    assertEquals("Unexpected number of best moves found", 5, mList.getNumMoves)

  @Test
  def testMinBestSmallerThanNumberReturnedP1(): Unit =
    options.setPercentLessThanBestThresh(50)
    options.setPercentageBestMoves(100)
    options.setMinBestMoves(2)
    mList = finder.getBestMoves(createSimpleThreeMoveList(BestMoveFinderTest.PLAYER1))
    assertEquals("Unexpected number of best moves found", 3, mList.getNumMoves)

  @Test
  def testBest20PercentMovesP1(): Unit =
    options.setPercentLessThanBestThresh(0)
    options.setPercentageBestMoves(20)
    options.setMinBestMoves(1)
    mList = finder.getBestMoves(createRangeMoveList(Range(-10, 10), BestMoveFinderTest.PLAYER1))
    assertEquals("Unexpected number of best moves found", 4, mList.getNumMoves)
    assertEquals("Unexpected value", 10, mList.getFirstMove.getValue)

  @Test
  def testBest20PercentMovesP2(): Unit =
    options.setPercentLessThanBestThresh(0)
    options.setPercentageBestMoves(20)
    options.setMinBestMoves(1)
    mList = finder.getBestMoves(createRangeMoveList(Range(-10, 10), BestMoveFinderTest.PLAYER2))
    assertEquals("Unexpected number of best moves found", 4, mList.getNumMoves)
    assertEquals("Unexpected value", -10, mList.getFirstMove.getValue)

  @Test
  def testBest25PercentLessThanBestThreshMovesP1(): Unit =
    options.setPercentLessThanBestThresh(25)
    options.setPercentageBestMoves(0)
    options.setMinBestMoves(1)
    mList = finder.getBestMoves(createRangeMoveList(Range(-20, 20), BestMoveFinderTest.PLAYER1))
    assertEquals("Unexpected number of best moves found", 11, mList.getNumMoves)
    assertEquals("Unexpected value", 20, mList.getFirstMove.getValue)

  @Test
  def testBest25PercentLessThanBestThreshMovesP1WhenAllPositive(): Unit =
    options.setPercentLessThanBestThresh(25)
    options.setPercentageBestMoves(0)
    options.setMinBestMoves(1)
    mList = finder.getBestMoves(createRangeMoveList(Range(1, 40), BestMoveFinderTest.PLAYER1))
    assertEquals("Unexpected number of best moves found", 10, mList.getNumMoves)
    assertEquals("Unexpected value", 40, mList.getFirstMove.getValue)

  @Test
  def testBest25PercentLessThanBestThreshMovesP2WhenAllPositive(): Unit =
    options.setPercentLessThanBestThresh(25)
    options.setPercentageBestMoves(0)
    options.setMinBestMoves(1)
    mList = finder.getBestMoves(createRangeMoveList(Range(1, 40), BestMoveFinderTest.PLAYER2))
    assertEquals("Unexpected number of best moves found", 10, mList.getNumMoves)
    assertEquals("Unexpected value", 1, mList.getFirstMove.getValue)

  @Test
  def testBest25PercentLessThanBestThreshMovesP1WhenAllNegative(): Unit =
    options.setPercentLessThanBestThresh(25)
    options.setPercentageBestMoves(0)
    options.setMinBestMoves(1)
    mList = finder.getBestMoves(createRangeMoveList(Range(-40, 1), BestMoveFinderTest.PLAYER1))
    assertEquals("Unexpected number of best moves found", 11, mList.getNumMoves)
    assertEquals("Unexpected value", 1, mList.getFirstMove.getValue)

  @Test
  def testBest30PercentLessThanBestThreshMovesP1(): Unit =
    options.setPercentLessThanBestThresh(30)
    options.setPercentageBestMoves(0)
    options.setMinBestMoves(1)
    mList = finder.getBestMoves(createSixMoveListWithNegatives(BestMoveFinderTest.PLAYER1))
    assertEquals("Unexpected number of best moves found", 4, mList.getNumMoves)
    assertEquals("Unexpected value", 50, mList.getFirstMove.getValue)

  @Test
  def testBest30PercentLessThanBestThreshMovesP2(): Unit =
    options.setPercentLessThanBestThresh(30)
    options.setPercentageBestMoves(0)
    options.setMinBestMoves(1)
    mList = finder.getBestMoves(createSixMoveListWithNegatives(BestMoveFinderTest.PLAYER2))
    assertEquals("Unexpected number of best moves found", 1, mList.getNumMoves)
    assertEquals("Unexpected value", -550, mList.getFirstMove.getValue)

  @Test
  def testBest25PercentLessThanBestThreshMovesP2WhenAllNegative(): Unit =
    options.setPercentLessThanBestThresh(25)
    options.setPercentageBestMoves(0)
    options.setMinBestMoves(1)
    mList = finder.getBestMoves(createRangeMoveList(Range(-40, 1), BestMoveFinderTest.PLAYER2))
    assertEquals("Unexpected number of best moves found", 11, mList.getNumMoves)
    assertEquals("Unexpected value", -40, mList.getFirstMove.getValue)

  @Test
  def testBest20PercentLessThanBestThreshMovesP1(): Unit =
    options.setPercentLessThanBestThresh(20)
    options.setPercentageBestMoves(0)
    options.setMinBestMoves(1)
    mList = finder.getBestMoves(createRangeMoveList(Range(-10, 9), BestMoveFinderTest.PLAYER1))
    assertEquals("Unexpected number of best moves found", 4, mList.getNumMoves)
    assertEquals("Unexpected value", 9, mList.getFirstMove.getValue)

  @Test
  def testBest20PercentLessThanBestThreshMovesP2(): Unit =
    options.setPercentLessThanBestThresh(20)
    options.setPercentageBestMoves(0)
    options.setMinBestMoves(1)
    mList = finder.getBestMoves(createRangeMoveList(Range(-10, 9), BestMoveFinderTest.PLAYER2))
    assertEquals("Unexpected number of best moves found", 4, mList.getNumMoves)
    assertEquals("Unexpected value", -10, mList.getFirstMove.getValue)

  @Test
  def testBestMinMoreThanPercentMovesP1(): Unit =
    options.setPercentLessThanBestThresh(0)
    options.setPercentageBestMoves(20)
    options.setMinBestMoves(10)
    val list = finder.getBestMoves(createRangeMoveList(Range(-10, 10), BestMoveFinderTest.PLAYER1))
    assertEquals("Unexpected number of best moves found", 10, list.getNumMoves)

  @Test
  def testBestMinMoreThanPercentLessThanBestThreshMovesP2(): Unit =
    options.setPercentLessThanBestThresh(0)
    options.setPercentageBestMoves(20)
    options.setMinBestMoves(10)
    mList = finder.getBestMoves(createRangeMoveList(Range(-10, 10), BestMoveFinderTest.PLAYER1))
    assertEquals("Unexpected number of best moves found", 10, mList.getNumMoves)

  @Test
  def test50PercentLessThanBest5P1(): Unit =
    options.setPercentLessThanBestThresh(50)
    options.setPercentageBestMoves(0)
    options.setMinBestMoves(1)
    mList = finder.getBestMoves(createSimpleFiveMoveList(BestMoveFinderTest.PLAYER1))
    assertEquals("Unexpected number of best moves found", 1, mList.getNumMoves)
    assertEquals("Unexpected value", 1000, mList.getFirstMove.getValue)

  @Test
  def test50PercentLessThanBest5P2(): Unit =
    options.setPercentLessThanBestThresh(50)
    options.setPercentageBestMoves(0)
    options.setMinBestMoves(1)
    mList = finder.getBestMoves(createSimpleFiveMoveList(BestMoveFinderTest.PLAYER2))
    assertEquals("Unexpected number of best moves found", 4, mList.getNumMoves)
    assertEquals("Unexpected value", -5, mList.getFirstMove.getValue)

  @Test
  def test50PercentLessThanBestWithDuplicatesP1(): Unit =
    options.setPercentLessThanBestThresh(50)
    options.setPercentageBestMoves(0)
    options.setMinBestMoves(1)
    mList = finder.getBestMoves(createMoveListWithDuplicateScores(BestMoveFinderTest.PLAYER1))
    assertEquals("Unexpected number of best moves found", 1, mList.getNumMoves)
    assertEquals("Unexpected value", 1000, mList.getFirstMove.getValue)

  @Test
  def test50PercentLessThanBestWithDuplicatesP2(): Unit =
    options.setPercentLessThanBestThresh(50)
    options.setPercentageBestMoves(0)
    options.setMinBestMoves(1)
    mList = finder.getBestMoves(createMoveListWithDuplicateScores(BestMoveFinderTest.PLAYER2))
    assertEquals("Unexpected number of best moves found", 3, mList.getNumMoves)
    assertEquals("Unexpected value", 5, mList.getFirstMove.getValue)

  @Test
  def testGetAllWhen100PercentLessThanBestP1(): Unit =
    options.setPercentLessThanBestThresh(100)
    options.setPercentageBestMoves(1)
    options.setMinBestMoves(1)
    mList = finder.getBestMoves(createSimpleFiveMoveList(BestMoveFinderTest.PLAYER1))
    assertEquals("Unexpected number of best moves found", 5, mList.getNumMoves)

  @Test
  def testGetAllWhen100PercentLessThanBestP2(): Unit =
    options.setPercentLessThanBestThresh(100)
    options.setPercentageBestMoves(1)
    options.setMinBestMoves(1)
    mList = finder.getBestMoves(createSimpleFiveMoveList(BestMoveFinderTest.PLAYER2))
    assertEquals("Unexpected number of best moves found", 5, mList.getNumMoves)

  private def createSimpleThreeMoveList(player1: Boolean): MoveList[TwoPlayerMove] =
    val moveList = new MoveList[TwoPlayerMove]()
    moveList.add(TwoPlayerMove.createMove(new ByteLocation(1, 3), 50, new GamePiece(player1)))
    moveList.add(TwoPlayerMove.createMove(new ByteLocation(1, 2), 100, new GamePiece(player1)))
    moveList.add(TwoPlayerMove.createMove(new ByteLocation(1, 4), -10, new GamePiece(player1)))
    moveList

  private def createSimpleFiveMoveList(player1: Boolean): MoveList[TwoPlayerMove] =
    val moveList = new MoveList[TwoPlayerMove]()
    moveList.add(TwoPlayerMove.createMove(new ByteLocation(1, 3), 0, new GamePiece(player1)))
    moveList.add(TwoPlayerMove.createMove(new ByteLocation(1, 2), 1000, new GamePiece(player1)))
    moveList.add(TwoPlayerMove.createMove(new ByteLocation(1, 5), 50, new GamePiece(player1)))
    moveList.add(TwoPlayerMove.createMove(new ByteLocation(1, 6), -5, new GamePiece(player1)))
    moveList.add(TwoPlayerMove.createMove(new ByteLocation(1, 4), 5, new GamePiece(player1)))
    moveList

  private def createMoveListWithDuplicateScores(player1: Boolean): MoveList[TwoPlayerMove] =
    val moveList = new MoveList[TwoPlayerMove]()
    moveList.add(TwoPlayerMove.createMove(new ByteLocation(1, 2), 500, new GamePiece(player1)))
    moveList.add(TwoPlayerMove.createMove(new ByteLocation(1, 3), 1000, new GamePiece(player1)))
    moveList.add(TwoPlayerMove.createMove(new ByteLocation(1, 5), 500, new GamePiece(player1)))
    moveList.add(TwoPlayerMove.createMove(new ByteLocation(1, 4), 5, new GamePiece(player1)))
    moveList

  private def createSixMoveListWithNegatives(player1: Boolean): MoveList[TwoPlayerMove] =
    val moveList = new MoveList[TwoPlayerMove]()
    moveList.add(TwoPlayerMove.createMove(new ByteLocation(1, 3), -10, new GamePiece(player1)))
    moveList.add(TwoPlayerMove.createMove(new ByteLocation(1, 2), -550, new GamePiece(player1)))
    moveList.add(TwoPlayerMove.createMove(new ByteLocation(1, 7), -50, new GamePiece(player1)))
    moveList.add(TwoPlayerMove.createMove(new ByteLocation(1, 5), -250, new GamePiece(player1)))
    moveList.add(TwoPlayerMove.createMove(new ByteLocation(1, 8), 50, new GamePiece(player1)))
    moveList.add(TwoPlayerMove.createMove(new ByteLocation(1, 4), -5, new GamePiece(player1)))
    moveList

  /** @return creates a move with every value in the range */
  private def createRangeMoveList(range: Range, player1: Boolean): MoveList[TwoPlayerMove] =
    val moveList = new MoveList[TwoPlayerMove]()
    var i = range.min.toInt
    while i <= range.max.toInt do
      moveList.add(TwoPlayerMove.createMove(new ByteLocation(i / 40, i % 40), i, new GamePiece(player1)))
      i += 1
    moveList

object BestMoveFinderTest:
  private val PLAYER1 = true
  private val PLAYER2 = false
