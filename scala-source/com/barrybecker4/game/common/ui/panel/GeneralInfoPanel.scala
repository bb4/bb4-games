// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.ui.panel

import com.barrybecker4.game.common.{GameContext, GameController}
import com.barrybecker4.game.common.player.Player
import com.barrybecker4.ui.util.GUIUtil
import javax.swing._
import javax.swing.border.Border
import java.awt.{Color, Font}
import javax.swing.SwingConstants

/**
 *  Show generic stuff like the current player's name and the number of moves made so far.
 *
 *  @author Barry Becker
 */
abstract class GeneralInfoPanel(player: Player) extends JPanel {

  var moveNumLabel: JLabel = _
  var playerLabel: JLabel = _

  def this() = this(null)

  if (player != null) {
    SectionPanel.styleSectionPanel(this, GameContext.getLabel("GENERAL_INFO"))
    val turnLabel = new InfoLabel(GameContext.getLabel("PLAYER_TO_MOVE"))
    initPlayerLabel(player)

    val moveNumTextLabel = new InfoLabel(GameContext.getLabel("CURRENT_MOVE_NUM"))
    moveNumTextLabel.setHorizontalAlignment(SwingConstants.LEFT)
    moveNumLabel = new JLabel("1")

    add(new RowEntryPanel(turnLabel, playerLabel))
    add(new RowEntryPanel(moveNumTextLabel, moveNumLabel))

    add(Box.createGlue())
  }

  def initPlayerLabel(player: Player): Unit = {
    playerLabel = new JLabel()
    playerLabel.setOpaque(true)
    playerLabel.setFont(GeneralInfoPanel.PLAYER_FONT)
    setPlayerLabel(player)
  }

  def update(controller: GameController[_, _]): Unit = {
    setPlayerLabel(controller.getCurrentPlayer)
    if (controller.getLastMove != null) {
      moveNumLabel.setText(" " + controller.getNumMoves)
    }
  }

  /**
   * set the appropriate text and color for the player label.
   */
  def setPlayerLabel(player: Player): Unit

  def getPlayerLabelBorder(pColor: Color): Border = {
    BorderFactory.createCompoundBorder(
      BorderFactory.createEmptyBorder(0, 4, 0, 0),
      BorderFactory.createEtchedBorder(pColor, pColor.darker()))
  }
}

object GeneralInfoPanel {
  private val PLAYER_FONT = new Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.BOLD, 12)
}
