/* Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.move

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.twoplayer.common.cache.ScoreCache
import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.GoSearchable
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone
import com.barrybecker4.game.twoplayer.go.options.{GoOptions, GoWeights}
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Evaluating a candidate must not leave it on the board if worth throws.
 */
class TestSetMoveValueUndo extends GoTestCase:

  override protected def getBoardSize: Int = 9

  @Test
  def generateMovesRestoresBoardWhenWorthThrows(): Unit =
    controller.makeMove(new GoMove(new ByteLocation(3, 3), 0, new GoStone(true)))
    controller.makeMove(new GoMove(new ByteLocation(3, 4), 0, new GoStone(false)))
    val board = getBoard
    val throwing = new GoSearchable(board, controller.getPlayers, new ScoreCache(), new GoOptions()):
      private var worthCalls = 0
      override def worth(lastMove: GoMove, weights: com.barrybecker4.optimization.parameter.ParameterArray): Int =
        worthCalls += 1
        if worthCalls == 1 then throw new RuntimeException("worth failed")
        super.worth(lastMove, weights)

    val before = throwing.getNumMoves()
    val last = controller.getLastMove.get.asInstanceOf[GoMove]
    try
      throwing.generateMoves(last, new GoWeights().getPlayer1Weights)
    catch
      case _: RuntimeException => ()
    assertEquals(before, throwing.getNumMoves(), s"orphaned candidate: ${throwing.getMoveList}")
