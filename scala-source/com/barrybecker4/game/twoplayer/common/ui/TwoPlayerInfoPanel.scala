/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui

import com.barrybecker4.game.common.GameController
import com.barrybecker4.game.common.player.Player
import com.barrybecker4.game.common.ui.panel.{GameChangedEvent, GameChangedListener, GameInfoPanel, GeneralInfoPanel}
import com.barrybecker4.game.twoplayer.common.TwoPlayerController

/**
 *  Show information and statistics about the game.
 *
 *  @author Barry Becker
 */
class TwoPlayerInfoPanel(controller: GameController[?, ?]) extends GameInfoPanel(controller):

  override def createGeneralInfoPanel(player: Player): GeneralInfoPanel =
    new TwoPlayerGeneralInfoPanel(player)

  protected def getController(): TwoPlayerController[?, ?] =
    controller_.asInstanceOf[TwoPlayerController[?, ?]]

  /**
   * implements the GameChangedListener interface.
   * This method called whenever a move has been made.
   */
  override def gameChanged(gce: GameChangedEvent[com.barrybecker4.game.common.Move]): Unit =
    if controller_ == null then
      return
    generalInfoPanel_.update(controller_)
