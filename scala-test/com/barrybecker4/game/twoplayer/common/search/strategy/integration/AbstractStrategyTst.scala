/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy.integration

import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.twoplayer.common.{TwoPlayerBoard, TwoPlayerController, TwoPlayerMove, TwoPlayerPlayerOptions}
import com.barrybecker4.game.twoplayer.common.search.{ISearchableHelper, Progress, SearchableHelper}
import com.barrybecker4.game.twoplayer.common.search.options.{BestMovesSearchOptions, BruteSearchOptions, SearchOptions}
import com.barrybecker4.game.twoplayer.common.search.strategy.{SearchStrategy, SearchStrategyType}
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode
import org.junit.Assert.{assertEquals, assertTrue}
import org.junit.{Before, Test}

/**
 * Verify that all the methods in the SearchStrategy interface work as expected (especially search).
 * Note that these are really integration tests and not unit tests.
 * @author Barry Becker
 */
abstract class AbstractStrategyTst:

  protected var controller: TwoPlayerController[?, ?] = scala.compiletime.uninitialized
  protected var searchOptions: SearchOptions[?, ?] = scala.compiletime.uninitialized
  protected var helper: ISearchableHelper = scala.compiletime.uninitialized

  /** All tests should be able to run in less time than this. */
  private val MAX_NUM_SECONDS = 4.0

  @Before
  def setUp(): Unit =
    helper = createSearchableHelper
    controller = helper.createController
    initializeSearchOptions()

  private def initializeSearchOptions(): Unit =
    searchOptions = helper.createSearchOptions
    val players = controller.getPlayers
    players.getPlayer1.getOptions.asInstanceOf[TwoPlayerPlayerOptions[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]]]
      .setSearchOptions(searchOptions.asInstanceOf[SearchOptions[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]]])
    players.getPlayer2.getOptions.asInstanceOf[TwoPlayerPlayerOptions[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]]]
      .setSearchOptions(searchOptions.asInstanceOf[SearchOptions[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]]])

  protected def getBruteSearchOptions: BruteSearchOptions = searchOptions.getBruteSearchOptions

  protected def getBestMovesOptions: BestMovesSearchOptions = searchOptions.getBestMovesSearchOptions

  protected def createSearchableHelper: SearchableHelper

  protected def getSearchStrategyToTest: SearchStrategyType

  @Test
  def testZeroLookAheadSearch(): Unit =
    getBruteSearchOptions.setLookAhead(0)
    verifyMoves("ZeroLookAhead", getExpectedZeroLookAheadMoves)

  @Test
  def testOneLevelLookAheadSearch(): Unit =
    getBruteSearchOptions.setLookAhead(1)
    verifyMoves("OneLevelLookAhead", getExpectedOneLevelLookAheadMoves)

  @Test
  def testOneLevelWithQuiescenceSearch(): Unit =
    getBruteSearchOptions.setLookAhead(1)
    getBruteSearchOptions.setQuiescence(true)
    verifyMoves("OneLevelWithQuiescence", getExpectedOneLevelWithQuiescenceMoves)

  @Test
  def testOneLevelWithQuiescenceAndABSearch(): Unit =
    getBruteSearchOptions.setLookAhead(1)
    getBruteSearchOptions.setQuiescence(true)
    getBruteSearchOptions.setAlphaBeta(true)
    verifyMoves("OneLevelWithQuiescenceAndAB", getExpectedOneLevelWithQuiescenceAndABMoves)

  @Test
  def testTwoLevelLookAheadSearch(): Unit =
    getBruteSearchOptions.setLookAhead(2)
    verifyMoves("TwoLevelLookAhead", getExpectedTwoLevelLookAheadMoves)

  @Test
  def testFourLevelLookAheadSearch(): Unit =
    getBruteSearchOptions.setLookAhead(4)
    verifyMoves("FourLevelLookAhead", getExpectedFourLevelLookaheadMoves)

  @Test
  def testFourLevelBest20PercentSearch(): Unit =
    getBruteSearchOptions.setLookAhead(4)
    getBestMovesOptions.setPercentageBestMoves(20)
    getBestMovesOptions.setMinBestMoves(4)
    verifyMoves("FourLevelBest20Percent", getExpectedFourLevelBest20PercentMoves)

  @Test
  def testTwoLevelWithQuiescenceLookAheadSearch(): Unit =
    getBruteSearchOptions.setLookAhead(2)
    getBruteSearchOptions.setQuiescence(true)
    verifyMoves("TwoLevelWithQuiescence", getExpectedTwoLevelWithQuiescenceMoves)

  @Test
  def testTwoLevelWithQuiescenceAndABSearch(): Unit =
    getBruteSearchOptions.setLookAhead(1)
    getBruteSearchOptions.setQuiescence(true)
    getBruteSearchOptions.setAlphaBeta(true)
    verifyMoves("TwoLevelWithQuiescenceAndAB", getExpectedTwoLevelWithQuiescenceAndABMoves)

  @Test
  def testThreeLevelWithQuiescenceLookAheadSearch(): Unit =
    getBruteSearchOptions.setLookAhead(3)
    getBruteSearchOptions.setQuiescence(true)
    verifyMoves("ThreeLevelWithQuiescence", getExpectedThreeLevelWithQuiescenceMoves)

  @Test
  def testFourLevelWithQuiescenceLookAheadSearch(): Unit =
    getBruteSearchOptions.setLookAhead(4)
    getBruteSearchOptions.setQuiescence(true)
    verifyMoves("FourLevelWithQuiescence", getExpectedFourLevelWithQuiescenceMoves)

  @Test
  def testFourLevelNoAlphaBetaSearch(): Unit =
    getBruteSearchOptions.setLookAhead(4)
    getBruteSearchOptions.setAlphaBeta(false)
    verifyMoves("FourLevelNoAlphaBeta", getExpectedFourLevelNoAlphaBetaMoves)

  def verifyMoves(desc: String, expectedMoves: ExpectedMoveMatrix): Unit =
    searchOptions.setSearchStrategyMethod(getSearchStrategyToTest)
    val time = System.currentTimeMillis
    for prog <- Progress.values do
      verifyMove(prog, true, expectedMoves, desc)
      verifyMove(prog, false, expectedMoves, desc)
    val elapsed = (System.currentTimeMillis - time).toFloat / 1000.0
    assertTrue(
      "Took too long. Expected to be less than " + MAX_NUM_SECONDS + " seconds but was: " + elapsed + " seconds.",
      elapsed < MAX_NUM_SECONDS
    )

  def verifyMove(prog: Progress, player1: Boolean, expectedMoves: ExpectedMoveMatrix, desc: String): Unit =
    controller.restoreFromStream(helper.getTestResource(prog, player1))
    val expectedNext = expectedMoves.getExpectedMove(prog, player1)
    val strategy = createSearchStrategy
    val nextMove = searchForNextMove(strategy)
    val numMoves = strategy.getNumMovesConsidered
    val info = "" + getSearchStrategyToTest + " " + desc + " " + prog + " player1=" + player1
    if expectedNext.hasMovesConsidered then
      assertEquals("Unexpected number of moves considered.", expectedNext.getNumMovesConsidered, numMoves)
    assertEquals(
      info + "\nWe did not get the next move that we expected after searching.",
      expectedNext.getMove,
      nextMove
    )

  protected def getExpectedZeroLookAheadMoves: ExpectedMoveMatrix
  protected def getExpectedOneLevelLookAheadMoves: ExpectedMoveMatrix
  protected def getExpectedOneLevelWithQuiescenceMoves: ExpectedMoveMatrix
  protected def getExpectedOneLevelWithQuiescenceAndABMoves: ExpectedMoveMatrix
  protected def getExpectedTwoLevelLookAheadMoves: ExpectedMoveMatrix
  protected def getExpectedFourLevelLookaheadMoves: ExpectedMoveMatrix
  protected def getExpectedFourLevelBest20PercentMoves: ExpectedMoveMatrix
  protected def getExpectedTwoLevelWithQuiescenceMoves: ExpectedMoveMatrix
  protected def getExpectedTwoLevelWithQuiescenceAndABMoves: ExpectedMoveMatrix
  protected def getExpectedThreeLevelWithQuiescenceMoves: ExpectedMoveMatrix
  protected def getExpectedFourLevelWithQuiescenceMoves: ExpectedMoveMatrix
  protected def getExpectedFourLevelNoAlphaBetaMoves: ExpectedMoveMatrix

  protected def searchForNextMove(strategy: SearchStrategy[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]]): TwoPlayerMove =
    val lastMove = controller.getLastMove.asInstanceOf[TwoPlayerMove]
    val root = new SearchTreeNode(lastMove)
    val nextMove = strategy.search(lastMove, root)
    if searchOptions.getBruteSearchOptions.getLookAhead > 0 then
      assertTrue(
        "The last move (" + lastMove + ") was the same player as the next move (" + nextMove + ")",
        lastMove.isPlayer1 != nextMove.isPlayer1
      )
    nextMove

  protected def createSearchStrategy: SearchStrategy[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]] =
    searchOptions.asInstanceOf[SearchOptions[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]]].getSearchStrategy(
      controller.getSearchable().asInstanceOf[com.barrybecker4.game.twoplayer.common.search.Searchable[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]]],
      controller.getComputerWeights().getDefaultWeights
    )

object AbstractStrategyTst:
  val PLAYER1_PIECE: GamePiece = new GamePiece(true)
  val PLAYER2_PIECE: GamePiece = new GamePiece(false)
