/* Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala.move

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.Move
import com.barrybecker4.game.twoplayer.mancala.board.{MancalaBin, MancalaBoard}
import scala.jdk.CollectionConverters.*

/**
  * Responsible for undoing a mancala move on the board.
  *
  * @author Barry Becker
  */
class MoveUndoer(board: MancalaBoard) extends MoveAction(board):

  /**
    * For mancala, undoing a move means picking up all the stones that were
    * placed and restoring them to their original bins.
    * It can be a bit tricky for compound moves - ones it which the player goes again because
    * their last seeded bin was their store. In these cases, the moves must be undone in the reverse order.
    * Furthermore some moves capture stones in other bins, those must also be restored and deducted from the
    * appropriate player storage.  Note: recursive.
    */
  def undoMove(move: Move): Unit =
    val m = move.asInstanceOf[MancalaMove]
    if m.getFollowUpMove != null then
      undoMove(m.getFollowUpMove)
    restoreCaptures(m)
    unseedStones(m)

  /**
    * Captures are restored from the appropriate player's storage.
    * @param move the move to restore captures for.
    */
  private def restoreCaptures(move: MancalaMove): Unit =
    val captures = move.getCaptures
    if captures.isEmpty then return

    var oppositeBinLocation: Location = null
    val playerHome = board.getHomeBin(move.isPlayer1)
    val opponentHome = board.getHomeBin(!move.isPlayer1)
    val keys = move.getCaptures.keySet()

    // first restore the player's captures
    for loc <- keys.asScala do
      val bin = board.getBin(loc)
      if bin.isOwnedByPlayer1 == move.isPlayer1 then
        bin.increment()
        if playerHome.getNumStones == 0 then
          throw IllegalStateException(s"error undoing move=${move.toString}")
        playerHome.increment(-1)
        oppositeBinLocation = board.getOppositeLocation(loc)

    // next restore the opponent's captures.
    // Restoring opponent captures on a whole side can only happen on the last move of the round/game.
    for loc <- keys.asScala do
      val bin = board.getBin(loc)
      if bin.isOwnedByPlayer1 != move.isPlayer1 then
        val numCaptures = move.getCaptures.get(loc)
        if loc.equals(oppositeBinLocation) then
          board.getBin(oppositeBinLocation).increment(numCaptures)
          playerHome.increment(-numCaptures)
        else
          bin.increment(numCaptures)
          assert(numCaptures < opponentHome.getNumStones,
            s"We cannot restore $numCaptures to $loc from the opponent home because it has only ${opponentHome.getNumStones}")
          opponentHome.increment(-numCaptures)

  /**
    * March around and pickup the seeds placed by the move
    * @param move the move for which the seeing will be undone.
    */
  private def unseedStones(move: MancalaMove): Unit =
    var numStones = move.getNumStonesSeeded
    var currentLocation = move.getFromLocation
    val startBin = board.getBin(currentLocation)
    startBin.increment(numStones)
    var stonesToPickup = numStones

    while stonesToPickup > 0 do
      currentLocation = board.getNextLocation(currentLocation)
      val nextBin = board.getBin(currentLocation)
      if !(nextBin.isHome && move.isPlayer1 != nextBin.isOwnedByPlayer1) then
        assert(nextBin.getNumStones > 0, 
          s"Cannot undo move $move. Cannot unseed $currentLocation because it is already at 0.")
        nextBin.increment(-1)
        stonesToPickup = (stonesToPickup - 1).toByte
