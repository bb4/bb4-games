/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.update

import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.move.GoMove

/**
 * Responsible for updating a go board after making or undoing a move.
 *
 * @author Barry Becker
 */
class BoardUpdater(board: GoBoard, capCounts: CaptureCounts) {

  private var postMoveUpdater_ : PostMoveUpdater = null
  private var postRemoveUpdater_ : PostRemoveUpdater = null
  private var captureCounts_ : CaptureCounts = capCounts

  initialize(board)

  /**
   * @return a defensive copy
   */
  def getCaptureCounts: CaptureCounts = captureCounts_.copy()

  private def initialize(board: GoBoard): Unit = {
    postMoveUpdater_ = new PostMoveUpdater(board, captureCounts_)
    postRemoveUpdater_ = new PostRemoveUpdater(board, captureCounts_)
  }

  /**
   * @param player1StonesCaptured if true then get the black stones captured
   * @return the captured stones of the specified color
   */
  def getNumCaptures(player1StonesCaptured: Boolean): Int = {
    captureCounts_.getNumCaptures(player1StonesCaptured)
  }

  /**
   * Update the board after move has been played.
   * @param move the move that was just made
   */
  def updateAfterMove(move: GoMove): Unit = {
    postMoveUpdater_.update(move)
  }

  /**
   * Update the board after backing out a move.
   * @param move the move that was just undone
   */
  def updateAfterRemove(move: GoMove): Unit = {
    postRemoveUpdater_.update(move)
  }
}
