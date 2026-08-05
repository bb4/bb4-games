/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.ui

import com.barrybecker4.game.common.{GameContext, GameController}
import com.barrybecker4.game.common.player.Player
import com.barrybecker4.game.common.ui.panel._
import com.barrybecker4.game.twoplayer.common.ui.{TwoPlayerGeneralInfoPanel, TwoPlayerInfoPanel}
import com.barrybecker4.game.twoplayer.go.GoController
import com.barrybecker4.game.twoplayer.go.board.{BoardValidator, GoSearchable}
import com.barrybecker4.game.twoplayer.go.ui.rendering.GoBoardRenderer
import com.barrybecker4.ui.legend.ContinuousColorLegend
import javax.swing.{BoxLayout, JLabel, JPanel}

/**
 *  Show information and statistics specific to the current game of go.
 *
 *  @author Barry Becker
 */
final class GoInfoPanel(controller: GameController[?, ?]) 
  extends TwoPlayerInfoPanel(controller) {

  private var p1CapturesLabel_ : JLabel = scala.compiletime.uninitialized
  private var p2CapturesLabel_ : JLabel = scala.compiletime.uninitialized
  private var p1TerritoryLabel_ : JLabel = scala.compiletime.uninitialized
  private var p2TerritoryLabel_ : JLabel = scala.compiletime.uninitialized

  private var legendPanel_ : JPanel = scala.compiletime.uninitialized

  override def getTitleText: String = GameContext.getLabel("GO_INFO")

  override def createSubPanels(): Unit = {
    super.createSubPanels()

    legendPanel_ = createLegendPanel()
    this.add(legendPanel_)
    legendPanel_.setVisible(GameContext.getDebugMode > 0)
  }

  override def createGeneralInfoPanel(player: Player): GeneralInfoPanel = {
    new TwoPlayerGeneralInfoPanel(player)
  }

  /**
   * This panel shows information that is specific to go - specifically
   * captures and territory estimates
   */
  override def createCustomInfoPanel(): JPanel = {
    val customPanel = createPanel()
    customPanel.setLayout(new BoxLayout(customPanel, BoxLayout.Y_AXIS))

    p1CapturesLabel_ = new InfoLabel()
    p2CapturesLabel_ = new InfoLabel()

    p1TerritoryLabel_ = new InfoLabel()
    p2TerritoryLabel_ = new InfoLabel()

    val capturesPanel = new SectionPanel(GameContext.getLabel("NUMBER_OF_CAPTURES"))
    val players = getController().getPlayers
    val p1 = new InfoLabel(players.getPlayer1.getName)
    val p2 = new InfoLabel(players.getPlayer2.getName)
    capturesPanel.add(new RowEntryPanel(p1, p1CapturesLabel_))
    capturesPanel.add(new RowEntryPanel(p2, p2CapturesLabel_))

    val territoryPanel = new SectionPanel(GameContext.getLabel("EST_TERRITORY"))
    val blackTerr = new InfoLabel(GameContext.getLabel("EST_BLACK_TERR"))
    val whiteTerr = new InfoLabel(GameContext.getLabel("EST_WHITE_TERR"))
    territoryPanel.add(new RowEntryPanel(blackTerr, p1TerritoryLabel_))
    territoryPanel.add(new RowEntryPanel(whiteTerr, p2TerritoryLabel_))

    customPanel.add(capturesPanel)
    customPanel.add(territoryPanel)

    customPanel
  }

  private def createLegendPanel(): JPanel = {
    val legendPanel = new SectionPanel("Group Health Legend")
    val legend = new ContinuousColorLegend(null, GoBoardRenderer.COLORMAP, false)
    legendPanel.add(legend)
    legendPanel
  }

  /**
   * update the info with controller stats when the game changes.
   */
  override def gameChanged(gce: GameChangedEvent[com.barrybecker4.game.common.Move]): Unit = {
    super.gameChanged(gce)
    val goController = controller_.asInstanceOf[GoController]

    if (p1CapturesLabel_ == null) return

    val searchable = goController.getSearchable().copy().asInstanceOf[GoSearchable]
    p1CapturesLabel_.setText(s"${searchable.getNumCaptures(false)} ")
    p2CapturesLabel_.setText(s"${searchable.getNumCaptures(true)} ")

    new BoardValidator(searchable.getBoard).confirmStonesInValidGroups()
    p1TerritoryLabel_.setText(s"${searchable.getTerritoryEstimate(true)} ")
    p2TerritoryLabel_.setText(s"${searchable.getTerritoryEstimate(false)} ")

    legendPanel_.setVisible(GameContext.getDebugMode > 0)
  }
}
