/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategyType
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Arrays

/**
 * @author Barry Becker
 */
class TestLifeAndDeath extends GoTestCase:

  private val PREFIX1 = "lifeanddeath/"
  private val PREFIX2 = "problems/sgf/life_death/"

  override protected def setOptionOverrides(options: SearchOptions[?, ?]): Unit =
    options.getBruteSearchOptions.setAlphaBeta(true)
    options.getBruteSearchOptions.setLookAhead(1) // should be at least 3.
    options.getBestMovesSearchOptions.setPercentageBestMoves(60)
    options.getBruteSearchOptions.setQuiescence(false) // should be true
    options.setSearchStrategyMethod(SearchStrategyType.MINIMAX)

  @Test
  def testPass(): Unit = assertTrue(true)

  private def doLifeAndDeathTest(filename: String, row: Int, column: Int): Unit =
    val m = getNextMove(PREFIX1 + filename, true)
    verifyExpected(m, row, column)

  private def doLifeAndDeathTest2(
    filename: String,
    acceptableMoves: Array[Location],
    blackToPlay: Boolean
  ): Unit =
    val move = getNextMove(PREFIX2 + filename, blackToPlay)
    verifyAcceptable(move, acceptableMoves)

  private def verifyAcceptable(move: GoMove, acceptableMoves: Array[Location]): Unit =
    var pass = false
    for loc <- acceptableMoves do
      if isExpected(move, loc) then pass = true
    assertTrue(
      "The computed move (" + move + ") was not one that we though acceptable =" +
        Arrays.toString(acceptableMoves.asInstanceOf[Array[Object]]),
      pass
    )

  private def verifyExpected(m: GoMove, row: Int, col: Int): Unit =
    assertTrue(
      "Was expecting " + row + ", " + col + ", but instead got " + m,
      isExpected(m, row, col)
    )

  private def isExpected(m: GoMove, loc: Location): Boolean =
    isExpected(m, m.getToRow, loc.col)

  private def isExpected(m: GoMove, row: Int, col: Int): Boolean =
    m.getToRow == row && m.getToCol == col
