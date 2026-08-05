/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information

import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.eye.IGoEye

/**
 * Describes an eye shape with 8 or more internal spaces.
 * It is almost certainly an unconditionally alive shape.
 * If its not, you have to play more to tell.
 *
 * @author Barry Becker
 */
class TerritorialEyeInformation extends AbstractEyeInformation {

  override def determineStatus(eye: IGoEye, board: GoBoard): EyeStatus = EyeStatus.ALIVE

  override def getTypeName: String = "Territorial"

  override def toString: String = getTypeName
}
