/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set.ui

import com.barrybecker4.game.common.Move
import com.barrybecker4.game.common.player.Player
import com.barrybecker4.game.common.ui.viewer.{GameBoardRenderer, ViewerMouseListener}
import com.barrybecker4.game.multiplayer.common.MultiGameController
import com.barrybecker4.game.multiplayer.common.online.SurrogateMultiPlayer
import com.barrybecker4.game.multiplayer.common.ui.MultiGameViewer
import com.barrybecker4.game.multiplayer.set.{Card, SetBoard, SetController, SetPlayer}
import com.barrybecker4.game.multiplayer.set.ui.render.SetGameRenderer

import java.awt.event.MouseEvent
import java.util.List as JList

class SetGameViewer extends MultiGameViewer[Move, SetBoard]:

  override def startNewGame(): Unit =
    controller.reset()

  override def createViewerMouseListener(): ViewerMouseListener[Move, SetBoard] =
    SetViewerMouseListener(this)

  override def createController(): MultiGameController[Move, SetBoard] =
    SetController()

  override def getBoardRenderer: GameBoardRenderer = SetGameRenderer.getRenderer

  override def getBoard: SetBoard = controller.getBoard.asInstanceOf[SetBoard]

  override def doComputerMove(player: Player): Boolean =
    assert(false, " no computer player for set yet. coming soon!")
    false

  override def doSurrogateMove(player: SurrogateMultiPlayer): Boolean =
    assert(false, " no online play for Set game yet. coming soon!")
    false

  override def getGameOverMessage: String =
    val winner = controller.asInstanceOf[SetController].determineWinners().get(0).asInstanceOf[SetPlayer]
    s"the game is over. The winner is ${winner.getName} with ${winner.getNumSetsFound}sets"

  def getSelectedCards: JList[Card] =
    controller.getBoard.asInstanceOf[SetBoard].getSelectedCards

  override def getToolTipText(e: MouseEvent): String = null
