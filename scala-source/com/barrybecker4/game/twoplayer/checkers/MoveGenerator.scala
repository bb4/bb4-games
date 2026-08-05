/* Copyright by Barry G. Becker, 2000-2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.checkers

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.common.{GameContext, MoveList}
import com.barrybecker4.game.common.board.{BoardPosition, CaptureList}
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.optimization.parameter.ParameterArray

import scala.jdk.CollectionConverters._

class MoveGenerator[M <: TwoPlayerMove](
    private val searchable: CheckersSearchable,
    private val weights: ParameterArray
) {
  private val board: CheckersBoard = searchable.getBoard

  def generateMoves(lastMove: CheckersMove): MoveList[CheckersMove] = {
    val player1 = (lastMove == null) || !lastMove.isPlayer1
    val moveList = new MoveList[CheckersMove]()

    for (row <- 1 to CheckersBoard.SIZE) {
      val odd = row % 2
      for (j <- 1 to CheckersBoard.SIZE / 2) {
        val col = 2 * j - odd
        val p = board.getPosition(row, col)
        if (p.isOccupied && p.getPiece.isOwnedByPlayer1 == player1) {
          addMoves(p, lastMove, moveList)
        }
      }
    }
    moveList
  }

  def addMoves(p: BoardPosition, lastMove: TwoPlayerMove, moveList: MoveList[CheckersMove]): Int = {
    var direction = -1
    if (p.getPiece.isOwnedByPlayer1) direction = 1

    var numMovesAdded = 0
    val initialNumMoves = moveList.size
    
    numMovesAdded += addMovesForDirection(p, direction, -1, lastMove, moveList)
    numMovesAdded += addMovesForDirection(p, direction, 1, lastMove, moveList)

    val piece = p.getPiece.asInstanceOf[CheckersPiece]
    if (piece.isKing) {
      numMovesAdded += addMovesForKing(p, lastMove, direction, numMovesAdded, initialNumMoves, moveList)
    }
    numMovesAdded
  }

  private def addMovesForDirection(
      pos: BoardPosition,
      rowInc: Int,
      colInc: Int,
      lastMove: TwoPlayerMove,
      moveList: MoveList[CheckersMove]
  ): Int = {
    val next = board.getPosition(pos.getRow + rowInc, pos.getCol + colInc)
    
    if (next != null) {
      if (next.isUnoccupied) {
        addSimpleMove(pos, rowInc, colInc, lastMove, moveList)
        return 1
      }
      
      val beyondNext = board.getPosition(pos.getRow + 2 * rowInc, pos.getCol + 2 * colInc)
      if (next.isOccupied &&
          next.getPiece.isOwnedByPlayer1 != pos.getPiece.isOwnedByPlayer1 &&
          beyondNext != null && beyondNext.isUnoccupied) {
        return addJumpMoves(pos, rowInc, lastMove, next, beyondNext, moveList)
      }
    }
    0
  }

  private def addSimpleMove(
      pos: BoardPosition,
      rowInc: Int,
      colInc: Int,
      lastMove: TwoPlayerMove,
      moveList: MoveList[CheckersMove]
  ): Unit = {
    val value = if (lastMove != null) lastMove.getValue else 0
    val m = CheckersMove.createMove(
      pos.getLocation,
      pos.getLocation.incrementOnCopy(rowInc, colInc),
      null,
      value,
      pos.getPiece.copy()
    )
    moveList.add(m)
  }

  private def addJumpMoves(
      pos: BoardPosition,
      rowInc: Int,
      lastMove: TwoPlayerMove,
      next: BoardPosition,
      beyondNext: BoardPosition,
      moveList: MoveList[CheckersMove]
  ): Int = {
    val capture = new CaptureList()
    capture.add(next.copy())
    val value = if lastMove != null then lastMove.getValue else 0
    val m = CheckersMove.createMove(
      pos.getLocation,
      beyondNext.getLocation,
      capture,
      value,
      pos.getPiece.copy()
    )

    val jumps = findJumpMoves(beyondNext, rowInc, m, weights)
    moveList.addAll(jumps.asJava)
    jumps.size
  }

  private def checkJumpMove(
      current: BoardPosition,
      m: CheckersMove,
      rowInc: Int,
      colInc: Int,
      jumpMoves: scala.collection.mutable.ListBuffer[CheckersMove],
      weights: ParameterArray
  ): Int = {
    val next = board.getPosition(current.getRow + rowInc, current.getCol + colInc)
    val beyondNext = board.getPosition(current.getRow + 2 * rowInc, current.getCol + 2 * colInc)

    val opponentAdjacent = next != null && next.isOccupied && 
                          (next.getPiece.isOwnedByPlayer1 != m.isPlayer1)
    
    if (opponentAdjacent &&
        beyondNext != null && beyondNext.isUnoccupied &&
        m.captureList != null && !m.captureList.alreadyCaptured(next)) {
      
      val mm = m.copy()
      mm.setToLocation(new ByteLocation(beyondNext.getLocation.row, beyondNext.getLocation.col))
      mm.captureList.add(next.copy())

      var justKinged = false
      GameContext.log(2, s"calling findJumpMoves on $beyondNext rowinc=$rowInc magnitude of capturelist=${mm.captureList.size}")
      
      if (mm.getPiece.getType == CheckersPiece.REGULAR_PIECE &&
          ((mm.isPlayer1 && mm.getToRow == CheckersBoard.SIZE) ||
           (!mm.isPlayer1 && mm.getToRow == 1))) {
        mm.kinged = true
        justKinged = true
        mm.setPiece(new CheckersPiece(mm.isPlayer1, CheckersPieceType.KING))
        GameContext.log(2, s"KINGED: $mm")
      } else {
        mm.kinged = false
      }

      if (!justKinged) {
        val list = findJumpMoves(beyondNext, rowInc, mm, weights)
        jumpMoves.addAll(list)
        return list.size
      }
    }
    0
  }

  private def findJumpMoves(
      current: BoardPosition,
      rowInc: Int,
      m: CheckersMove,
      weights: ParameterArray
  ): scala.collection.mutable.ListBuffer[CheckersMove] = {
    val jumpMoves = scala.collection.mutable.ListBuffer[CheckersMove]()
    var moreJumps = false

    if (checkJumpMove(current, m, rowInc, -1, jumpMoves, weights) > 0) moreJumps = true
    if (checkJumpMove(current, m, rowInc, 1, jumpMoves, weights) > 0) moreJumps = true

    if (m.getPiece.getType == CheckersPiece.KING && !m.kinged) {
      if (checkJumpMove(current, m, -rowInc, -1, jumpMoves, weights) > 0) moreJumps = true
      if (checkJumpMove(current, m, -rowInc, 1, jumpMoves, weights) > 0) moreJumps = true
    }

    if (!moreJumps) {
      board.makeMove(m)
      m.setValue(searchable.worth(m, weights))
      board.undoMove()
      jumpMoves += m
    }
    jumpMoves
  }

  private def addMovesForKing(
      p: BoardPosition,
      lastMove: TwoPlayerMove,
      direction: Int,
      numMovesAdded: Int,
      initialNumMoves: Int,
      moveList: MoveList[CheckersMove]
  ): Int = {
    var numKingMoves = 0
    numKingMoves += addMovesForDirection(p, -direction, -1, lastMove, moveList)
    numKingMoves += addMovesForDirection(p, -direction, 1, lastMove, moveList)

    val numMoves = searchable.getNumMoves()
    if (numMoves - 4 > 1) {
      val moveToCheck = searchable.getMoveList.get(numMoves - 4)
      if (moveToCheck.captureList == null) {
        var i = 0
        while (i < numKingMoves) {
          val m = moveList.get(initialNumMoves + numMovesAdded + i)
          GameContext.log(1, s"lastMove=$lastMove")
          assert(m.isPlayer1 == moveToCheck.isPlayer1,
            s"player ownership not equal comparing \n$m with \n$moveToCheck")
          
          if (m.captureList == null && m.equals(moveToCheck)) {
            GameContext.log(0, s"found a cycle. new score = ${m.getValue} old score=${moveToCheck.getValue} remove move= $m")
            moveList.remove(m)
            numKingMoves -= 1
            // break
            i = numKingMoves
          } else {
            i += 1
          }
        }
      }
    }
    numKingMoves
  }
}
