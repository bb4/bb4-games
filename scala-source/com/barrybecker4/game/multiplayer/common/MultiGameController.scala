/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.common

import com.barrybecker4.game.common.{GameContext, GameController, GameOptions, Move, MoveList}
import com.barrybecker4.game.common.board.{Board, IBoard}
import com.barrybecker4.game.common.online.server.IServerConnection
import com.barrybecker4.game.common.online.server.connection.ServerConnection
import com.barrybecker4.game.common.player.{Player, PlayerAction}
import com.barrybecker4.game.multiplayer.common.online.SurrogateMultiPlayer
import com.barrybecker4.game.multiplayer.common.ui.MultiGameViewer
import com.barrybecker4.optimization.parameter.ParameterArray
import java.awt.Dimension
import java.util.{ArrayList, List}
import javax.swing.SwingUtilities
import scala.jdk.CollectionConverters._

/**
 * Abstract base class for multi player game controllers.
 *
 * @author Barry Becker
 */
abstract class MultiGameController[M <: Move, B <: IBoard[M]](size: Dimension) 
    extends GameController[M, B] {

  var currentPlayerIndex: Int = 0
  var startingPlayerIndex_ : Int = 0
  var playIndex: Int = 0
  private var recentRobotActions_ : List[PlayerAction] = _

  def this() = this(new Dimension(0, 0))

  def this(nrows: Int, ncols: Int) = {
    this(new Dimension(nrows, ncols))
    initializeData()
  }

  override def createBoard: B = createTable(size.width, size.height)

  def createTable(nrows: Int, ncols: Int): B

  override def getMoveList: MoveList[M] =
    getBoard.asInstanceOf[Board[M]].getMoveList

  override def reset(): Unit = {
    super.reset()
    initializeData()
  }

  override def initializeData(): Unit = {
    startingPlayerIndex_ = 0
    playIndex = 0
    currentPlayerIndex = 0
    recentRobotActions_ = new ArrayList[PlayerAction]()
    initPlayers()
  }

  override def getOptions: GameOptions = {
    if (gameOptions == null) {
      gameOptions = createOptions
    }
    gameOptions
  }

  def createOptions: GameOptions

  def initPlayers(): Unit

  override def getCurrentPlayer: MultiGamePlayer =
    getPlayers.get(currentPlayerIndex).asInstanceOf[MultiGamePlayer]

  override def computerMovesFirst(): Unit = {
    val viewer = this.getViewer.asInstanceOf[MultiGameViewer[M, Board[M]]]
    viewer.doComputerMove(getCurrentPlayer)
  }

  override def createServerConnection: IServerConnection =
    new ServerConnection(getServerPort)

  override def isOnlinePlayAvailable: Boolean =
    getServerConnection.isConnected

  override def getServerPort: Int = {
    assert(false, s"online game play not supported for ${this.getClass.getName}")
    0
  }

  override def handlePlayerAction(action: PlayerAction): Unit = {
    getPlayers.asScala.foreach { p =>
      if (p.getActualPlayer.getName.equals(action.getPlayerName)) {
        p.getActualPlayer.asInstanceOf[MultiGamePlayer].setAction(action)
        doAdvanceToNextPlayer()
      }
    }
  }

  def advanceToNextPlayer(): Unit = {
    SwingUtilities.invokeLater(() => doAdvanceToNextPlayer())
  }

  def doAdvanceToNextPlayer(): Unit = {
    val pviewer = getViewer.asInstanceOf[MultiGameViewer[M, Board[M]]]
    pviewer.refresh()

    if (!isDone) {
      advanceToNextPlayerIndex()
      val currentPlayer = getCurrentPlayer
      GameContext.log(0, s"advanced to player=${currentPlayer.getName}")
      if (currentPlayer.isSurrogate) {
        GameContext.log(0, s"about to request surrogate move for $currentPlayer in controller=$this in thread=${Thread.currentThread().getName}")
        pviewer.doSurrogateMove(currentPlayer.asInstanceOf[SurrogateMultiPlayer])
      } else if (!currentPlayer.isHuman) {
        GameContext.log(0, s"now moving for computer player  = $currentPlayer")
        pviewer.doComputerMove(currentPlayer)
      }
    }

    pviewer.sendGameChangedEvent(null.asInstanceOf[M])
  }

  def addRecentRobotAction(action: PlayerAction): Unit = {
    recentRobotActions_.add(action)
  }

  def getRecentRobotActions: List[PlayerAction] = {
    val actions = new ArrayList[PlayerAction]()
    GameContext.log(0, s"There were ${recentRobotActions_.size} recent robot actions.")
    actions.addAll(recentRobotActions_)
    recentRobotActions_.clear()
    actions
  }

  def determineWinners(): List[? <: MultiGamePlayer]

  def advanceToNextPlayerIndex(): Int

  def getPlayer(index: Int): MultiGamePlayer =
    getPlayers.get(index).asInstanceOf[MultiGamePlayer]

  def worth(lastMove: M, weights: ParameterArray): Double = lastMove.getValue

  def generateMoves(lastMove: M, weights: ParameterArray): MoveList[M] = new MoveList[M]()

  def generateUrgentMoves(lastMove: M, weights: ParameterArray): List[_] = null
}
