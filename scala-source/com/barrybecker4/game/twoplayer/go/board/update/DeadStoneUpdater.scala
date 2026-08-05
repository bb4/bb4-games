/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.update

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoStone}

/**
 * Responsible for determining and updating the dead stones on the board.
 * @author Barry Becker
 */
final class DeadStoneUpdater(board_ : GoBoard) {

  /** keeps track of dead stones. */
  private val deadStones_ : DeadStones = new DeadStones()

  def getNumDeadStonesOnBoard(forPlayer1: Boolean): Int = {
    deadStones_.getNumberOnBoard(forPlayer1)
  }

  /**
   * Update the final life and death status of all the stones still on the board.
   * This method must only be called once at the end of the game or stones will get prematurely marked as dead.
   * The first can update the health of groups and perhaps remove obviously dead stones.
   */
  def determineDeadStones(): Unit = {
    deadStones_.clear()

    for (row <- 1 to board_.getNumRows) {
      for (col <- 1 to board_.getNumCols) {
        val space = board_.getPosition(row, col).asInstanceOf[GoBoardPosition]
        if (space.isOccupied) {
          val stone = space.getPiece.asInstanceOf[GoStone]
          val side = if (stone.isOwnedByPlayer1) 1 else -1
          GameContext.log(1, s"life & death: $space health=${stone.getHealth}")
          if (side * stone.getHealth < 0) {
            // then the stone is more dead than alive, so mark it so
            GameContext.log(0, s"setting $space to dead")
            stone.setDead(true)
            deadStones_.increment(space.getPiece.isOwnedByPlayer1)
          }
        }
      }
    }
    GameContext.log(0, deadStones_.toString)
  }
}
