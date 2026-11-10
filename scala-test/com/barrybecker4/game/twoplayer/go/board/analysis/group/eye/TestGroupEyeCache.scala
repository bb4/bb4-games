/* Copyright by Barry G. Becker, 2011-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.analysis.group.eye

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.{GoBoard, GoProfiler}
import com.barrybecker4.game.twoplayer.go.board.analysis.WorthCalculator
import com.barrybecker4.game.twoplayer.go.board.analysis.group.{AbsoluteHealthCalculator, GroupAnalyzerMap}
import com.barrybecker4.game.twoplayer.go.board.analysis.territory.TerritoryUpdater
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoStone}
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import com.barrybecker4.game.twoplayer.go.options.GoWeights
import org.junit.Assert.{assertEquals, assertFalse, assertTrue}
import org.junit.{After, Test}
import scala.jdk.CollectionConverters._

/**
 * Regression tests for issue #17 — GroupEyeCache invalidation and reuse.
 */
class TestGroupEyeCache extends GoTestCase:

  override protected def getBoardSize: Int = 5

  /**
   * A group with zero eyes must still become invalid after invalidate().
   * Previously clearEyes() returned early when eyes_ was empty and left isValid_=true.
   */
  @Test
  def testInvalidateClearsValidWhenZeroEyes(): Unit =
    restore("board/analysis/group/eye/eyespace/single_isolated_stone")
    val board = getBoard
    val group = getBiggestGroup(true)
    val cache = new GroupEyeCache(group, new GroupAnalyzerMap())

    cache.updateEyes(board)
    assertTrue("Cache should be valid after updateEyes", cache.isValid)
    assertEquals("Isolated stone should have no eyes", 0, cache.getEyes(board).size)

    cache.invalidate()
    assertFalse("Cache must be invalid after invalidate even with zero eyes", cache.isValid)

  /**
   * Liberty count change without groupChanged() must not return stale absolute health.
   */
  @Test
  def testLibertyChangeInvalidatesAbsoluteHealthCache(): Unit =
    val board = new GoBoard(5, 0)
    val blackMove = new GoMove(new ByteLocation(3, 3), 0, new GoStone(true))
    board.makeMove(blackMove)

    val group = board.getPosition(3, 3).asInstanceOf[GoBoardPosition].getGroup
    val calculator = new AbsoluteHealthCalculator(group, new GroupAnalyzerMap())
    val healthBefore = calculator.calculateAbsoluteHealth(board)
    assertTrue(calculator.isValid)
    val libsBefore = group.getNumLiberties(board)
    // Single stone: 4 and 3 liberties both score 0.1; drop to 2 so health must change.
    assertEquals("Center stone should start with 4 liberties", 4, libsBefore)

    for (liberty <- group.getLiberties(board).asScala.take(2)) {
      liberty.setPiece(new GoStone(false))
      board.adjustLiberties(liberty)
    }

    val libsAfter = group.getNumLiberties(board)
    assertEquals("Should have 2 liberties after filling two", 2, libsAfter)
    assertTrue("Eye cache still marked valid until health is recalculated", calculator.isValid)

    val healthAfter = calculator.calculateAbsoluteHealth(board)
    assertTrue("Cache should be valid again after recompute", calculator.isValid)
    assertTrue(
      s"Black health should drop when liberties drop (before=$healthBefore after=$healthAfter)",
      healthAfter < healthBefore
    )
    // Recompute again with unchanged liberties — must hit the fast path and stay consistent.
    assertEquals(healthAfter, calculator.calculateAbsoluteHealth(board), 0.0f)

  @After
  override def tearDown(): Unit =
    TerritoryUpdater.FORCE_NEW_ANALYZERS = false
    val profiler = GoProfiler.getInstance
    profiler.resetAll()
    profiler.setEnabled(false)
    super.tearDown()

  /**
   * Same board evaluated twice must produce the same worth and reuse the eye cache.
   * On the second worth(), UPDATE_EYES should not run (profiler time stays 0).
   */
  @Test
  def testDoubleWorthSameBoardReusesEyeCache(): Unit =
    restore("board/worth5x5")
    val board = getBoard
    val analyzerMap = new GroupAnalyzerMap()
    val calculator = new WorthCalculator(board, analyzerMap)
    val weights = new GoWeights().getDefaultWeights
    val move = controller.getLastMove
    val profiler = GoProfiler.getInstance
    profiler.setEnabled(true)

    val worth1 = calculator.worth(move, weights)
    val group = getBiggestGroup(true)
    val analyzer = analyzerMap.getAnalyzer(group)
    assertTrue("Analyzer should be valid after first worth()", analyzer.isValid)

    profiler.resetAll()
    val worth2 = calculator.worth(move, weights)
    assertEquals("Repeated worth on same board must be identical", worth1, worth2)
    assertTrue("Eye cache should still be valid after second worth()", analyzer.isValid)
    assertEquals(
      "Second worth() should skip eye updates when cache is valid",
      0L,
      profiler.getEntry("update eyes").getTime
    )

  /** Sanity check: FORCE_NEW_ANALYZERS discards analyzers between worth() calls. */
  @Test
  def testForceNewAnalyzersDiscardsAnalyzers(): Unit =
    restore("board/worth5x5")
    val board = getBoard
    val analyzerMap = new GroupAnalyzerMap()
    val calculator = new WorthCalculator(board, analyzerMap)
    val weights = new GoWeights().getDefaultWeights
    val move = controller.getLastMove

    TerritoryUpdater.FORCE_NEW_ANALYZERS = true
    val worth1 = calculator.worth(move, weights)
    val group = getBiggestGroup(true)
    val analyzerAfterFirst = analyzerMap.getAnalyzer(group)

    val worth2 = calculator.worth(move, weights)
    val analyzerAfterSecond = analyzerMap.getAnalyzer(group)

    assertEquals(worth1, worth2)
    assertFalse(
      "clear() must discard prior analyzers so a new instance is created",
      analyzerAfterFirst eq analyzerAfterSecond
    )
