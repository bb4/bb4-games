/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui.panel

import javax.swing._

/**
 * @author Barry Becker
 */
trait IGamePanel {

  /**
   * common initialization in the event that there are multiple constructors.
   */
  def init(parent: JFrame): Unit

  /** Open a new game. */
  def openGame(): Unit

  /** Save the current game. */
  def saveGame(): Unit

  /**
   * @return the title for the applet/application window.
   */
  def getTitle: String
}
