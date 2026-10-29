/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information

import com.barrybecker4.game.twoplayer.go.board.elements.eye.IGoEye

import java.util
import java.util.Arrays

/**
 * Enum for the different possible Eye shapes.
 * See http://www.ai.univ-paris8.fr/~cazenave/eyeLabelling.pdf
 */
abstract class AbstractEyeInformation extends EyeInformation {
  override def hasLifeProperty: Boolean = false

  override def getVitalPoints: Array[Float] = Array.empty[Float]

  override def getEndPoints: Array[Float] = Array.empty[Float]

  override def isInCorner(eye: IGoEye): Boolean = eye.getNumCornerPoints == 3

  override def isOnEdge(eye: IGoEye): Boolean = eye.getNumEdgePoints >= 3

  override def equals(obj: Any): Boolean = obj match {
    case that: AbstractEyeInformation =>
      (this eq that) ||
        (hasLifeProperty == that.hasLifeProperty &&
          util.Arrays.equals(getEndPoints, that.getEndPoints) &&
          util.Arrays.equals(getVitalPoints, that.getVitalPoints))
    case _ => false
  }

  override def hashCode(): Int = {
    var result = if (hasLifeProperty) 1 else 0
    result = 31 * result + (if (getVitalPoints != null) util.Arrays.hashCode(getVitalPoints) else 0)
    result = 31 * result + (if (getEndPoints != null) util.Arrays.hashCode(getEndPoints) else 0)
    result
  }
}
