/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.chess

import com.barrybecker4.game.common.{GameContext, MoveList}
import com.barrybecker4.game.common.board.BoardPosition
import com.barrybecker4.game.common.player.PlayerList
import com.barrybecker4.game.twoplayer.checkers.CheckersBoard
import com.barrybecker4.game.twoplayer.common.TwoPlayerSearchable
import com.barrybecker4.optimization.parameter.ParameterArray

import scala.jdk.CollectionConverters._

class ChessSearchable(board: ChessBoard, players: PlayerList)
    extends TwoPlayerSearchable[ChessMove, ChessBoard](board, players) {

  def this(searchable: ChessSearchable) = {
    this(searchable.board_, searchable.players)
  }

  override def copy(): ChessSearchable = new ChessSearchable(this)

  override def getBoard: ChessBoard = board_

  override def worth(lastMove: ChessMove, weights: ParameterArray): Int = {
    var score = 0.0

    for (row <- 1 to CheckersBoard.SIZE; col <- 1 to CheckersBoard.SIZE) {
      val pos = getBoard.getPosition(row, col)
      if (pos.isOccupied) {
        val piece = pos.getPiece.asInstanceOf[ChessPiece]
        val side = if (piece.isOwnedByPlayer1) 1 else -1
        val advancement = if (piece.isOwnedByPlayer1) pos.getRow - 1 
                         else (CheckersBoard.SIZE - pos.getRow - 1)
        score += piece.getWeightedScore(side, pos, weights, advancement)
      }
    }
    score.toInt
  }

  override def generateMoves(lastMove: ChessMove, weights: ParameterArray): MoveList[ChessMove] = {
    val moveList = new MoveList[ChessMove]()
    val player1 = (lastMove == null) || !lastMove.isPlayer1

    for (row <- 1 to CheckersBoard.SIZE; col <- 1 to CheckersBoard.SIZE) {
      val pos = getBoard.getPosition(row, col)
      if (pos.isOccupied && pos.getPiece.isOwnedByPlayer1 == player1) {
        addMoves(pos, moveList, lastMove, weights)
      }
    }

    removeSelfCheckingMoves(moveList)
    bestMoveFinder_.getBestMoves(moveList)
  }

  override def generateUrgentMoves(lastMove: ChessMove, weights: ParameterArray): MoveList[ChessMove] = {
    new MoveList[ChessMove]()
  }

  def addMoves(pos: BoardPosition, moveList: MoveList[ChessMove], lastMove: ChessMove, weights: ParameterArray): Int = {
    val moves = pos.getPiece.asInstanceOf[ChessPiece].findPossibleMoves(getBoard, pos.getRow, pos.getCol, lastMove)

    import scala.jdk.CollectionConverters._
    moves.asScala.foreach { move =>
      getBoard.makeMove(move)
      move.setValue(worth(move, weights))
      getBoard.undoMove()
    }
    moveList.addAll(moves)
    moveList.size
  }

  def removeSelfCheckingMoves(moveList: java.util.List[_]): Unit = {
    val it = moveList.iterator()
    while (it.hasNext) {
      val move = it.next().asInstanceOf[ChessMove]
      if (getBoard.causesSelfCheck(move)) {
        GameContext.log(2, s"don't allow $move because it puts the king in check.")
        it.remove()
      }
    }
  }
}
