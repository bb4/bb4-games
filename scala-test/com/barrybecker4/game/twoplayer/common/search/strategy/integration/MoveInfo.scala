/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy.integration

import com.barrybecker4.game.twoplayer.common.TwoPlayerMove

/** Meta information about a move made during search. @author Barry Becker */
class MoveInfo(private val move: TwoPlayerMove, private val numMovesConsidered: Long):

  def this(move: TwoPlayerMove) =
    this(move, MoveInfo.UNKNOWN)

  def getMove: TwoPlayerMove = move

  def hasMovesConsidered: Boolean = numMovesConsidered != MoveInfo.UNKNOWN

  def getNumMovesConsidered: Long = numMovesConsidered

object MoveInfo:
  private val UNKNOWN: Long = -1
