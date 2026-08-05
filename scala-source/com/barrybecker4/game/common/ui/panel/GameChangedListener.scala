/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui.panel

import com.barrybecker4.game.common.Move
import java.util.EventListener

/**
 * This interface must be implemented by any class that wants to receive GameChangedEvents
 *
 * @author Barry Becker
 */
trait GameChangedListener[M <: Move] extends EventListener {
  def gameChanged(evt: GameChangedEvent[M]): Unit
}
