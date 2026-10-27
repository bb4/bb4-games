/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.model

import com.barrybecker4.game.common.Move
import com.barrybecker4.game.common.board.{Board, BoardPosition}
import com.barrybecker4.game.common.player.{Player, PlayerList}
import com.barrybecker4.game.multiplayer.poker.player.PokerPlayer

class PokerTable(numRows: Int, numCols: Int) extends Board[PokerRound]() {
  import PokerTable.*

  setSize(numRows, numCols)
  private val round: PokerRound = PokerRound()

  def this(table: PokerTable) = {
    this(table.getNumRows, table.getNumCols)
    round.addToPot(table.getPotValue)
  }

  override def copy(): PokerTable = PokerTable(this)

  def getRound: PokerRound = round

  def getPotValue: Int = round.getPotValue

  override def getMaxNumMoves: Int = 100000

  def initPlayers(players: PlayerList): Unit = {
    var angle = 0.6 * Math.PI
    val angleIncrement = 2.0 * Math.PI / players.size
    val rowRad = getNumRows >> 1
    val colRad = getNumCols >> 1
    reset()
    
    val it = players.iterator()
    while (it.hasNext) {
      val p = it.next()
      val pp = p.getActualPlayer.asInstanceOf[PokerPlayer]

      val row = (0.93 * rowRad + (RADIUS * rowRad) * Math.sin(angle)).toInt
      val col = (0.9 * colRad + (RADIUS * colRad) * Math.cos(angle)).toInt

      val position = getPosition(row, col)
      position.setPiece(pp.getPiece)
      pp.getPiece.setLocation(position.getLocation)
      angle += angleIncrement
    }
  }

  override def makeInternalMove(move: PokerRound): Boolean = true

  override def undoInternalMove(move: PokerRound): Unit = 
    throw UnsupportedOperationException("undo no implemented yet for poker.")
}

object PokerTable {
  private val RADIUS = 0.65
}
