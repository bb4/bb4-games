/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.multiplayer.galactic.player.GalacticPlayer

import java.awt.Color
import java.awt.geom.Point2D
import scala.compiletime.uninitialized

class Planet(name: Char, initialNumShips: Int, productionCapacity: Int, pos: Location)
  extends GamePiece(ownedByPlayer1 = false, pieceType = name):

  private var owner_ : GalacticPlayer = uninitialized
  private var numShips_ : Int = initialNumShips
  private var productionCapacity_ : Int = productionCapacity
  private var location_ : Location = pos
  private var underAttack_ = false
  private var highlighted_ = false

  assert(pos != null)
  setAnnotation("" + name)

  def getName: Char = pieceType

  def getOwner: GalacticPlayer = owner_
  def setOwner(owner: GalacticPlayer): Unit = owner_ = owner

  def getNumShips: Int = numShips_
  def setNumShips(numShips: Int): Unit = numShips_ = numShips

  def setUnderAttack(underAttack: Boolean): Unit = underAttack_ = underAttack
  def isUnderAttack: Boolean = underAttack_

  def isHighlighted: Boolean = highlighted_
  def setHighlighted(highlighted: Boolean): Unit = highlighted_ = highlighted

  def deductShips(numShips: Int): Unit =
    assert(numShips_ >= numShips, s"Trying to send $numShips when you have only $numShips_")
    GameContext.log(3, s"subracting $numShips from $numShips_")
    setNumShips(numShips_ - numShips)

  def getProductionCapacity: Int = productionCapacity_
  def setProductionCapacity(productionCapacity: Int): Unit = productionCapacity_ = productionCapacity

  def getColor: Color =
    if getOwner != null then getOwner.getColor
    else Planet.NEUTRAL_COLOR

  def incrementYear(): Unit =
    if getOwner != null then numShips_ += productionCapacity_

  def getLocation: Location = location_
  def setLocation(loc: Location): Unit = location_ = loc

  override def getDescription: String =
    s"${this.toString}Planet: $pieceType production:$getProductionCapacity"

  def getDistanceFrom(p: Planet): Double = getDistanceFrom(p.getLocation)

  private def getDistanceFrom(loc: Location): Double = location_.getDistanceFrom(loc)

  def getDistanceFrom(loc: Point2D): Double = location_.getDistanceFrom(loc)

  def getRadius: Double = 0.85 * Math.pow(getProductionCapacity, 0.333) / 2.1

  override def toString: String = toString("\n")

  def toHtml: String = toString("<br>")

  private[galactic] def toString(newLine: String): String =
    val sb = new StringBuilder(s"Planet: $pieceType$newLine")
    if getOwner != null then sb.append(s"Owner: ${getOwner.getName}$newLine")
    sb.append(s"production:$getProductionCapacity$newLine")
    sb.append(s"Num ships: $getNumShips")
    sb.toString

object Planet:
  val NEUTRAL_COLOR: Color = Color.LIGHT_GRAY
