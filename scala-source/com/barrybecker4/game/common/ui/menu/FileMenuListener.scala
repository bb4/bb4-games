// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.ui.menu

/**
 * This interface must be implemented by any class that wants to receive GameMenuChangeEvents
 *
 * @author Barry Becker
 */
trait FileMenuListener {

  /** Opens a saved file and displays it. */
  def openFile(): Unit

  /** Saves the current game to a file. */
  def saveFile(): Unit

  /** Saves a picture of the current game in its current state. */
  def saveImage(): Unit
}
