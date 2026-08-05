/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku

import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.common.player.PlayerList
import com.barrybecker4.game.twoplayer.common.{TwoPlayerMove, TwoPlayerSearchable}
import com.barrybecker4.game.twoplayer.gomoku.analysis.MoveEvaluator
import com.barrybecker4.game.twoplayer.gomoku.pattern.{GoMokuPatterns, GoMokuWeights, Patterns}
import com.barrybecker4.optimization.parameter.ParameterArray

/**
 * Defines everything the computer needs to know to search for the next gomoku move.
 *
 * @author Barry Becker
 */
class GoMokuSearchable[B <: GoMokuBoard](board: B, players: PlayerList)
  extends TwoPlayerSearchable[TwoPlayerMove, B](board, players) {

  private var moveEvaluator: MoveEvaluator = _
  private var generator: GoMokuMoveGenerator = _

  init()

  private def this(searchable: GoMokuSearchable[B]) = {
    this(searchable.board_, searchable.players)
    init()
  }

  private def init(): Unit = {
    generator = new GoMokuMoveGenerator()
    moveEvaluator = new MoveEvaluator(createPatterns())
  }

  override def copy(): GoMokuSearchable[B] = {
    new GoMokuSearchable[B](this)
  }

  override def getBoard: B = board_

  def createPatterns(): Patterns = {
    new GoMokuPatterns()
  }

  /**
   * Statically evaluate the board position from player1's point of view.
   * @return the lastMoves value modified by the value add of the new move.
   *  a large positive value means that the move is good from player1's viewpoint
   */
  override def worth(lastMove: TwoPlayerMove, weights: ParameterArray): Int = {
    moveEvaluator.worth(getBoard, lastMove, weights)
  }

  /**
   * generate all possible next moves.
   */
  override def generateMoves(lastMove: TwoPlayerMove, weights: ParameterArray): MoveList[TwoPlayerMove] = {
    generator.generateMoves(this, lastMove, weights)
  }

  /**
   * Consider both our moves and opponent moves that result in wins.
   * Opponent moves that result in a win should be blocked.
   * @return Set of moves the moves that result in a certain win or a certain loss.
   */
  override def generateUrgentMoves(lastMove: TwoPlayerMove, weights: ParameterArray): MoveList[TwoPlayerMove] = {
    generator.generateUrgentMoves(this, lastMove, weights)
  }

  /**
   * Consider the delta big if &gt;= w. Where w is the value of a near win.
   * @return true if the last move created a big change in the score
   */
  override def inJeopardy(move: TwoPlayerMove, weights: ParameterArray): Boolean = {
    if (move == null)
      return false
    val newValue = worth(move, weights)
    val diff = newValue - move.getValue
    diff > getJeopardyWeight
  }

  def getJeopardyWeight: Int = GoMokuWeights.JEOPARDY_WEIGHT
}
