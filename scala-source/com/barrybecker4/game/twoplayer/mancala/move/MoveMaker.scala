/* Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala.move

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.Move
import com.barrybecker4.game.twoplayer.mancala.board.{MancalaBin, MancalaBoard}

/**
  * Responsible for making a mancala move on the board.
  *
  * @author Barry Becker
  */
class MoveMaker(board: MancalaBoard) extends MoveAction(board):

  /**
    * Given a move specification, execute it on the board.
    * See rules: http://boardgames.about.com/cs/mancala/ht/play_mancala.htm
    * @param move the move to make, if possible.
    * @return false if the move is illegal.
    */
  def makeMove(move: Move): Boolean =
    var currentMove = move.asInstanceOf[MancalaMove]
    while currentMove != null do
      var currentLocation = currentMove.getFromLocation
      val bin = board.getBin(currentLocation)
      val numStones = currentMove.getNumStonesSeeded
      if bin.isHome || numStones == 0 then
        return false
      bin.takeStones()

      // march counter-clockwise around the board dropping stones into bins (but skipping the opponent home bin)
      var numStonesLeft = numStones
      while numStonesLeft > 0 do
        currentLocation = board.getNextLocation(currentLocation)
        val nextBin = board.getBin(currentLocation)
        if !(nextBin.isHome && currentMove.isPlayer1 != nextBin.isOwnedByPlayer1) then
          nextBin.increment()
          numStonesLeft = (numStonesLeft - 1).toByte
      currentMove.setCaptures(doCaptures(currentMove, currentLocation))
      currentMove = currentMove.getFollowUpMove
    true

  /**
    * @param move the move that made the captures.
    * @param currentLocation location of final seeded bin.
    * @return map from location to number of captures made
    */
  private def doCaptures(move: MancalaMove, currentLocation: Location): Captures =
    // handle some special cases. like capturing
    val captures = Captures()
    val lastBin = board.getBin(currentLocation)
    val oppositeLoc = board.getOppositeLocation(currentLocation)
    var oppositeBin: MancalaBin = null
    if !lastBin.isHome then
      oppositeBin = board.getBin(oppositeLoc)

    // if the last bin seeded
    //  - had no stones,
    //  - and was on the player's side,
    //  - and there is at least one opponent stone opposite,
    // capture that piece and all the stones from the opposite bin and put in your store
    if !lastBin.isHome
        && lastBin.getNumStones == 1
        && lastBin.isOwnedByPlayer1 == move.isPlayer1
        && oppositeBin.getNumStones > 0 then
      val homeBin = board.getHomeBin(move.isPlayer1)
      captures.put(currentLocation, 1.toByte)
      homeBin.increment(lastBin.takeStones())
      captures.put(oppositeLoc, oppositeBin.getNumStones)
      homeBin.increment(oppositeBin.takeStones())

    // if no stones left on players side, opponent captures all remaining stones on his side
    if board.isSideClear(move.isPlayer1) then
      board.clearSide(!move.isPlayer1, captures)
    else if board.isSideClear(!move.isPlayer1) then
      board.clearSide(move.isPlayer1, captures)
    captures
