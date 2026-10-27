/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.Move
import com.barrybecker4.game.common.board.{Board, BoardPosition}
import com.barrybecker4.game.common.player.PlayerList
import com.barrybecker4.game.multiplayer.trivial.player.TrivialPlayer

/**
 * Representation of a Trivial Game Board
 *
 * @author Barry Becker
 */
class TrivialTable(numRows: Int, numCols: Int) extends Board:

  setSize(numRows, numCols)

  /** Copy constructor */
  def this(table: TrivialTable) =
    this(table.getNumRows, table.getNumCols)
    positions_ = table.positions_.copy()

  override def copy(): TrivialTable =
    new TrivialTable(this)

  /**
   * A trivial game has no real limit so we just return a huge number.
   * @return max number of trivial rounds allowed.
   */
  override def getMaxNumMoves: Int = 1000

  /**
   * place the players around the trivial table
   */
  def initPlayers(players: PlayerList): Unit =
    var angle = 0.6 * Math.PI
    val angleIncrement = 2.0 * Math.PI / players.size
    val rowRad = getNumRows >> 1
    val colRad = getNumCols >> 1
    reset()

    val it = players.iterator()
    while it.hasNext do
      val p = it.next()
      val tp = p.getActualPlayer.asInstanceOf[TrivialPlayer]

      val row = (0.93 * rowRad + (TrivialTable.RADIUS * rowRad) * Math.sin(angle)).toInt
      val col = (0.9 * colRad + (TrivialTable.RADIUS * colRad) * Math.cos(angle)).toInt

      val position = getPosition(row, col)
      position.setPiece(tp.getPiece)
      tp.getPiece.setLocation(position.getLocation)
      angle += angleIncrement

  /**
   * given a move specification, execute it on the board
   *
   * @param move the move to make, if possible.
   * @return false if the move is illegal.
   */
  override def makeInternalMove(move: Move): Boolean = true

  /**
   * For Trivial, undoing a move means turning time back a round and
   * restoring the state of the game one full round earlier
   * TODO
   */
  override def undoInternalMove(move: Move): Unit =
    GameContext.log(0, "undo not implemented yet for Trivial.")

object TrivialTable:
  /** size of a players marker */
  private val RADIUS = 0.65
