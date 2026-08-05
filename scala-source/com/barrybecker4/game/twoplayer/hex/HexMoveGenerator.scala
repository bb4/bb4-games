package com.barrybecker4.game.twoplayer.hex

import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.twoplayer.common.{BestMoveFinder, TwoPlayerMove}
import com.barrybecker4.optimization.parameter.ParameterArray

/**
 * Responsible for determining a set of reasonable next moves.
 */
class HexMoveGenerator:

  /**
   * @return all reasonably good next moves.
   */
  final def generateMoves(searchable: HexSearchable, lastMove: TwoPlayerMove,
                         weights: ParameterArray): MoveList[TwoPlayerMove] =
    val moveList = MoveList[TwoPlayerMove]()
    val hb = searchable.getBoard
    val candMoves = hb.getCandidateMoves
    val player1 = (lastMove == null) || !lastMove.isPlayer1
    val ncols = hb.getNumCols
    val nrows = hb.getNumRows

    for i <- 1 to nrows do
      for j <- 1 to ncols do
        if candMoves.isCandidateMove(i, j) then
          val lastValue = if lastMove == null then 0 else lastMove.getValue
          val move = TwoPlayerMove.createMove(i, j, lastValue, GamePiece(player1))

          searchable.makeInternalMove(move)
          move.setValue(searchable.worth(move, weights))
          searchable.undoInternalMove(move)
          moveList.add(move)

    val finder = BestMoveFinder[TwoPlayerMove](
      searchable.getSearchOptions.getBestMovesSearchOptions)
    finder.getBestMoves(moveList)
