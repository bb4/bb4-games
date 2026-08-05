/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information

import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.eye.IGoEye

/**
 * MetaData for the different possible Eye shapes of size 4.
 *
 * @author Barry Becker
 */
class E4Information(subTypeDesc: String) extends AbstractEyeSubtypeInformation {

  import E4Information.Eye4Type
  private val e4Type: Eye4Type = Eye4Type.valueOf(subTypeDesc)

  e4Type match {
    case Eye4Type.E1122 => initialize(false, 4, Array(2.03f, 2.03f))
    case Eye4Type.E1113 => initialize(false, 4, Array(3.03f))
    case Eye4Type.E2222 => initialize(false, 4)
  }

  /**
   * @return eye status for E4 types.
   */
  override def determineStatus(eye: IGoEye, board: GoBoard): EyeStatus = {
    val nbrMap = new EyeNeighborMap(eye)
    e4Type match {
      case Eye4Type.E1122 => handleVitalPointCases(nbrMap, eye, 2)
      case Eye4Type.E1113 => handleVitalPointCases(nbrMap, eye, 1)
      case Eye4Type.E2222 => EyeStatus.UNSETTLED
    }
  }

  override def getTypeName: String = e4Type.toString
}

object E4Information:
  /** Different sorts of eye with 4 spaces. */
  enum Eye4Type extends java.lang.Enum[Eye4Type]:
    case E1122, E1113, E2222
