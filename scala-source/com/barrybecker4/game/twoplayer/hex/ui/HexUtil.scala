package com.barrybecker4.game.twoplayer.hex.ui

/**
 * Constants and static methods for working with hexagons
 */
object HexUtil:
  private val DEG_TO_RAD = Math.PI / 180.0
  val ROOT3: Double = Math.sqrt(3.0)
  val ROOT3D2: Double = ROOT3 / 2.0

  def rad(angleInDegrees: Double): Double =
    angleInDegrees * DEG_TO_RAD
