/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.tictactoe.ui

import com.barrybecker4.game.common.GameController
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.gomoku.GoMokuBoard
import com.barrybecker4.game.twoplayer.gomoku.ui.GoMokuBoardViewer
import com.barrybecker4.game.twoplayer.tictactoe.TicTacToeController

/**
 * Takes a TicTacToeController as input and displays the current state of the TicTacToe Game.
 *
 * @author Barry Becker
 */
class TicTacToeBoardViewer extends GoMokuBoardViewer:

  override def createController(): GameController[TwoPlayerMove, GoMokuBoard] =
    new TicTacToeController().asInstanceOf[GameController[TwoPlayerMove, GoMokuBoard]]

  override def getBoardRenderer: GameBoardRenderer =
    TicTacToeBoardRenderer.getRenderer
