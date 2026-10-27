/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.tictactoe

import com.barrybecker4.game.common.player.PlayerList
import com.barrybecker4.game.twoplayer.gomoku.GoMokuSearchable
import com.barrybecker4.game.twoplayer.gomoku.pattern.Patterns

/**
 * Defines everything the computer needs to know to play TicTacToe.
 *
 * @author Barry Becker
 */
class TicTacToeSearchable(board: TicTacToeBoard, players: PlayerList) 
    extends GoMokuSearchable[TicTacToeBoard](board, players) {

  def this(searchable: TicTacToeSearchable) = {
    this(searchable.board_.asInstanceOf[TicTacToeBoard], searchable.players.clone().asInstanceOf[PlayerList])
  }

  override def createPatterns(): Patterns = {
    new TicTacToePatterns()
  }

  override def copy(): TicTacToeSearchable = {
    new TicTacToeSearchable(this)
  }

  override def getJeopardyWeight: Int = {
    TicTacToeWeights.JEOPARDY_WEIGHT
  }
}
