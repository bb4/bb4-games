/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.hex.ui;

import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer;
import com.barrybecker4.game.twoplayer.gomoku.ui.GoMokuBoardViewer;
import com.barrybecker4.game.twoplayer.tictactoe.TicTacToeController;

/**
 *  Takes a TicTacToeController as input and displays the
 *  current state of the TicTactToe Game.
 *
 *  @author Barry Becker
 */
public class HexBoardViewer extends GoMokuBoardViewer {

    /**
      *  Constructor
      */
    public HexBoardViewer() {
    }

    @Override
    protected TicTacToeController createController() {
        return new TicTacToeController();
    }

    @Override
    protected GameBoardRenderer getBoardRenderer() {
        return HexBoardRenderer.getRenderer();
    }
}
