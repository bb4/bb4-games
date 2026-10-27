/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set.ui

import com.barrybecker4.game.common.{GameContext, GameController, GameOptions}
import com.barrybecker4.game.multiplayer.common.ui.MultiGameOptionsDialog
import com.barrybecker4.game.multiplayer.set.SetOptions
import com.barrybecker4.ui.components.NumberInput

import java.awt.Component
import java.awt.event.{ActionListener, ItemListener}
import javax.swing.JComponent
import scala.compiletime.uninitialized

class SetOptionsDialog(parent: Component, controller: GameController[?, ?])
  extends MultiGameOptionsDialog(parent, controller) with ActionListener with ItemListener:

  private var initialNumCards_ : NumberInput = uninitialized

  override def getControllerParamComponents: Array[JComponent] =
    val options = controller_.getOptions.asInstanceOf[SetOptions]
    initialNumCards_ = NumberInput(
      GameContext.getLabel("INITIAL_NUM_CARDS"), options.getInitialNumCardsShown,
      GameContext.getLabel("INITIAL_NUM_CARDS_TIP"), 8, 81, true)
    initMultiControllerParamComponents(options)
    Array[JComponent](initialNumCards_, maxNumPlayers, numRobotPlayers)

  override def getOptions: GameOptions =
    SetOptions(maxNumPlayers.getIntValue, numRobotPlayers.getIntValue, initialNumCards_.getIntValue)
