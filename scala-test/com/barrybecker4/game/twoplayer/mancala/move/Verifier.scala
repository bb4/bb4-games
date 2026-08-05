// Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.mancala.move

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.twoplayer.mancala.board.MancalaBoard

/** Used by tests to verify board state. */
class Verifier(board: MancalaBoard):

  def checkOverallBoard(
      stonesAt11: Int,
      stonesAt12: Int,
      stonesAt13: Int,
      stonesAt14: Int,
      stonesAt15: Int,
      stonesAt16: Int,
      stonesAt17: Int,
      stonesAt18: Int,
      stonesAt22: Int,
      stonesAt23: Int,
      stonesAt24: Int,
      stonesAt25: Int,
      stonesAt26: Int,
      stonesAt27: Int
  ): Unit =
    checkStones(new ByteLocation(1, 1), stonesAt11)
    checkStones(new ByteLocation(1, 2), stonesAt12)
    checkStones(new ByteLocation(1, 3), stonesAt13)
    checkStones(new ByteLocation(1, 4), stonesAt14)
    checkStones(new ByteLocation(1, 5), stonesAt15)
    checkStones(new ByteLocation(1, 6), stonesAt16)
    checkStones(new ByteLocation(1, 7), stonesAt17)
    checkStones(new ByteLocation(1, 8), stonesAt18)
    checkStones(new ByteLocation(2, 2), stonesAt22)
    checkStones(new ByteLocation(2, 3), stonesAt23)
    checkStones(new ByteLocation(2, 4), stonesAt24)
    checkStones(new ByteLocation(2, 5), stonesAt25)
    checkStones(new ByteLocation(2, 6), stonesAt26)
    checkStones(new ByteLocation(2, 7), stonesAt27)

  private def checkStones(loc: Location, expNumStones: Int): Unit =
    assert(
      board.getBin(loc).getNumStones == expNumStones,
      "Unexpected stones at location " + loc
    )
