/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis

import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.common.board.CaptureList
import com.barrybecker4.game.twoplayer.go.board.PositionalScore

/**
 * All the stuff used to compute the worth.
 * temp class used for debugging.
 *
 * @author Barry Becker
 */
class WorthInfo(
  territoryDelta: Double,
  captureScore: Double,
  blackCap: Int,
  whiteCap: Int,
  positionalScore: Double,
  positionalScores: Array[Array[PositionalScore]],
  captures: CaptureList,
  worth: Double,
  moves: MoveList[?]
) {

  /**
   * Show all the worth information
   * @return string form.
   */
  override def toString: String = {
    val bldr = new StringBuilder()
    bldr.append(s" territoryDelta=$territoryDelta captureScore=$captureScore")
    bldr.append(s"(b=$blackCap w=$whiteCap) positionalScore=$positionalScore")
    if (captures != null) bldr.append(s" captures=$captures")
    bldr.append(s" worth=$worth \nposScores:\n")
    for (posScoreRow <- positionalScores) {
      for (pscore <- posScoreRow) {
        bldr.append(pscore)
      }
      bldr.append("\n")
    }
    bldr.append(s"moves=$moves")
    bldr.append("\n")

    bldr.toString
  }
}
