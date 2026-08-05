package com.barrybecker4.game.twoplayer.hex.pathsearch

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.twoplayer.hex.HexBoard

/**
 * Hex state consists of an initial board position and a current location.
 * CRITICAL: board compared by identity (eq/ne), matching Java ==.
 */
class HexState(private val board: HexBoard, private val location: Location):

  def getBoard: HexBoard = board

  def getLocation: Location = location

  override def toString: String = location.toString

  override def equals(o: Any): Boolean =
    if (this eq o.asInstanceOf[AnyRef]) then return true
    if o == null || (getClass ne o.getClass) then return false

    val hexState = o.asInstanceOf[HexState]

    if board != null then
      if board ne hexState.board then return false
    else if hexState.board != null then return false

    // Preserve Java: return !(location != null ? !location.equals(...) : hexState.location != null)
    if location != null then location.equals(hexState.location)
    else hexState.location == null

  override def hashCode(): Int =
    var result = if board != null then board.hashCode() else 0
    result = 31 * result + (if location != null then location.hashCode() else 0)
    result
