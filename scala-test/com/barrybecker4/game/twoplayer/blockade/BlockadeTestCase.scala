/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade

import com.barrybecker4.game.common.{GameContext, Move}
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove
import com.barrybecker4.game.twoplayer.common.TwoPlayerPlayerOptions
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategyType
import com.barrybecker4.ui.file.GenericFileFilter
import org.junit.Assert.assertTrue
import org.junit.Before

/** Base class for all Blockade test cases. @author Barry Becker */
abstract class BlockadeTestCase:

  /** moved all test cases here so they are not included in the jar and do not need to be searched */
  private val EXTERNAL_TEST_CASE_DIR = "/com/barrybecker4/game/twoplayer/blockade/cases/"
  private val SGF_EXTENSION = ".sgf"

  protected var controller_ : BlockadeController = scala.compiletime.uninitialized

  @Before
  def setUp(): Unit =
    GameContext.setDebugMode(-1)
    GameContext.loadResources("blockade")
    controller_ = new BlockadeController()
    import scala.jdk.CollectionConverters._
    for player <- controller_.getPlayers.asScala do
      val sOptions: SearchOptions[?, ?] =
        player.getOptions.asInstanceOf[TwoPlayerPlayerOptions[BlockadeMove, BlockadeBoard]].getSearchOptions
      sOptions.getBruteSearchOptions.setAlphaBeta(true)
      sOptions.getBruteSearchOptions.setLookAhead(3)
      sOptions.getBestMovesSearchOptions.setPercentageBestMoves(100)
      sOptions.setSearchStrategyMethod(SearchStrategyType.MINIMAX)

  protected def restore(problemFile: String): Unit =
    val path = EXTERNAL_TEST_CASE_DIR + problemFile + SGF_EXTENSION
    val stream = getClass.getResourceAsStream(path)
    if stream == null then
      throw new IllegalArgumentException("Test resource not found (check path case): " + path)
    controller_.restoreFromStream(stream)

  protected def getFilesMatching(directory: String, pattern: String): Array[String] =
    GenericFileFilter.getFilesMatching(EXTERNAL_TEST_CASE_DIR + directory, pattern)

  protected def getNextMove(problemFile: String, firstPlayerPlays: Boolean): Move =
    GameContext.log(1, "finding next move for " + problemFile + " ...")
    val time = System.currentTimeMillis
    restore(problemFile)
    controller_.requestComputerMove(firstPlayerPlays, true)
    val m = controller_.getLastMove
    val elapsedTime = (System.currentTimeMillis - time) / 1000
    GameContext.log(1, "got " + m + " in " + elapsedTime + " seconds.")
    m

  protected def checkExpected(m: BlockadeMove, row: Int, col: Int): Unit =
    assertTrue(
      "Was expecting " + row + ", " + col + ", but instead got " + m,
      m.getToRow == row && m.getToCol == col
    )
