/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.ui.dialog

import com.barrybecker4.game.common.{GameContext, GameController, GameOptions}
import com.barrybecker4.game.multiplayer.common.ui.MultiGameOptionsDialog
import com.barrybecker4.game.multiplayer.poker.PokerOptions
import com.barrybecker4.ui.components.NumberInput
import javax.swing.{JComponent, JPanel}
import java.awt.Component
import java.awt.event.{ActionListener, ItemListener}

class PokerOptionsDialog(parent: Component, controller: GameController[_, _]) 
    extends MultiGameOptionsDialog(parent, controller) with ActionListener with ItemListener {

  private var ante_ : NumberInput = _
  private var initialChips_ : NumberInput = _
  private var maxAbsoluteRaise_ : NumberInput = _

  override def getControllerParamComponents: Array[JComponent] = {
    val options = controller_.getOptions.asInstanceOf[PokerOptions]
    ante_ = NumberInput(GameContext.getLabel("ANTE"), options.getAnte,
                       GameContext.getLabel("ANTE_TIP"), 1, 100 * PokerOptions.DEFAULT_ANTE, true)
    initialChips_ = NumberInput(GameContext.getLabel("INITIAL_CASH"), options.getInitialCash,
                               GameContext.getLabel("INITIAL_CASH_TIP"), 1, 1000 * PokerOptions.DEFAULT_INITIAL_CASH, true)
    maxAbsoluteRaise_ = NumberInput(GameContext.getLabel("MAX_RAISE"), options.getMaxAbsoluteRaise,
                                   GameContext.getLabel("MAX_RAISE_TIP"), 1, 100 * PokerOptions.DEFAULT_MAX_ABS_RAISE, true)

    initMultiControllerParamComponents(options)

    val spacer = JPanel()

    Array[JComponent](ante_, initialChips_, maxAbsoluteRaise_, maxNumPlayers, numRobotPlayers, spacer)
  }

  override def getOptions: GameOptions = {
    PokerOptions(maxNumPlayers.getIntValue, numRobotPlayers.getIntValue,
                ante_.getIntValue, maxAbsoluteRaise_.getIntValue, initialChips_.getIntValue)
  }
}
