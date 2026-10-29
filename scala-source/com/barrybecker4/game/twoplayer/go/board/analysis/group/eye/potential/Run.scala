/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.group.eye.potential

/**
 * Figure out the "eye potential" contribution from a horizontal or vertical run within a string.
 */
class Run(val firstPos: Int, val endPos: Int, val max: Int, val boundedByFriends: Boolean) {

  /**
   * @return potential score for the runlength.
   */
  def getPotential: Float = {
    val runLength = endPos - firstPos
    assert(runLength > 0)

    if ((firstPos == 1 || endPos == max + 1 || boundedByFriends)) {
      getRunPotentialInternal(runLength)
    }
    else {
      getRunPotentialToBoundary(runLength)
    }
  }

  /**
   * This case is where the run is next to an edge or bounded by friend stones.
   * Weight the potential more heavily.
   * @return potential score for the runlength.
   */
  private def getRunPotentialInternal(runLength: Int): Float = {
    runLength match {
      case 1 => 0.25f
      case 2 => 0.35f
      case 3 => 0.4f
      case 4 => 0.3f
      case 5 => 0.2f
      case 6 => 0.15f
      case 7 => 0.1f
      case 8 => 0.05f
      case _ => 0.0f
    }
  }

  /**
   * A run to boundary. Less weight attributed.
   * @return potential score for the runlength.
   */
  private def getRunPotentialToBoundary(runLength: Int): Float = {
    runLength match {
      case 1 => 0.05f
      case 2 => 0.15f
      case 3 => 0.2f
      case 4 => 0.25f
      case 5 => 0.2f
      case 6 => 0.15f
      case 7 => 0.1f
      case 8 => 0.05f
      case _ => 0.0f
    }
  }
}
