/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.chess

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.common.Move
import com.barrybecker4.game.common.board.{Board, BoardPosition, CaptureList}
import com.barrybecker4.optimization.parameter.ParameterArray

import scala.jdk.CollectionConverters._

enum ChessPieceType(private val symbol: Char) {
  case PAWN extends ChessPieceType('P')
  case ROOK extends ChessPieceType('R')
  case KNIGHT extends ChessPieceType('N')
  case BISHOP extends ChessPieceType('B')
  case QUEEN extends ChessPieceType('Q')
  case KING extends ChessPieceType('K')

  def getSymbol: Char = symbol

  def findPossibleMoves(board: Board[ChessMove], row: Int, col: Int, lastMove: Move, piece: ChessPiece): java.util.List[ChessMove] = {
    import ChessPieceType._
    this match {
      case PAWN => findPawnMoves(board, row, col, piece)
      case ROOK => findRookMoves(board, row, col, piece)
      case KNIGHT => findKnightMoves(board, row, col, piece)
      case BISHOP => findBishopMoves(board, row, col, piece)
      case QUEEN => findQueenMoves(board, row, col, lastMove, piece)
      case KING => findKingMoves(board, row, col, piece)
    }
  }

  def getWeightedScore(side: Int, pos: BoardPosition, weights: ParameterArray, advancement: Int): Double = {
    this match {
      case PAWN => 
        var score = side * advancement * weights.get(ChessWeights.PAWN_ADVANCEMENT_WEIGHT_INDEX).getValue
        score += side * weights.get(ChessWeights.PAWN_WEIGHT_INDEX).getValue
        score
      case ROOK => side * weights.get(ChessWeights.ROOK_WEIGHT_INDEX).getValue
      case KNIGHT => side * weights.get(ChessWeights.KNIGHT_WEIGHT_INDEX).getValue
      case BISHOP => side * weights.get(ChessWeights.BISHOP_WEIGHT_INDEX).getValue
      case QUEEN => side * weights.get(ChessWeights.QUEEN_WEIGHT_INDEX).getValue
      case KING => side * weights.get(ChessWeights.KING_WEIGHT_INDEX).getValue
    }
  }
}

object ChessPieceType {
  private def findPawnMoves(board: Board[ChessMove], row: Int, col: Int, piece: ChessPiece): java.util.List[ChessMove] = {
    val moveList = scala.collection.mutable.ListBuffer[ChessMove]()
    val direction = if (piece.isOwnedByPlayer1) 1 else -1

    if (piece.isFirstTimeMoved) {
      checkPawnForward(row, col, direction, 2, board, moveList, piece)
    }
    checkPawnForward(row, col, direction, 1, board, moveList, piece)
    checkPawnDiagonal(row, col, direction, -1, board, moveList, piece)
    checkPawnDiagonal(row, col, direction, 1, board, moveList, piece)

    moveList.asJava
  }

  private def checkPawnForward(row: Int, col: Int, direction: Int, numSteps: Int, b: Board[ChessMove],
                               moveList: scala.collection.mutable.ListBuffer[ChessMove], piece: ChessPiece): Unit = {
    val next = b.getPosition(row + numSteps * direction, col)
    checkForNonCapture(next, row, col, moveList, piece)
  }

  private def checkPawnDiagonal(row: Int, col: Int, direction: Int, colInc: Int, b: Board[ChessMove],
                                moveList: scala.collection.mutable.ListBuffer[ChessMove], piece: ChessPiece): Unit = {
    val diag = b.getPosition(row + direction, col + colInc)
    checkForCapture(diag, row, col, moveList, piece)
  }

  private def findRookMoves(board: Board[ChessMove], row: Int, col: Int, piece: ChessPiece): java.util.List[ChessMove] = {
    val moveList = scala.collection.mutable.ListBuffer[ChessMove]()
    checkRunDirection(row, col, 1, 0, board, moveList, piece)
    checkRunDirection(row, col, -1, 0, board, moveList, piece)
    checkRunDirection(row, col, 0, 1, board, moveList, piece)
    checkRunDirection(row, col, 0, -1, board, moveList, piece)
    moveList.asJava
  }

  private def findKnightMoves(board: Board[ChessMove], row: Int, col: Int, piece: ChessPiece): java.util.List[ChessMove] = {
    val knightMoveRow = Array(2, 2, -2, -2, 1, -1, 1, -1)
    val knightMoveCol = Array(1, -1, 1, -1, 2, 2, -2, -2)
    getEightDirectionalMoves(board, row, col, knightMoveRow, knightMoveCol, piece)
  }

  private def findBishopMoves(board: Board[ChessMove], row: Int, col: Int, piece: ChessPiece): java.util.List[ChessMove] = {
    val moveList = scala.collection.mutable.ListBuffer[ChessMove]()
    checkRunDirection(row, col, 1, 1, board, moveList, piece)
    checkRunDirection(row, col, 1, -1, board, moveList, piece)
    checkRunDirection(row, col, -1, 1, board, moveList, piece)
    checkRunDirection(row, col, -1, -1, board, moveList, piece)
    moveList.asJava
  }

  private def findQueenMoves(board: Board[ChessMove], row: Int, col: Int, lastMove: Move, piece: ChessPiece): java.util.List[ChessMove] = {
    val moveList = scala.collection.mutable.ListBuffer[ChessMove]()
    val rookMoveList = findRookMoves(board, row, col, piece).asScala
    val bishopMoveList = findBishopMoves(board, row, col, piece).asScala
    moveList.addAll(rookMoveList)
    moveList.addAll(bishopMoveList)
    moveList.asJava
  }

  private def findKingMoves(board: Board[ChessMove], row: Int, col: Int, piece: ChessPiece): java.util.List[ChessMove] = {
    val kingMoveRow = Array(1, -1, 0, 0, -1, 1, -1, 1)
    val kingMoveCol = Array(0, 0, 1, -1, -1, 1, 1, -1)
    getEightDirectionalMoves(board, row, col, kingMoveRow, kingMoveCol, piece)
  }

  private def getEightDirectionalMoves(board: Board[ChessMove], row: Int, col: Int,
                                      rowOffsets: Array[Int], colOffsets: Array[Int],
                                      piece: ChessPiece): java.util.List[ChessMove] = {
    val moveList = scala.collection.mutable.ListBuffer[ChessMove]()
    for (i <- 0 until 8) {
      val next = board.getPosition(row + rowOffsets(i), col + colOffsets(i))
      checkForCapture(next, row, col, moveList, piece)
      checkForNonCapture(next, row, col, moveList, piece)
    }
    moveList.asJava
  }

  private def checkRunDirection(curRow: Int, curCol: Int, rowDir: Int, colDir: Int, board: Board[ChessMove],
                                moveList: scala.collection.mutable.ListBuffer[ChessMove], piece: ChessPiece): Unit = {
    var row = curRow + rowDir
    var col = curCol + colDir
    var next = board.getPosition(row, col)

    while ((next != null) && next.isUnoccupied) {
      val m = ChessMove.createMove(
        new ByteLocation(curRow, curCol),
        new ByteLocation(row, col),
        null, 0, piece
      )
      moveList += m
      row += rowDir
      col += colDir
      next = board.getPosition(row, col)
    }

    checkForCapture(next, curRow, curCol, moveList, piece)
  }

  private def checkForNonCapture(next: BoardPosition, row: Int, col: Int,
                                 moveList: scala.collection.mutable.ListBuffer[ChessMove],
                                 piece: ChessPiece): Unit = {
    if ((next != null) && next.isUnoccupied) {
      val m = ChessMove.createMove(
        new ByteLocation(row, col),
        new ByteLocation(next.getRow, next.getCol),
        null, 0, piece
      )
      moveList += m
    }
  }

  private def checkForCapture(next: BoardPosition, row: Int, col: Int,
                             moveList: scala.collection.mutable.ListBuffer[ChessMove],
                             piece: ChessPiece): Unit = {
    if ((next != null) && next.isOccupied && (next.getPiece.isOwnedByPlayer1 != piece.isOwnedByPlayer1)) {
      val capture = new CaptureList()
      capture.add(next.copy())
      val m = ChessMove.createMove(
        new ByteLocation(row, col),
        new ByteLocation(next.getRow, next.getCol),
        capture, 0, piece
      )
      moveList += m
    }
  }
}
