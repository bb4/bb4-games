/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui.panel

import com.barrybecker4.game.common.{GameContext, GameController, Move}
import com.barrybecker4.game.common.online.ui.ChatPanel
import com.barrybecker4.game.common.player.Player
import com.barrybecker4.ui.components.TexturedPanel
import javax.swing.{BorderFactory, BoxLayout, JPanel}
import javax.swing.border.Border
import java.awt.Dimension

/**
 *  Show information and statistics about the game.
 *
 *  @author Barry Becker
 */
abstract class GameInfoPanel(controller: GameController[_, _]) extends TexturedPanel(null)
  with GameChangedListener[Move] {

  var controller_ : GameController[_, _] = controller
  var generalInfoPanel_ : GeneralInfoPanel = _

  this.setBorder(BorderFactory.createLoweredBevelBorder())
  this.setToolTipText(getTitleText)
  this.setPreferredSize(new Dimension(getMinWidth, GameInfoPanel.MAX_HEIGHT))
  this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))

  createSubPanels()

  if (controller_.isOnlinePlayAvailable) {
    val chat = new ChatPanel(controller_.getServerConnection)
    chat.setPreferredSize(new Dimension(getMinWidth, GameInfoPanel.MAX_HEIGHT))
    add(chat)
  } else {
    val filler = createPanel()
    filler.setPreferredSize(new Dimension(getMinWidth, GameInfoPanel.MAX_HEIGHT))
    add(filler)
  }

  /**
   *  create all the sub panels in the desired order.
   *  Subclasses may override to get a different ordering.
   */
  def createSubPanels(): Unit = {
    val customPanel = createCustomInfoPanel()
    if (customPanel != null) {
      this.add(customPanel)
    }

    generalInfoPanel_ = createGeneralInfoPanel(controller_.getCurrentPlayer)
    add(generalInfoPanel_)
  }

  def getMinWidth: Int = GameInfoPanel.DEFAULT_MIN_WIDTH

  /**
   * restore to new game state.
   */
  def reset(): Unit = {}

  /**
   * @return title to display at the top of the game info window.
   */
  def getTitleText: String = GameContext.getLabel("GAME_INFORMATION")

  /**
   * This panel shows information that is specific to the game type (like captures or territory estimates).
   */
  def createCustomInfoPanel(): JPanel = null

  /**
   * this is general information that is applicable to every 2 player game.
   */
  def createGeneralInfoPanel(player: Player): GeneralInfoPanel

  final def createPanel(): JPanel = {
    val p = new JPanel(true)
    p.setOpaque(false)
    p
  }

  /**
   * implements the GameChangedListener interface.
   * This method called whenever a move has been made.
   */
  override def gameChanged(gce: GameChangedEvent[Move]): Unit = {
    if (controller_ != null) {
      generalInfoPanel_.update(controller_)
    }
  }
}

object GameInfoPanel {
  private val DEFAULT_MIN_WIDTH = 210
  private val MAX_HEIGHT = 1000

  def createMarginBorder(): Border = BorderFactory.createEmptyBorder(3, 3, 3, 3)
}
