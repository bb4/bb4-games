/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial.ui.dialog

import com.barrybecker4.game.common.{GameController, GameOptions}
import com.barrybecker4.game.multiplayer.common.ui.MultiGameOptionsDialog
import com.barrybecker4.game.multiplayer.trivial.TrivialOptions

import javax.swing.JComponent
import java.awt.Component
import java.awt.event.{ActionListener, ItemListener}

/**
 * Use this modal dialog to let the user choose from among the
 * different game options.
 *
 * @author Barry Becker
 */
class TrivialOptionsDialog(parent: Component, controller: GameController[?, ?])
  extends MultiGameOptionsDialog(parent, controller)
  with ActionListener with ItemListener:

  /**
   * @return an array of panels to put in the parent controller param panel.
   */
  override def getControllerParamComponents: Array[JComponent] =
    val options = controller_.getOptions.asInstanceOf[TrivialOptions]
    initMultiControllerParamComponents(options)

    val spacer = new javax.swing.JPanel()

    Array[JComponent](maxNumPlayers, numRobotPlayers, spacer)

  override def getOptions: GameOptions =
    new TrivialOptions(maxNumPlayers.getIntValue, numRobotPlayers.getIntValue)
