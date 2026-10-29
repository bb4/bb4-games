/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board

import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionList, GoStone}
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import scala.jdk.CollectionConverters._

/**
 * The number of star points used for handicap stones on the board
 * There may be none.
 *
 * Immutable
 */
class HandicapStones(num: Int, boardSize: Int) {

  /** the number of initial handicap stones to use. */
  private val numHandicapStones_ : Int = num

  /** typically there are at most 9 handicap stones in an uneven game */
  private val starPoints_ : GoBoardPositionList = initStarPoints(boardSize)

  /**
   * @return number of handicap stones in this set.
   */
  def getNumber: Int = numHandicapStones_

  def getStarPoints: java.util.List[?] = starPoints_

  /**
   * specify the number of handicap stones that will actually be used this game.
   * public since we might set if from the options dialog
   * @return handicap stones
   */
  def getHandicapMoves: java.util.List[GoMove] = {
    assert(numHandicapStones_ <= starPoints_.size)
    val handicapMoves = new java.util.ArrayList[GoMove](numHandicapStones_)

    for (i <- 0 until numHandicapStones_) {
      val hpos = starPoints_.get(i)
      val m = new GoMove(hpos.getLocation, 0, hpos.getPiece.asInstanceOf[GoStone])
      handicapMoves.add(m)
    }
    handicapMoves
  }

  /**
   * initialize a list of stones at the star points
   */
  private def initStarPoints(boardSize: Int): GoBoardPositionList = {
    // initialize the list of handicap stones.
    // The number of these that actually get placed on the board
    // depends on the handicap
    val starPoints = new GoBoardPositionList()
    val min = if (boardSize < 13) 3 else 4
    val max = boardSize - (min - 1)
    val mid = (boardSize >> 1) + 1

    // add the star points
    val handicapStone = new GoStone(true, HandicapStones.HANDICAP_STONE_HEALTH)
    starPoints.add(new GoBoardPosition(min, min, null, handicapStone.copy()))
    starPoints.add(new GoBoardPosition(max, max, null, handicapStone.copy()))
    starPoints.add(new GoBoardPosition(min, max, null, handicapStone.copy()))
    starPoints.add(new GoBoardPosition(max, min, null, handicapStone.copy()))
    starPoints.add(new GoBoardPosition(min, mid, null, handicapStone.copy()))
    starPoints.add(new GoBoardPosition(max, mid, null, handicapStone.copy()))
    starPoints.add(new GoBoardPosition(mid, min, null, handicapStone.copy()))
    starPoints.add(new GoBoardPosition(mid, max, null, handicapStone.copy()))
    starPoints.add(new GoBoardPosition(mid, mid, null, handicapStone))
    starPoints
  }
}

object HandicapStones {
  /** Initially the handicap stones have this health score */
  private val HANDICAP_STONE_HEALTH = 0.8f
}
