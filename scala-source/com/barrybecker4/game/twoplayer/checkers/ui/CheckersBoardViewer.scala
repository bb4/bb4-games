/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.checkers.ui

import com.barrybecker4.game.common.GameController
import com.barrybecker4.game.common.ui.viewer.{GameBoardRenderer, ViewerMouseListener}
import com.barrybecker4.game.twoplayer.checkers.{CheckersBoard, CheckersController, CheckersMove}
import com.barrybecker4.game.twoplayer.common.ui.AbstractTwoPlayerBoardViewer

/**
 * This class takes a CheckersController as input and displays the
 * Current state of the Checkers Game. The CheckersController contains a CheckersBoard
 * which describes this state.
 *
 * @author Barry Becker
 */
class CheckersBoardViewer extends AbstractTwoPlayerBoardViewer[CheckersMove, CheckersBoard] {

  override def createController(): GameController[CheckersMove, CheckersBoard] = 
    new CheckersController()

  override def getBoardRenderer: GameBoardRenderer =
    CheckersBoardRenderer.getRenderer

  override def createViewerMouseListener(): ViewerMouseListener[CheckersMove, CheckersBoard] = 
    new CheckersViewerMouseListener(this)
}
