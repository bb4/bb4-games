/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go

import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategyType
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Test a collection of problems from
 * Martin Mueller (mmueller)
 * Markus Enzenberger (emarkus)
 * email domain: cs.ualberta.ca
 * @author Barry Becker
 */
class TestBlunderCollection extends GoTestCase:

  private val PREFIX = "problems/sgf/blunder/"

  override protected def setOptionOverrides(options: SearchOptions[?, ?]): Unit =
    options.getBruteSearchOptions.setAlphaBeta(true)
    options.getBruteSearchOptions.setLookAhead(1) // should be 2 or more
    options.getBestMovesSearchOptions.setPercentageBestMoves(80)
    options.getBruteSearchOptions.setQuiescence(false) //true)
    options.setSearchStrategyMethod(SearchStrategyType.NEGASCOUT_W_MEMORY)

  @Test
  def testPass(): Unit = assertTrue(true)
