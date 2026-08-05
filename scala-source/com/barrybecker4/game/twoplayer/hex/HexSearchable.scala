package com.barrybecker4.game.twoplayer.hex

import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.common.player.PlayerList
import com.barrybecker4.game.twoplayer.common.{TwoPlayerMove, TwoPlayerSearchable}
import com.barrybecker4.optimization.parameter.ParameterArray

/**
 * Defines everything the computer needs to know to play Hex.
 */
class HexSearchable(board: HexBoard, players: PlayerList) 
    extends TwoPlayerSearchable[TwoPlayerMove, HexBoard](board, players):

  private val generator = HexMoveGenerator()
  private val worthCalculator = WorthCalculator()

  def this(searchable: HexSearchable) =
    this(searchable.board_, searchable.players.clone().asInstanceOf[PlayerList])

  override def copy(): HexSearchable =
    HexSearchable(this)

  override def worth(lastMove: TwoPlayerMove, weights: ParameterArray): Int =
    worthCalculator.calculateWorth(board_, lastMove, weights)

  /**
   * @return some good legal next moves
   */
  override def generateMoves(lastMove: TwoPlayerMove, weights: ParameterArray): MoveList[TwoPlayerMove] =
    generator.generateMoves(this, lastMove, weights)

  override def generateUrgentMoves(lastMove: TwoPlayerMove, weights: ParameterArray): MoveList[TwoPlayerMove] =
    null
