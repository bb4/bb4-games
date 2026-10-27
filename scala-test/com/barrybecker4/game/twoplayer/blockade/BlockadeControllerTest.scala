/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove
import com.barrybecker4.game.twoplayer.blockade.board.move.wall.BlockadeWall
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy
import org.junit.Assert.assertEquals
import org.junit.Test

/** @author Barry Becker */
class BlockadeControllerTest extends BlockadeTestCase:

  /** Verify that the calculated worth for various moves is within reasonable ranges. */
  @Test
  def testWorthOfWinningMove(): Unit =
    restore("whitebox/endGame")

    val winningMove = new BlockadeMove(
      new ByteLocation(5, 8), new ByteLocation(4, 8), 0, new GamePiece(true),
      new BlockadeWall(new BlockadeBoardPosition(12, 5), new BlockadeBoardPosition(12, 4))
    )

    controller_.makeMove(winningMove)

    val weights = controller_.getComputerWeights().getDefaultWeights
    val winFromP1Persp = controller_.getSearchable().worth(winningMove, weights)

    assertEquals(
      "Unexpected value of winning move from P1 perspective",
      SearchStrategy.WINNING_VALUE,
      winFromP1Persp
    )
