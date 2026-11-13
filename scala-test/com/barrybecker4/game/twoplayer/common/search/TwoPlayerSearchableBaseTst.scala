/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search

import com.barrybecker4.game.common.{GameContext, MoveList}
import com.barrybecker4.game.twoplayer.common.*
import com.barrybecker4.game.twoplayer.common.search.options.{BestMovesSearchOptions, SearchOptions}
import com.barrybecker4.optimization.parameter.ParameterArray
import org.junit.jupiter.api.Assertions.{assertEquals, assertFalse, assertTrue}
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import scala.jdk.CollectionConverters.*

/**
 * Verify that all the methods in the Searchable interface work as expected.
 * Derived test classes will exercise these methods for specific game instances.
 */
abstract class TwoPlayerSearchableBaseTst extends SearchableBaseTst:

  /** -1 silences all GameContext.log output; use 0+ when debugging. */
  private val DEFAULT_DEBUG_LEVEL = -1
  private val DEFAULT_LOOKAHEAD = 2
  private val DEFAULT_BEST_PERCENTAGE = 100

  private var controller: TwoPlayerController[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]] =
    scala.compiletime.uninitialized
  private var searchOptions_ : SearchOptions[?, ?] = scala.compiletime.uninitialized

  @BeforeEach
  override def setUp(): Unit =
    GameContext.setDebugMode(getDebugLevel)
    super.setUp()
    searchable = getController.getSearchable()
    setPlayerSearchOptions()

  private def setPlayerSearchOptions(): Unit =
    searchOptions_ = createDefaultOptions()
    val players = controller.getPlayers
    val opts = searchOptions_.asInstanceOf[SearchOptions[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]]]
    players.getPlayer1.getOptions.asInstanceOf[TwoPlayerPlayerOptions[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]]]
      .setSearchOptions(opts)
    players.getPlayer2.getOptions.asInstanceOf[TwoPlayerPlayerOptions[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]]]
      .setSearchOptions(opts)

  override protected def createSearchableHelper: ISearchableHelper

  protected def createDefaultOptions(): SearchOptions[?, ?] =
    val options = helper.createSearchOptions
    options.getBestMovesSearchOptions.setPercentageBestMoves(DEFAULT_BEST_PERCENTAGE)
    options.getBestMovesSearchOptions.setPercentLessThanBestThresh(0)
    options.getBestMovesSearchOptions.setMinBestMoves(2)
    val sOptions = options.getBruteSearchOptions
    sOptions.setLookAhead(DEFAULT_LOOKAHEAD)
    sOptions.setAlphaBeta(true)
    sOptions.setQuiescence(false)
    options

  protected def restore(problemFileBase: String): Unit =
    try
      getController.restoreFromStream(helper.getTestResource(problemFileBase))
    catch
      case e: IllegalArgumentException =>
        throw new IllegalArgumentException(
          "Could not find problem file " + helper.getTestResourceLocation(problemFileBase),
          e
        )

  protected def getController: TwoPlayerController[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]] =
    if controller == null then
      controller = helper.createController
    controller

  protected def createInitialMove: TwoPlayerMove

  protected def getDebugLevel: Int = DEFAULT_DEBUG_LEVEL

  protected def getTwoPlayerOptions: TwoPlayerOptions = getController.getOptions

  protected def getSearchOptions: SearchOptions[?, ?] = searchOptions_

  protected def getBestMovesOptions: BestMovesSearchOptions = searchOptions_.getBestMovesSearchOptions

  @Test
  override def testLookaheadValue(): Unit =
    val opts = getSearchOptions.getBruteSearchOptions
    assertEquals(DEFAULT_LOOKAHEAD, opts.getLookAhead, "Unexpected lookahead value.")
    opts.setLookAhead(7)
    assertEquals(7, opts.getLookAhead, "Unexpected lookahead value.")

  @Test
  override def testAlphaBetaValue(): Unit =
    val opts = getSearchOptions.getBruteSearchOptions
    assertEquals(true, opts.getAlphaBeta, "Unexpected alphabeta value.")
    opts.setAlphaBeta(false)
    assertEquals(false, opts.getAlphaBeta, "Unexpected alphabeta value.")

  @Test
  override def testQuiescenceValue(): Unit =
    val opts = getSearchOptions.getBruteSearchOptions
    assertEquals(false, opts.getQuiescence, "Unexpected quiessence value.")
    opts.setQuiescence(true)
    assertEquals(true, opts.getQuiescence, "Unexpected quiessence value.")

  @Test
  def testDoneBeforeAnyMovesMade(): Unit =
    assertFalse(searchable.done(null, false), "We cannot be done if no moves have been made yet. ")

  @Test
  def testDoneNullAfterFirstMove(): Unit =
    controller.computerMovesFirst()
    assertTrue(searchable.done(null, false), "We expect to be done if our next move is null and at least one move has been made. ")

  @Test
  def testDoneStartGame(): Unit =
    assertFalse(searchable.done(createInitialMove, false), "We don't expect to be done after making the very first move. ")

  @Test
  def testNotDoneMidGame(): Unit

  @Test
  def testDoneForMidGameWin(): Unit

  @Test
  def testDoneEndGame(): Unit

  @Test
  def testGenerateMovesBeforeFirstMove(): Unit =
    val moves = searchable.generateMoves(null, weights)
    assertTrue(moves != null, "We expect the move list to be non-null at the very start of the game.")
    val exp = getExpectedNumGeneratedMovesBeforeFirstMove
    assertEquals(exp, moves.size, "Unexpected number of generated moves before the first move has been played.")

  protected def getExpectedNumGeneratedMovesBeforeFirstMove: Int = 0

  @Test
  def testGenerateMovesAfterFirstMove(): Unit =
    controller.computerMovesFirst()
    val wts = weights
    val lastMove = controller.getLastMove
    val moves = searchable.generateMoves(lastMove, wts)
    assertTrue(moves != null, "We expect the move list to be non-null very start of the game.")
    assertTrue(moves.size > 0, "We expected some valid next moves at the very start of the game.")

  @Test def testGenerateAllP1MovesMidGame(): Unit = assertFalse(false)
  @Test def testGenerateTopP1MovesMidGame(): Unit = assertFalse(false)
  @Test def testGenerateAllP1MovesEndGame(): Unit = assertFalse(false)
  @Test def testGenerateTopP1MovesEndGame(): Unit = assertFalse(false)
  @Test def testGenerateAllP2MovesMidGame(): Unit = assertFalse(false)
  @Test def testGenerateTopP2MovesMidGame(): Unit = assertFalse(false)
  @Test def testGenerateAllP2MovesEndGame(): Unit = assertFalse(false)
  @Test def testGenerateTopP2MovesEndGame(): Unit = assertFalse(false)

  @Test
  def testGenerateUrgentMovesAtStartOfGame(): Unit =
    val moves = searchable.generateUrgentMoves(null, weights)
    assertTrue(moves != null, "We expected move list to be non-null.")
    assertTrue(moves.size == 0, "We expected no urgent moves at the start of the game, but was:" + moves)

  @Test
  def testGenerateUrgentMoves(): Unit =
    assertFalse(false)

  @Test
  def testInJeopardy(): Unit =
    var actualInJeopardy = searchable.inJeopardy(null, weights)
    assertFalse(actualInJeopardy, "We don't expect anything to be in jeopardy at the very start of the game.")
    val lastMove = createInitialMove
    searchable.makeInternalMove(lastMove)
    actualInJeopardy =
      searchable.inJeopardy(lastMove, getController.getComputerWeights().getPlayer2Weights)

  protected def checkMoveListAgainstExpected(
      title: String,
      expectedMoves: Array[TwoPlayerMove],
      moves: MoveList[TwoPlayerMove]
  ): Unit =
    if expectedMoves.length != moves.size then
      printMoves(title, moves)
    assertEquals(expectedMoves.length, moves.size, "Unexpected number of generated moves. The moves generated were:\n" + moves)
    val diffs = new StringBuilder("")
    for i <- 0 until moves.size do
      val move = moves.get(i)
      val expMove = expectedMoves(i)
      if move != expMove then
        diffs.append(i).append(") Unexpected moves.\n Expected ").append(expMove)
          .append(" \nBut got ").append(move).append("\n")
    if diffs.length > 0 then
      printMoves(title, moves)
    assertTrue(diffs.length == 0, "There were unexpected generated moves for " + title + "\n" + diffs)

  protected def weights: ParameterArray =
    getController.getComputerWeights().getPlayer1Weights

  protected def printMoves(name: String, moves: MoveList[TwoPlayerMove]): Unit =
    System.out.println("generated moves for " + name + " were:")
    for m <- moves.asScala do
      System.out.println(m.asInstanceOf[TwoPlayerMove].getConstructorString)
