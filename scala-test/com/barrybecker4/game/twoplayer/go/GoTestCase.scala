/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go

import com.barrybecker4.common.format.FormatUtil
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.common.TwoPlayerPlayerOptions
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategyType
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import org.junit.{After, Before}
import scala.jdk.CollectionConverters._

/**
 * Base class for all Go test cases. Most of this code could be moved to GoBoardConfigurator
 * @author Barry Becker
 */
abstract class GoTestCase:

  /** moved all test cases here so they are not included in the jar and do not need to be searched */
  val EXTERNAL_TEST_CASE_DIR: String = GoTestCase.EXTERNAL_TEST_CASE_DIR

  protected var controller: GoController = scala.compiletime.uninitialized

  /** common initialization for all go test cases. Override setOptionOverrides if you want different search parameters. */
  @Before
  def setUp(): Unit =
    GameContext.setDebugMode(GoTestCase.DEBUG_LEVEL)
    GameContext.loadResources("go")
    controller = new GoController(getBoardSize, 0)
    for player <- controller.getPlayers.asScala do
      val searchOptions: SearchOptions[?, ?] =
        player.getOptions.asInstanceOf[TwoPlayerPlayerOptions[GoMove, GoBoard]].getSearchOptions
      setOptionOverrides(searchOptions)

  @After
  def tearDown(): Unit =
    controller = null

  /**
   * Derived classes should override if they want different options.
   * @param sOptions default options to override
   */
  protected def setOptionOverrides(sOptions: SearchOptions[?, ?]): Unit =
    sOptions.getBruteSearchOptions.setAlphaBeta(true)
    sOptions.getBruteSearchOptions.setLookAhead(2)
    sOptions.getBestMovesSearchOptions.setPercentageBestMoves(40)
    sOptions.getBestMovesSearchOptions.setPercentLessThanBestThresh(0)
    sOptions.setSearchStrategyMethod(SearchStrategyType.NEGAMAX_W_MEMORY)

  /** Override if your test suite requires a different size. */
  protected def getBoardSize: Int = 13

  protected def getBoard: GoBoard = controller.getBoard.asInstanceOf[GoBoard]

  protected def restore(problemFile: String): Unit =
    val path = GoTestCase.EXTERNAL_TEST_CASE_DIR + problemFile + GoTestCase.SGF_EXTENSION
    val stream = getClass.getResourceAsStream(path)
    if stream == null then
      throw new IllegalArgumentException("Test resource not found (check path case): " + path)
    controller.restoreFromStream(stream)

  protected def getNextMove(problemFile: String, blackPlays: Boolean): GoMove =
    val time = System.currentTimeMillis
    restore(problemFile)
    controller.requestComputerMove(blackPlays, true)
    val m = controller.getLastMove.asInstanceOf[GoMove]
    val elapsedTime = (System.currentTimeMillis - time) / 1000.0
    GameContext.log(1, "got " + m + " in " + FormatUtil.formatNumber(elapsedTime) + " seconds.")
    m

  /**
   * @param isBlack true if black
   * @return the biggest black group if black is true else biggest white group.
   */
  protected def getBiggestGroup(isBlack: Boolean): IGoGroup =
    val groups = getBoard.getGroups
    var biggestGroup: IGoGroup = null
    for group <- groups.asScala do
      val stones = group.getStones
      if stones.iterator.next.getPiece.isOwnedByPlayer1 == isBlack then
        if biggestGroup == null || group.getNumStones > biggestGroup.getNumStones then
          biggestGroup = group
    biggestGroup


object GoTestCase:
  val EXTERNAL_TEST_CASE_DIR = "/com/barrybecker4/game/twoplayer/go/cases/"
  private val SGF_EXTENSION = ".sgf"
  /** -1 silences all GameContext.log output; use 0+ when debugging. */
  private val DEBUG_LEVEL = -1
