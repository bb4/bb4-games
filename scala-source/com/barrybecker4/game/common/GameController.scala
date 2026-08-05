/* Copyright by Barry G. Becker, 2000-2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common

import com.barrybecker4.game.common.board.IBoard
import com.barrybecker4.game.common.online.server.IServerConnection
import com.barrybecker4.game.common.persistence.GameExporter
import com.barrybecker4.game.common.player.{PlayerAction, PlayerList}

/**
 * This is an abstract base class for a Game Controller.
 * It contains the key logic for n player games.
 * Instance of this class process requests from the GameViewer.
 *
 * @author Barry Becker
 */
abstract class GameController[M <: Move, B <: IBoard[M]] extends IGameController[M, B] {

  private var board: B = _
  var viewer: GameViewModel = _
  private var players: PlayerList = _
  var gameOptions: GameOptions = _
  private var serverConnection: IServerConnection = _

  GameContext.log(3, s" mem=${Runtime.getRuntime.freeMemory}")

  def setViewer(viewer: GameViewModel): Unit = {
    this.viewer = viewer
  }

  def reset(): Unit = {
    getBoard.reset()
  }

  def getLastMove: M = getMoveList.getLastMove

  override def getNumMoves: Int = getMoveList.getNumMoves

  override def getViewer: GameViewModel = viewer

  def getStrengthOfWin: Int = 0

  override def getBoard: B = {
    if (board == null) {
      board = createBoard
    }
    board
  }

  def createBoard: B

  def initializeData(): Unit

  override def makeMove(move: M): Unit = {
    getBoard.makeMove(move)
  }

  override def undoLastMove(): M = getBoard.undoMove()

  def getExporter: GameExporter[M, B] = {
    GameContext.log(0, s"Error: saveToFile(name, rte) not implemented yet for ${getClass.getName}")
    null
  }

  def restoreFromFile(fileName: String): Unit = {
    GameContext.log(0, s"Error: restoreFromFile($fileName) not implemented yet")
  }

  override def getPlayers: PlayerList = players

  def setPlayers(players: PlayerList): Unit = {
    this.players = players
    initializeData()
  }

  def setOptions(options: GameOptions): Unit = {
    gameOptions = options
  }

  def getOptions: GameOptions

  def getProfiler: AbstractGameProfiler = GameProfiler.getInstance

  def getServerConnection: IServerConnection = {
    if (serverConnection == null) {
      serverConnection = createServerConnection
    }
    serverConnection
  }

  def createServerConnection: IServerConnection = {
    GameContext.log(0, s"Cannot create a server connection for ${this.getClass.getName}. Online play not supported")
    null
  }

  def isOnlinePlayAvailable: Boolean

  def getServerPort: Int = -1

  def handlePlayerAction(action: PlayerAction): Unit = {
    assert(false, s"handlePlayerAction not implemented for ${this.getClass.getName}")
  }
}
