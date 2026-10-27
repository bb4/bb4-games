// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.set.ui

import com.barrybecker4.game.common.{GameContext, GameController}
import com.barrybecker4.game.common.player.Player
import com.barrybecker4.game.common.ui.panel.{GeneralInfoPanel, InfoLabel, RowEntryPanel, SectionPanel}
import com.barrybecker4.game.multiplayer.set.SetBoard

import javax.swing.{Box, JLabel, SwingConstants}
import scala.compiletime.uninitialized

class SetGeneralInfoPanel(player: Player) extends GeneralInfoPanel(player):

  private var numSetsOnBoardLabel_ : JLabel = uninitialized
  private var numCardsRemainingLabel_ : JLabel = uninitialized

  SectionPanel.styleSectionPanel(this, GameContext.getLabel("GENERAL_INFO"))

  private val numSetsOnBoardText = InfoLabel(GameContext.getLabel("NUMBER_OF_SETS_ON_BOARD"))
  numSetsOnBoardLabel_ = InfoLabel(" ")
  private val numCardsRemainingText = InfoLabel(GameContext.getLabel("NUMBER_OF_CARDS_REMAINING"))
  numCardsRemainingLabel_ = InfoLabel(" ")
  numCardsRemainingLabel_.setHorizontalAlignment(SwingConstants.LEFT)

  add(RowEntryPanel(numSetsOnBoardText, numSetsOnBoardLabel_))
  add(RowEntryPanel(numCardsRemainingText, numCardsRemainingLabel_))
  add(Box.createGlue())

  override def setPlayerLabel(player: Player): Unit =
    assert(false, "Did not expect this to be called")

  override def update(controller: GameController[?, ?]): Unit =
    val b = controller.getBoard.asInstanceOf[SetBoard]
    numSetsOnBoardLabel_.setText(s"${b.getNumSetsOnBoard} ")
    val cardsInDeck = b.getDeck.size() - b.getNumCardsShowing
    numCardsRemainingLabel_.setText(s"$cardsInDeck ")
