/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information

import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.eye.IGoEye

/**
 * MetaData for the different possible Eye shapes of size 5.
 * There are 4 different subtypes to consider.
 */
class E5Information(subTypeDesc: String) extends AbstractEyeSubtypeInformation {

  import E5Information.Eye5Type
  private val e5Type: Eye5Type = Eye5Type.valueOf(subTypeDesc)

  e5Type match {
    case Eye5Type.E11222 => initialize(true, 5)
    case Eye5Type.E11123 => initialize(false, 5, Array(3.04f, 2.04f), Array(1.02f))
    case Eye5Type.E11114 => initialize(false, 5, Array(4.04f))
    case Eye5Type.E12223 => initialize(false, 5)
  }

  /**
   * @return eye status for E5 types.
   */
  override def determineStatus(eye: IGoEye, board: GoBoard): EyeStatus = {
    val nbrMap = new EyeNeighborMap(eye)
    e5Type match {
      case Eye5Type.E11222 => handleSubtypeWithLifeProperty(eye, board)
      case Eye5Type.E11123 => handleVitalPointCases(nbrMap, eye, 2)
      case Eye5Type.E11114 => handleVitalPointCases(nbrMap, eye, 1)
      case Eye5Type.E12223 => EyeStatus.UNSETTLED
    }
  }

  override def getTypeName: String = e5Type.toString
}

object E5Information:
  /** Different sorts of eye with 5 spaces. */
  enum Eye5Type extends java.lang.Enum[Eye5Type]:
    case E11222, E11123, E11114, E12223
