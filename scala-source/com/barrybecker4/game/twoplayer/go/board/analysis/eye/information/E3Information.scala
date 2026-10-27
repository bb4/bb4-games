/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information

import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.eye.IGoEye

/**
 * Three space eye *** - there is only one type.
 * The vital point may or may not be filled - determining its status.
 *
 * @author Barry Becker
 */
class E3Information extends AbstractEyeSubtypeInformation {

  initialize(false, 3, Array(2.02f))

  /**
   * @return eye status for E3 type.
   */
  override def determineStatus(eye: IGoEye, board: GoBoard): EyeStatus = {
    val nbrMap = new EyeNeighborMap(eye)
    handleVitalPointCases(nbrMap, eye, 1)
  }

  override def getTypeName: String = "E112"
}
