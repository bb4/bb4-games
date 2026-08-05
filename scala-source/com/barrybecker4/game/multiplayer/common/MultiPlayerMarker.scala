/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.common

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.board.GamePiece
import java.awt.Color

/**
 * Represents a trivial player in the viewer.
 * For the player we draw their value.
 *
 * @author Barry Becker
 */
class MultiPlayerMarker(private val owner_ : MultiGamePlayer) extends GamePiece {

  private var location_ : Location = _
  private var highlighted_ : Boolean = false

  setAnnotation(s"${owner_.getName}")

  def getOwner: MultiGamePlayer = owner_

  def isHighlighted: Boolean = highlighted_

  def setHighlighted(highlighted: Boolean): Unit = {
    highlighted_ = highlighted
  }

  def getColor: Color = getOwner.getColor

  def getLocation: Location = location_

  def setLocation(loc: Location): Unit = {
    location_ = loc
  }

  override def toString: String = toString("\n")

  def toHtml: String = toString("<br>")

  private def toString(newLine: String): String = {
    val sb = new StringBuilder()
    if (getOwner != null)
      sb.append("Owner: ").append(this.getOwner.getName).append(newLine)
    sb.toString()
  }
}
