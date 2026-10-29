/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information

import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.eye.IGoEye

/**
 * Info about a false eye
 */
class FalseEyeInformation extends AbstractEyeInformation {

  override def determineStatus(eye: IGoEye, board: GoBoard): EyeStatus = EyeStatus.FALSE

  override def getTypeName: String = "False"
}
