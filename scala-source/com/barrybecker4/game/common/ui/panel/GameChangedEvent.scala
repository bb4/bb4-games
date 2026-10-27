/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui.panel

import com.barrybecker4.game.common.IGameController
import com.barrybecker4.game.common.Move
import java.util.EventObject

/**
 * This event gets fired whenever the Game state changes.
 *
 * @see GameChangedListener
 * @author Barry Becker
 */
final class GameChangedEvent[M <: Move](mv: M, controller: IGameController[?, ?], source: AnyRef) 
  extends EventObject(source) {

  private val move_ = mv
  private val controller_ = controller

  /**
   * @return the game controller for the viewer that sent the event.
   */
  def getController: IGameController[?, ?] = controller_

  /**
   * @return the move that just caused the game changed event to fire.
   */
  def getMove: M = move_
}
