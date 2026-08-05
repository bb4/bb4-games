/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.tictactoe.ui

import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPieceRenderer
import com.barrybecker4.game.twoplayer.gomoku.ui.GoMokuBoardRenderer

/**
 * Singleton class that takes a game board and renders it for the GameBoardViewer.
 *
 * @author Barry Becker
 */
class TicTacToeBoardRenderer private () extends GoMokuBoardRenderer:
  setPieceRenderer(TwoPlayerPieceRenderer.getRenderer())

object TicTacToeBoardRenderer:
  private var renderer_ : GameBoardRenderer = _

  def getRenderer: GameBoardRenderer =
    if renderer_ == null then
      renderer_ = new TicTacToeBoardRenderer()
    renderer_
