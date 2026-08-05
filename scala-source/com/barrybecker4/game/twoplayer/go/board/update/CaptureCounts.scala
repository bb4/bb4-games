/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.update

import com.barrybecker4.game.twoplayer.go.board.move.GoMove

/**
 * Maintains the count of captured stones for each side.
 * @author Barry Becker
 */
class CaptureCounts {

  private var numWhiteStonesCaptured_ : Int = 0
  private var numBlackStonesCaptured_ : Int = 0

  def copy(): CaptureCounts = {
    val countsCopy = new CaptureCounts()
    countsCopy.numBlackStonesCaptured_ = this.numBlackStonesCaptured_
    countsCopy.numWhiteStonesCaptured_ = this.numWhiteStonesCaptured_
    countsCopy
  }

  def getNumCaptures(player1StonesCaptured: Boolean): Int = {
    if (player1StonesCaptured) numBlackStonesCaptured_ else numWhiteStonesCaptured_
  }

  /**
   * @param move the move just made or removed.
   * @param increment if true then add to number of captures, else subtract.
   */
  def updateCaptures(move: GoMove, increment: Boolean): Unit = {
    val numCaptures = move.getNumCaptures
    val num = if (increment) move.getNumCaptures else -move.getNumCaptures

    if (numCaptures > 0) {
      if (move.isPlayer1) {
        numWhiteStonesCaptured_ += num
        assert(numWhiteStonesCaptured_ >= 0,
          s"The number of captured white stones became less than 0 :  $numWhiteStonesCaptured_")
      } else {
        numBlackStonesCaptured_ += num
        assert(numBlackStonesCaptured_ >= 0,
          s"The number of captured black stones became less than 0 :  $numBlackStonesCaptured_")
      }
    }
  }
}
