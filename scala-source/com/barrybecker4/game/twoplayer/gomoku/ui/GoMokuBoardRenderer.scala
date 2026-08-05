/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku.ui

import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer
import com.barrybecker4.game.twoplayer.common.ui.{TwoPlayerBoardRenderer, TwoPlayerPieceRenderer}

/**
 * Singleton class that takes a game board and renders it for the GameBoardViewer.
 * Having the board renderer separate from the viewer helps to separate out the rendering logic
 * from other features of the GameBoardViewer.
 *
 * @author Barry Becker
 */
class GoMokuBoardRenderer extends TwoPlayerBoardRenderer {
  pieceRenderer_ = TwoPlayerPieceRenderer.getRenderer()
}

object GoMokuBoardRenderer {
  private var renderer_ : GameBoardRenderer = _

  def getRenderer: GameBoardRenderer = {
    if (renderer_ == null)
      renderer_ = new GoMokuBoardRenderer()
    renderer_
  }
}
