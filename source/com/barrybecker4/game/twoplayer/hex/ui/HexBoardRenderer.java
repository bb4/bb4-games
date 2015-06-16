/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.hex.ui;

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
class HexBoardRenderer extends TwoPlayerBoardRenderer {

    private static GameBoardRenderer renderer_;

    /**
     * private constructor because this class is a singleton.
     * Use getRenderer instead
     */
    private HexBoardRenderer() {
        pieceRenderer_ = TwoPlayerPieceRenderer.getRenderer();
    }

    public static GameBoardRenderer getRenderer() {
        if (renderer_ == null)
            renderer_ = new HexBoardRenderer();
        return renderer_;
    }

}

