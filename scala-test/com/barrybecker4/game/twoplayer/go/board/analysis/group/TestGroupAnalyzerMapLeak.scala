/* Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.group

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.twoplayer.common.cache.ScoreCache
import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.GoSearchable
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import com.barrybecker4.game.twoplayer.go.options.{GoOptions, GoWeights}
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * GroupAnalyzerMap used WeakHashMap but GroupAnalyzer strongly refs its key and
 * registers as a change listener, so entries never get GC'd. During search that
 * accumulates until OOM unless the map is cleared when the board structure changes.
 */
class TestGroupAnalyzerMapLeak extends GoTestCase:

  override protected def getBoardSize: Int = 9

  @Test
  def makeWorthUndoDoesNotGrowAnalyzerMapUnbounded(): Unit =
    val searchable = new GoSearchable(getBoard, controller.getPlayers, new ScoreCache(), new GoOptions())
    val weights = new GoWeights().getPlayer1Weights

    searchable.makeInternalMove(new GoMove(new ByteLocation(3, 3), 0, new GoStone(true)))
    searchable.makeInternalMove(new GoMove(new ByteLocation(3, 4), 0, new GoStone(false)))
    searchable.makeInternalMove(new GoMove(new ByteLocation(4, 3), 0, new GoStone(true)))

    searchable.worth(searchable.getMoveList.getLastMove.get, weights)
    val afterFirst = searchable.analyzerMapSize

    for (r <- 5 to 8; c <- 5 to 8) do
      val m = new GoMove(new ByteLocation(r, c), 0, new GoStone(false))
      searchable.makeInternalMove(m)
      searchable.worth(m, weights)
      searchable.undoInternalMove(m)

    val afterMany = searchable.analyzerMapSize
    assertTrue(
      afterMany <= afterFirst + 4,
      s"analyzer map leaked: afterFirst=$afterFirst afterMany=$afterMany"
    )
