/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common

import com.barrybecker4.game.common.board.IBoard
import com.barrybecker4.game.common.player.{Player, PlayerList}

/**
 * This is the interface that all game controllers should implement.
 * Providing both an interface and an abstract implementation is a pattern
 * which maximizes flexibility in a framework. The interface defines the
 * public contract. The abstract class may be package private if we don't
 * want to expose it. Other classes may implement this interface without
 * extending the abstract base class.
 *
 * @see GameController for the abstract implementation of this interface
 * @author Barry Becker
 */
trait IGameController[M <: Move, B <: IBoard[M]] {

  def getBoard: B

  def getViewer: GameViewModel

  def undoLastMove(): M

  def makeMove(m: M): Unit

  def getMoveList: MoveList[M]

  def getNumMoves: Int

  def getPlayers: PlayerList

  def getCurrentPlayer: Player

  def computerMovesFirst(): Unit

  def isDone: Boolean
}
