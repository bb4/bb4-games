/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.chess

import com.barrybecker4.game.common.{GameContext, MoveList}
import com.barrybecker4.game.common.player.{PlayerList, PlayerOptions}
import com.barrybecker4.game.twoplayer.common.{TwoPlayerController, TwoPlayerOptions}

import java.awt.Color

class ChessController extends TwoPlayerController[ChessMove, ChessBoard] {

  initializeData()

  override def createBoard: ChessBoard = new ChessBoard()

  override def initializeData(): Unit = {
    weights_ = new ChessWeights()
  }

  override def computerMovesFirst(): Unit = {
    val moveList = getSearchable().generateMoves(null, weights_.getPlayer1Weights)
    makeMove(moveList.getRandomMove)
    player1sTurn = false
  }

  override def createOptions(): TwoPlayerOptions = new TwoPlayerOptions()

  override def createPlayerOptions(playerName: String, color: Color): PlayerOptions = {
    new ChessPlayerOptions(playerName, color)
  }

  def removeSelfCheckingMoves(moveList: java.util.List[?]): Unit = {
    val b = getBoard
    val it = moveList.iterator()
    while (it.hasNext) {
      val move = it.next().asInstanceOf[ChessMove]
      if (b.causesSelfCheck(move)) {
        GameContext.log(2, s"don't allow $move because it puts the king in check.")
        it.remove()
      }
    }
  }

  override def createSearchable(board: ChessBoard, players: PlayerList): ChessSearchable = {
    new ChessSearchable(board, players)
  }
}
