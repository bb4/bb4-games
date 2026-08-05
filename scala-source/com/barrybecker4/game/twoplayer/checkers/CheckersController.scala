/* Copyright by Barry G. Becker, 2000-2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.checkers

import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.common.player.{PlayerList, PlayerOptions}
import com.barrybecker4.game.twoplayer.common.{TwoPlayerController, TwoPlayerOptions}

import java.awt.Color

class CheckersController extends TwoPlayerController[CheckersMove, CheckersBoard] {

  initializeData()

  override def createBoard: CheckersBoard = new CheckersBoard()

  override def initializeData(): Unit = {
    weights_ = new CheckersWeights()
  }

  override def createOptions(): TwoPlayerOptions = new TwoPlayerOptions()

  override def createPlayerOptions(playerName: String, color: Color): PlayerOptions = {
    new CheckersPlayerOptions(playerName, color)
  }

  override def computerMovesFirst(): Unit = {
    val moveList = getSearchable().generateMoves(null, weights_.getPlayer1Weights)
    assert(!moveList.isEmpty)
    makeMove(moveList.getRandomMove)
    player1sTurn = false
  }

  override def getStrengthOfWin: Int = {
    if (!getPlayers.anyPlayerWon) {
      0
    } else {
      getSearchable().worth(getLastMove, weights_.getDefaultWeights)
    }
  }

  override def createSearchable(board: CheckersBoard, players: PlayerList): CheckersSearchable = {
    new CheckersSearchable(board, players)
  }
}
