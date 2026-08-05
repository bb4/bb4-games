// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.player

import java.awt.Color
import java.io.Serializable

/**
 * Represents a player in a game (either human or computer).
 *
 * @author Barry Becker
 */
class PlayerOptions(var name_ : String, private var color_ : Color) extends Serializable {

  assert(color_ != null)

  def getName: String = name_

  def setName(name: String): Unit = {
    this.name_ = name
  }

  def getColor: Color = color_

  def setColor(color: Color): Unit = {
    this.color_ = color
  }

  override def toString: String = s"name = $name_* ${additionalInfo()} ]"

  def additionalInfo(): String = ""
}
