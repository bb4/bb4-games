/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic

import com.barrybecker4.game.multiplayer.galactic.player.GalacticPlayer

import java.awt.geom.{Line2D, Point2D}
import scala.compiletime.uninitialized

class Order(origin: Planet, target: Planet, fleetSize: Int, loc: Point2D):
  private var destination: Planet = uninitialized
  private var owner: GalacticPlayer = uninitialized
  private val currentLocation: Point2D = loc
  private var arrived = false

  commonInit(origin, target, fleetSize)

  def this(origin: Planet, target: Planet, fleetSize: Int) =
    this(origin, target, fleetSize,
      new Point2D.Double(origin.getLocation.col, origin.getLocation.row))

  private def commonInit(origin: Planet, target: Planet, fleetSize: Int): Unit =
    assert(origin != null)
    this.destination = target
    this.owner = origin.getOwner
    assert(owner != null)

  def getOrigin: Planet = origin
  def getDestination: Planet = destination
  def getFleetSize: Int = fleetSize

  def getOwner: GalacticPlayer =
    assert(owner != null)
    owner

  private def getDistanceRemaining: Double = destination.getDistanceFrom(getCurrentLocation)

  def getTimeRemaining: Double = getDistanceRemaining / Order.NORMAL_SPEED

  def hasArrived(): Boolean = arrived

  def incrementYear(): Unit =
    val oldLocation = new Point2D.Double(currentLocation.getX, currentLocation.getY)
    val v = getUnitDirection
    v.setLocation(Order.NORMAL_SPEED * v.getX, Order.NORMAL_SPEED * v.getY)
    currentLocation.setLocation(currentLocation.getX + v.getX, currentLocation.getY + v.getY)

    val line = new Line2D.Double(oldLocation, currentLocation)
    val dLoc = destination.getLocation
    if line.intersects(dLoc.col, dLoc.row, Order.INTERSECT_TOLERANCE, Order.INTERSECT_TOLERANCE) then
      currentLocation.setLocation(dLoc.col, dLoc.row)
      arrived = true

  def getTimeNeeded: Int = (getDistanceRemaining / Order.NORMAL_SPEED + 1).toInt

  private def getUnitDirection: Point2D =
    val dLoc = destination.getLocation
    val unitVec = new Point2D.Double(dLoc.col - currentLocation.getX, dLoc.row - currentLocation.getY)
    val dist = unitVec.distance(new Point2D.Double(0, 0))
    unitVec.setLocation(unitVec.getX / dist, unitVec.getY / dist)
    unitVec

  def getCurrentLocation: Point2D = currentLocation

  override def toString: String =
    s"Target: $destination\nFleet size: $fleetSize\nLocation: $currentLocation\nOwner: $owner\n"

object Order:
  private val NORMAL_SPEED = 2
  private val INTERSECT_TOLERANCE = 0.2
