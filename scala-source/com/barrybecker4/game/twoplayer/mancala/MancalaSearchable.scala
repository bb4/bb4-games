/* Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala

import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.common.player.PlayerList
import com.barrybecker4.game.twoplayer.common.TwoPlayerSearchable
import com.barrybecker4.game.twoplayer.mancala.board.MancalaBoard
import com.barrybecker4.game.twoplayer.mancala.move.{MancalaMove, MancalaMoveGenerator, MoveEvaluator}
import com.barrybecker4.optimization.parameter.ParameterArray

/**
  * Defines everything the computer needs to know to search for the next mancala move.
  *
  * @author Barry Becker
  */
class MancalaSearchable(board: MancalaBoard, players: PlayerList)
  extends TwoPlayerSearchable[MancalaMove, MancalaBoard](board, players):

  private val moveEvaluator = MoveEvaluator()
  private val generator = MancalaMoveGenerator()

  def this(searchable: MancalaSearchable) =
    this(searchable.board_, searchable.players)

  override def copy(): MancalaSearchable =
    MancalaSearchable(this)

  override def getBoard: MancalaBoard = board_

  /**
    * Statically evaluate the board position.
    * @return the lastMoves value modified by the value add of the new move.
    *  a large positive value means that the move is good from player1's viewpoint
    */
  override def worth(lastMove: MancalaMove, weights: ParameterArray): Int =
    moveEvaluator.worth(getBoard, lastMove, weights)

  /**
    * generate all possible next moves.
    */
  override def generateMoves(lastMove: MancalaMove, weights: ParameterArray): MoveList[MancalaMove] =
    generator.generateMoves(this, lastMove, weights)

  /**
    * Consider both our moves and opponent moves that result in wins.
    * Opponent moves that result in a win should be blocked.
    * @return Set of moves the moves that result in a certain win or a certain loss.
    */
  override def generateUrgentMoves(lastMove: MancalaMove, weights: ParameterArray): MoveList[MancalaMove] =
    generator.generateUrgentMoves(this, lastMove, weights)

  /**
    * Consider the delta big if &gt;= w. Where w is the value of a near win.
    * @return true if the last move created a big change in the score
    */
  override def inJeopardy(move: MancalaMove, weights: ParameterArray): Boolean =
    if move == null then
      return false
    val newValue = worth(move, weights)
    val diff = newValue - move.getValue
    diff > getJeopardyWeight

  protected def getJeopardyWeight: Int =
    MancalaWeights.JEOPARDY_WEIGHT
