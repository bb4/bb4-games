/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.tictactoe.ui;

import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer;
import com.barrybecker4.game.twoplayer.gomoku.ui.GoMokuBoardViewer;
import com.barrybecker4.game.twoplayer.tictactoe.TicTacToeController;

/**
 *  Takes a TicTacToeController as input and displays the
 *  current state of the TicTactToe Game.
 *
 *  @author Barry Becker
 */
public class TicTacToeBoardViewer extends GoMokuBoardViewer {

    /**
      *  Constructor
      */
    public TicTacToeBoardViewer() {
    }

    @Override
    protected TicTacToeController createController() {
        return new TicTacToeController();
    }

    @Override
    protected GameBoardRenderer getBoardRenderer() {
        return TicTacToeBoardRenderer.getRenderer();
    }
}
