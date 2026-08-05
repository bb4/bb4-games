/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy.integration

import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.common.search.Progress

/** The expected moves for a given game for each combination of game progress and player. @author Barry Becker */
class ExpectedMoveMatrix(
    private val beginningP1: MoveInfo,
    private val beginningP2: MoveInfo,
    private val middleP1: MoveInfo,
    private val middleP2: MoveInfo,
    private val endP1: MoveInfo,
    private val endP2: MoveInfo
):

  /** Use this constructor if you do not have the numMovesConsidered info to include. */
  def this(
      beginningPlayer1: TwoPlayerMove,
      beginningPlayer2: TwoPlayerMove,
      middlePlayer1: TwoPlayerMove,
      middlePlayer2: TwoPlayerMove,
      endPlayer1: TwoPlayerMove,
      endPlayer2: TwoPlayerMove
  ) =
    this(
      new MoveInfo(beginningPlayer1),
      new MoveInfo(beginningPlayer2),
      new MoveInfo(middlePlayer1),
      new MoveInfo(middlePlayer2),
      new MoveInfo(endPlayer1),
      new MoveInfo(endPlayer2)
    )

  def getExpectedMove(progress: Progress, player1: Boolean): MoveInfo =
    progress match
      case Progress.BEGINNING => if player1 then beginningP1 else beginningP2
      case Progress.MIDDLE => if player1 then middleP1 else middleP2
      case Progress.END => if player1 then endP1 else endP2
