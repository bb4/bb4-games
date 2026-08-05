/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial.player

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.multiplayer.trivial.{ActionName, TrivialAction, TrivialController}

import java.awt.Color

/**
 * Represents a Robot player.
 *
 * @author Barry Becker
 */
class TrivialRobotPlayer(name: String, color: Color) extends TrivialPlayer(name, color, false):

  GameContext.log(1, "created a robot with name=" + name)

  /**
   * @return a string describing the type of robot.
   */
  def getType: String = "Robot"

  /**
   * Only reveal action with  a certain probability.
   * @param controller game controller
   * @return the action
   */
  def getAction(controller: TrivialController): TrivialAction =
    val opt = if Math.random() > 0.4 then ActionName.REVEAL else ActionName.KEEP_HIDDEN
    new TrivialAction(this.getName, opt)
