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
import org.junit.jupiter.api.Assertions.{assertEquals, assertFalse, assertTrue}
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
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
    assertTrue(cache.isValid, "Cache should be valid after updateEyes")
    assertEquals(0, cache.getEyes(board).size, "Isolated stone should have no eyes")

    cache.invalidate()
    assertFalse(cache.isValid, "Cache must be invalid after invalidate even with zero eyes")

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
    assertEquals(4, libsBefore, "Center stone should start with 4 liberties")

    for (liberty <- group.getLiberties(board).asScala.take(2)) {
      liberty.setPiece(new GoStone(false))
      board.adjustLiberties(liberty)
    }

    val libsAfter = group.getNumLiberties(board)
    assertEquals(2, libsAfter, "Should have 2 liberties after filling two")
    assertTrue(calculator.isValid, "Eye cache still marked valid until health is recalculated")

    val healthAfter = calculator.calculateAbsoluteHealth(board)
    assertTrue(calculator.isValid, "Cache should be valid again after recompute")
    assertTrue(healthAfter < healthBefore, s"Black health should drop when liberties drop (before=$healthBefore after=$healthAfter)")
    // Recompute again with unchanged liberties — must hit the fast path and stay consistent.
    assertEquals(healthAfter, calculator.calculateAbsoluteHealth(board), 0.0f)

  @AfterEach
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
    assertTrue(analyzer.isValid, "Analyzer should be valid after first worth()")

    profiler.resetAll()
    val worth2 = calculator.worth(move, weights)
    assertEquals(worth1, worth2, "Repeated worth on same board must be identical")
    assertTrue(analyzer.isValid, "Eye cache should still be valid after second worth()")
    assertEquals(0L, profiler.getEntry("update eyes").getTime, "Second worth() should skip eye updates when cache is valid")

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
    assertFalse(analyzerAfterFirst eq analyzerAfterSecond, "clear() must discard prior analyzers so a new instance is created")
