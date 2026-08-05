/* Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.hex

import com.barrybecker4.common.geometry.{ByteLocation, Location}
import com.barrybecker4.game.common.board.BoardPosition
import com.barrybecker4.game.twoplayer.common.{TwoPlayerBoard, TwoPlayerMove}
import com.barrybecker4.game.twoplayer.hex.pathsearch.ShortestPathSearch

class HexBoard extends TwoPlayerBoard[TwoPlayerMove]:

  setSize(HexBoard.DEFAULT_SIZE, HexBoard.DEFAULT_SIZE)

  override def getPositionPrototype: BoardPosition =
    HexBoardPosition(1, 1, null)

  def getP1PathCost: Int = getPathCost(true)

  def getP2PathCost: Int = getPathCost(false)

  def getCenter: Location =
    ByteLocation((getNumRows / 2).toByte, (getNumCols / 2).toByte)

  private def getPathCost(player1: Boolean): Int =
    ShortestPathSearch(this, player1).getShortestPathCost

  override def undoInternalMove(move: TwoPlayerMove): Unit =
    getPosition(move.getToRow, move.getToCol).clear()

  override def copy(): HexBoard =
    val board = new HexBoard()
    board.setSize(getNumRows, getNumCols)
    board.positions_ = this.positions_.copy()
    board.getMoveList.clear()
    var i = 0
    while i < this.getMoveList.getNumMoves do
      board.getMoveList.add(this.getMoveList.get(i))
      i += 1
    board

  override def getNumPositionStates: Int = 3

  override def getMaxNumMoves: Int = getNumCols * getNumRows

  def getCandidateMoves: HexCandidateMoves =
    HexCandidateMoves(this)

object HexBoard:
  private val DEFAULT_SIZE = 11
