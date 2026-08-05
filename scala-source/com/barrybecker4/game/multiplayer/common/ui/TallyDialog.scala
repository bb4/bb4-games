/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.common.ui

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.player.PlayerList
import com.barrybecker4.game.multiplayer.common.{MultiGameController, MultiGamePlayer}
import com.barrybecker4.ui.components.GradientButton
import com.barrybecker4.ui.dialogs.OptionsDialog

import javax.swing._
import java.awt._
import java.awt.event.ActionEvent
import scala.jdk.CollectionConverters._

/**
 * Show a summary of the final results.
 * We will show how stats about each player.
 * The winner is determined by some metric applied to these stats.
 *
 * @author Barry Becker
 */
abstract class TallyDialog(parent: Component, val controller: MultiGameController[?, ?])
  extends OptionsDialog(parent) {

  private var okButton: GradientButton = null

  showContent()

  override def getTitle: String =
    GameContext.getLabel("TALLY_TITLE")

  override def createDialogContent: JComponent = {
    val mainPanel = new JPanel()
    mainPanel.setLayout(new BorderLayout())

    val players = controller.getPlayers
    val winningPlayers = formatWinnersLabel(findWinners(players))

    // show a label at the top with who the winner(s) is
    val winnersLabel = new JLabel()
    winnersLabel.setText("<html>" + GameContext.getLabel("GAME_OVER") + "<br>"
      + GameContext.getLabel("WINNER_IS") + "<br>" + winningPlayers + "</html>")
    winnersLabel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6))
    mainPanel.add(winnersLabel, BorderLayout.NORTH)

    val summaryTable_ = createSummaryTable(players)
    val tablePanel = new JPanel()
    tablePanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6))
    tablePanel.add(new JScrollPane(summaryTable_.getTable), BorderLayout.CENTER)

    mainPanel.add(tablePanel, BorderLayout.CENTER)
    mainPanel.add(createButtonsPanel, BorderLayout.SOUTH)

    mainPanel
  }

  private def formatWinnersLabel(winners: java.util.List[? <: MultiGamePlayer]): String = {
    val bldr = new StringBuilder()
    winners.asScala.foreach { winner =>
      bldr.append(winner.getName).append(", ")
    }
    bldr.substring(0, bldr.length - 2)
  }

  def createSummaryTable(players: PlayerList): SummaryTable

  def findWinners(players: PlayerList): java.util.List[? <: MultiGamePlayer]

  override def createButtonsPanel: JPanel = {
    val buttonsPanel = new JPanel(new FlowLayout())

    okButton = new GradientButton()
    initBottomButton(okButton, GameContext.getLabel("OK"), GameContext.getLabel("PLACE_ORDER_TIP"))

    buttonsPanel.add(okButton)

    buttonsPanel
  }

  override def actionPerformed(e: ActionEvent): Unit = {
    val source = e.getSource

    if (source eq okButton) {
      this.setVisible(false)
    }
  }
}
