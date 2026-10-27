/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.tictactoe

import com.barrybecker4.game.common.player.{PlayerList, PlayerOptions}
import com.barrybecker4.game.twoplayer.common.TwoPlayerOptions
import com.barrybecker4.game.twoplayer.gomoku.GoMokuController

import java.awt.Color

/**
 * Defines everything the computer needs to know to play TicTacToe.
 *
 * Without taking symmetries into account, the number of possible games is 255,168 (Henry Bottomley, 2001)
 * Accounting for board symmetries, the number of games in these conditions is 26,830 (Schaeffer 2002)
 * See http://en.wikipedia.org/wiki/Tic-tac-toe
 *
 * @author Barry Becker
 */
class TicTacToeController extends GoMokuController[TicTacToeBoard] {

  /**
   * Constructor
   */
  initializeData()

  override def createBoard: TicTacToeBoard = {
    new TicTacToeBoard()
  }

  override def createOptions(): TwoPlayerOptions = {
    new TwoPlayerOptions()
  }

  override def createPlayerOptions(playerName: String, color: Color): PlayerOptions = {
    new TicTacToePlayerOptions(playerName, color)
  }

  /**
   * this gets the game specific patterns and weights
   */
  override def initializeData(): Unit = {
    weights_ = new TicTacToeWeights()
  }

  override def getWinRunLength: Int = {
    TicTacToePatterns.WIN_RUN_LENGTH
  }

  override def getSearchable(): TicTacToeSearchable = {
    super.getSearchable().asInstanceOf[TicTacToeSearchable]
  }

  override def createSearchable(board: TicTacToeBoard, players: PlayerList): TicTacToeSearchable = {
    new TicTacToeSearchable(board, players)
  }
}
