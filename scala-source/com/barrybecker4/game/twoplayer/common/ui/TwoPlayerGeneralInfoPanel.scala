// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.common.ui

import com.barrybecker4.common.format.FormatUtil
import com.barrybecker4.game.common.{GameContext, GameController}
import com.barrybecker4.game.common.player.Player
import com.barrybecker4.game.common.ui.panel.{GeneralInfoPanel, InfoLabel, RowEntryPanel, SectionPanel}
import com.barrybecker4.game.twoplayer.common.WinProbabilityCalculator

import javax.swing.{Box, JLabel, SwingConstants}
import java.text.MessageFormat

/**
 *  Show information and statistics about the game.
 *
 *  @author Barry Becker
 */
class TwoPlayerGeneralInfoPanel(player: Player) extends GeneralInfoPanel:

  private var chanceOfWinningLabel_ : JLabel = null

  SectionPanel.styleSectionPanel(this, GameContext.getLabel("GENERAL_INFO"))

  val turnLabel = new InfoLabel(GameContext.getLabel("PLAYER_TO_MOVE"))
  initPlayerLabel(player)

  val moveNumTextLabel = new InfoLabel(GameContext.getLabel("CURRENT_MOVE_NUM"))
  moveNumTextLabel.setHorizontalAlignment(SwingConstants.LEFT)
  moveNumLabel = new JLabel("1")

  val args = Array[Object](player.getName)
  val m = MessageFormat.format(GameContext.getLabel("CHANCE_OF_WINNING"), args*)
  val chanceOfWinningTextLabel = new InfoLabel(m)
  chanceOfWinningTextLabel.setHorizontalAlignment(SwingConstants.LEFT)
  chanceOfWinningLabel_ = new InfoLabel("   ")

  add(new RowEntryPanel(turnLabel, playerLabel))
  add(new RowEntryPanel(moveNumTextLabel, moveNumLabel))
  add(new RowEntryPanel(chanceOfWinningTextLabel, chanceOfWinningLabel_))
  add(Box.createGlue())

  override def setPlayerLabel(player: Player): Unit =
    playerLabel.setText(s" ${player.getName} ")
    playerLabel.setBorder(getPlayerLabelBorder(player.getColor))
    repaint()

  override def update(controller: GameController[?, ?]): Unit =
    if controller.getLastMove != null then
      setPlayerLabel(controller.getCurrentPlayer)
      moveNumLabel.setText(s"${controller.getNumMoves} ")
      val calc = new WinProbabilityCalculator()
      val formattedPropability =
        FormatUtil.formatNumber(calc.getChanceOfPlayer1Winning(controller.getMoveList.copy()))
      chanceOfWinningLabel_.setText(formattedPropability + ' ')
