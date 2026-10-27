/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.checkers

import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.common.player.PlayerList
import com.barrybecker4.game.twoplayer.common.TwoPlayerSearchable
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy
import com.barrybecker4.optimization.parameter.ParameterArray

class CheckersSearchable(board: CheckersBoard, players: PlayerList) 
    extends TwoPlayerSearchable[CheckersMove, CheckersBoard](board, players) {

  def this(searchable: CheckersSearchable) = {
    this(searchable.board_, searchable.players)
  }

  override def copy(): CheckersSearchable = new CheckersSearchable(this)

  override def generateMoves(lastMove: CheckersMove, weights: ParameterArray): MoveList[CheckersMove] = {
    val generator = new MoveGenerator(this, weights)
    val moveList = generator.generateMoves(lastMove)
    bestMoveFinder_.getBestMoves(moveList)
  }

  override def done(move: CheckersMove, recordWin: Boolean): Boolean = {
    if (move == null) {
      if (recordWin) {
        val lastMove = getMoveList.getLastMove
        recordPlayerWin(lastMove.isPlayer1)
      }
      return true
    }

    var won = math.abs(move.getValue) >= SearchStrategy.WINNING_VALUE

    if (won && recordWin) {
      recordPlayerWin(move.isPlayer1)
    }

    if (getNumMoves() >= getBoard.getMaxNumMoves) {
      won = true
      if (recordWin) {
        recordPlayerWin(math.abs(move.getValue) >= 0)
      }
    }
    won
  }

  private def recordPlayerWin(p1Won: Boolean): Unit = {
    if (p1Won) {
      players.getPlayer1.setWon(true)
    } else {
      players.getPlayer2.setWon(true)
    }
  }

  override def worth(lastMove: CheckersMove, weights: ParameterArray): Int = {
    new BoardEvaluator(board_, weights).calculateWorth
  }

  override def generateUrgentMoves(lastMove: CheckersMove, weights: ParameterArray): MoveList[CheckersMove] = {
    new MoveList[CheckersMove]()
  }
}
