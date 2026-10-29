// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.go.board.elements.eye

/**
 * Move serialization of eye to separate class for better reuse.
 */
class EyeSerializer(eye: IGoEye) {

  def serialize(): String = {
    val bldr = new StringBuilder("GoEye: ")
    bldr.append(" ownedByPlayer1=").append(eye.isOwnedByPlayer1)
    bldr.append(" status=").append(eye.getStatus)
    bldr.append(" info=").append("[").append(eye.getInformation).append("]")
    bldr.append(" num corner pts=").append(eye.getNumCornerPoints)
    bldr.append(" num edge pts=").append(eye.getNumEdgePoints)
    bldr.append(" UnconditionallyAlive=").append(eye.isUnconditionallyAlive)
    bldr.append(" num members=").append(eye.size)
    bldr.toString
  }
}
