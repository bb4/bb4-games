/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.update

/**
 * Keeps track of the number of dead stones of each color that are on the board.
 * At the very end of the game we visibly mark dead stones dead.
 */
final class DeadStones {

  private var numDeadBlackStonesOnBoard_ : Int = 0
  private var numDeadWhiteStonesOnBoard_ : Int = 0

  def clear(): Unit = {
    numDeadBlackStonesOnBoard_ = 0
    numDeadWhiteStonesOnBoard_ = 0
  }

  /**
   * @param player1 black player if true
   * @return the number of dead stones on the board for the specified player
   */
  def getNumberOnBoard(player1: Boolean): Int = {
    if (player1) numDeadBlackStonesOnBoard_ else numDeadWhiteStonesOnBoard_
  }

  /**
   * Add to the dead stone count for the specified player
   * @param player1 player to add a dead stone for.
   */
  def increment(player1: Boolean): Unit = {
    if (player1) {
      numDeadBlackStonesOnBoard_ += 1
    } else {
      numDeadWhiteStonesOnBoard_ += 1
    }
  }

  override def toString: String = {
    s"Dead black stones: $numDeadBlackStonesOnBoard_\nDead white stones: $numDeadWhiteStonesOnBoard_"
  }
}
