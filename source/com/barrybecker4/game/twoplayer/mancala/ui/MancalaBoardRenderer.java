/** Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala.ui;

import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer;
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerBoardRenderer;
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPieceRenderer;

/**
 * Singleton class that takes a game board and renders it for the GameBoardViewer.
 * Having the board renderer separate from the viewer helps to separate out the rendering logic
 * from other features of the GameBoardViewer.
 *
 * @author Barry Becker
 */
public class MancalaBoardRenderer extends TwoPlayerBoardRenderer {

    private static GameBoardRenderer renderer_;


    /**
     * private constructor because this class is a singleton.
     * Use getRenderer instead
     */
    protected MancalaBoardRenderer(){
        pieceRenderer_ = TwoPlayerPieceRenderer.getRenderer();
    }

    public static GameBoardRenderer getRenderer() {
        if (renderer_ == null)
            renderer_ = new MancalaBoardRenderer();
        return renderer_;
    }

}

