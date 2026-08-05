/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.common.ui

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.GameController
import com.barrybecker4.game.common.ui.dialogs.GameOptionsDialog
import com.barrybecker4.game.multiplayer.common.MultiGameOptions
import com.barrybecker4.ui.components.NumberInput

import java.awt.Component
import java.awt.event.{KeyEvent, KeyListener}

/**
 * @author Barry Becker
 */
abstract class MultiGameOptionsDialog(parent: Component, controller: GameController[?, ?])
  extends GameOptionsDialog(parent, controller) with KeyListener {

  var maxNumPlayers: NumberInput = null
  var numRobotPlayers: NumberInput = null

  private val ABS_MAX_NUM_PLAYERS = 30

  def initMultiControllerParamComponents(options: MultiGameOptions): Unit = {
    maxNumPlayers =
      new NumberInput(
        GameContext.getLabel("MAX_NUM_PLAYERS"), options.getMaxNumPlayers,
        GameContext.getLabel("MAX_NUM_PLAYERS_TIP"), options.getMinNumPlayers,
        ABS_MAX_NUM_PLAYERS, true)
    maxNumPlayers.addKeyListener(this)

    numRobotPlayers =
      new NumberInput(
        GameContext.getLabel("NUM_ROBOTS"), options.getNumRobotPlayers,
        GameContext.getLabel("NUM_ROBOTS_TIP"), 0, ABS_MAX_NUM_PLAYERS, true)
  }

  override def keyTyped(e: KeyEvent): Unit = {
    if (maxNumPlayers.getIntValue > 0) {
      numRobotPlayers.maxAllowed = maxNumPlayers.getValue
    }
  }

  override def keyPressed(e: KeyEvent): Unit = {}
  override def keyReleased(e: KeyEvent): Unit = {}
}
