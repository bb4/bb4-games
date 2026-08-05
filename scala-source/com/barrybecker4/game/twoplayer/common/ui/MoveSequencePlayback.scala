/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui

import com.barrybecker4.game.common.Move
import com.barrybecker4.game.twoplayer.common.{TwoPlayerBoard, TwoPlayerController, TwoPlayerMove}

import java.util.List

/**
 * Helps to play back a sequence of moves on the board.
 *
 * @author Barry Becker
 */
class MoveSequencePlayback[M <: TwoPlayerMove, B <: TwoPlayerBoard[M]](private val controller_ : TwoPlayerController[M, B]):

  /**
   * perform a sequence of moves from somewhere in the game;
   * not necessarily the start. We do, however,
   * assume the moves are valid. It is for display purposes only.
   *
   * @param moveSequence the list of moves to make
   * @param numMovesToBackup number of moves to undo before playing this move sequence
   */
  def makeMoveSequence(moveSequence: List[?], numMovesToBackup: Int): Unit =
    if moveSequence == null || moveSequence.size == 0 then
      return
    val firstMove = moveSequence.get(0).asInstanceOf[Move]
    // the first time we click on a row in the tree, the controller has no moves.
    val lastMove = controller_.getLastMove

    if lastMove == null then
      controller_.reset() // was AbsTwoPlayerBoardViewer.reset()
    else
      backupNMoves(moveSequence, numMovesToBackup, firstMove)

    performMoveSequence(moveSequence)

  /**
   * We keep the original moves and just back up to firstMove.moveNumber.
   * number of steps to backup is # of most recent real moves minus
   * the first move in the sequence.
   */
  private def backupNMoves(moveSequence: List[?], numMovesToBackup: Int, firstMove: Move): Unit =
    var ct = 0
    if firstMove != null then
      while ct < numMovesToBackup do
        controller_.undoLastMove()
        // I suppose this is possible
        if controller_.getLastMove == null then
          throw new IllegalArgumentException("Reached the end after backing up "
            + ct + " out of " + numMovesToBackup + " steps." +
            "\n moveSequence=" + moveSequence)
        ct += 1

  /**
   * Perform the specified sequence of moves.
   * @param moveSequence the sequence of consecutive moves to play on the board.
   */
  private def performMoveSequence(moveSequence: List[?]): Unit = synchronized {
    var firstFuture = 0
    var i = 0
    while i < moveSequence.size do
      val m = moveSequence.get(i).asInstanceOf[TwoPlayerMove]
      if m.isFuture then
        if firstFuture == 0 then
          firstFuture = i
        m.getPiece.setAnnotation(Integer.toString(i - firstFuture + 1))
        m.getPiece.setTransparency(MoveSequencePlayback.FUTURE_MOVE_TRANSP)
      controller_.makeMove(m.asInstanceOf[M])
      i += 1
  }

object MoveSequencePlayback:
  /** Piece transparency for moves that have not been played yet. */
  private val FUTURE_MOVE_TRANSP: Short = 190
