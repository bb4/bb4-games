/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go

import com.barrybecker4.ca.dj.jigo.sgf.SGFException
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.player.{Player, PlayerList, PlayerOptions}
import com.barrybecker4.game.twoplayer.common.TwoPlayerController
import com.barrybecker4.game.twoplayer.common.TwoPlayerOptions
import com.barrybecker4.game.twoplayer.common.cache.ScoreCache
import com.barrybecker4.game.twoplayer.common.persistence.TwoPlayerGameExporter
import com.barrybecker4.game.twoplayer.go.board.{GoBoard, GoProfiler, GoSearchable}
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import com.barrybecker4.game.twoplayer.go.options.{GoOptions, GoPlayerOptions, GoWeights}
import com.barrybecker4.game.twoplayer.go.persistence.{GoGameExporter, GoGameImporter}
import com.barrybecker4.game.twoplayer.go.ui.rendering.GoStoneRenderer
import java.awt.Color
import java.io.{IOException, InputStream}
import scala.jdk.CollectionConverters._

/**
 * Defines everything the computer needs to know to play Go.
 *
 * @author Barry Becker
 */
final class GoController() extends TwoPlayerController[GoMove, GoBoard] {

  private var scoreCache_ : ScoreCache = scala.compiletime.uninitialized
  private var boardOpts: BoardOptions = new BoardOptions(GoController.DEFAULT_SIZE, 0)

  /**
   * Construct the Go game controller given dimensions and number of handicap stones.
   * @param numHandicapStones 0 - 9 handicap stones to show initially.
   */
  def this(size: Int, numHandicapStones: Int) = {
    this()
    boardOpts = new BoardOptions(size, numHandicapStones)
    initializeData()
  }

  override def createBoard: GoBoard = new GoBoard(boardOpts.size, boardOpts.numHandicaps)

  override def createOptions(): TwoPlayerOptions = new GoOptions()

  /**
   * create the 2 players.
   */
  override def createPlayers(): PlayerList = {
    val players = new PlayerList()
    players.add(new Player(createPlayerOptions(GameContext.getLabel("BLACK"),
      GoStoneRenderer.DEFAULT_PLAYER1_COLOR), true))
    players.add(new Player(createPlayerOptions(GameContext.getLabel("WHITE"),
      GoStoneRenderer.DEFAULT_PLAYER1_COLOR), false))
    players
  }

  override def createPlayerOptions(playerName: String, color: Color): PlayerOptions = {
    new GoPlayerOptions(playerName, color)
  }

  /**
   * this gets the Go specific patterns and weights.
   */
  override def initializeData(): Unit = {
    weights_ = new GoWeights()
  }

  override def getProfiler: GoProfiler = GoProfiler.getInstance

  /**
   * specify the number of handicap stones.
   * @param handicap number of handicap stones to place on the board at star points.
   */
  def setHandicap(handicap: Int): Unit = {
    getBoard.setHandicap(handicap)
    player1sTurn = false
  }

  /**
   * @return true if the computer is to make the first move.
   */
  override def doesComputerMoveFirst(): Boolean = {
    val handicap = getBoard.getHandicap
    val player1 = getPlayers.getPlayer1
    (!player1.isHuman && (handicap == 0)) ||
      (player1.isHuman && (handicap > 0))
  }

  /**
   * Measure is determined by the score (amount of territory + captures)
   * If called before the end of the game it just returns 0 - same as it does in the case of a tie.
   * @return some measure of how overwhelming the win was. May need to negate based on which player one.
   */
  override def getStrengthOfWin: Int = {
    Math.abs(getFinalScore(true) - getFinalScore(false)).toInt
  }

  /**
   * @param player1 if true, then the score for player one is returned else player2's score is returned
   * @return the score (larger is better regardless of player)
   */
  def getFinalScore(player1: Boolean): Double = {
    if (isProcessing) {
      GameContext.log(0, "Error: tried to get Score() while processing!")
      return 0
    }
    GameContext.log(0, "cache results " + scoreCache_)
    getSearchable().asInstanceOf[GoSearchable].getFinalScore(player1)
  }

  /**
   * Call this at the end of the game when we need to try to get an accurate score.
   * @param forPlayer1  true if for player one (black)
   * @return the actual amount of territory for the specified player (each empty space counts as one)
   */
  def getFinalTerritory(forPlayer1: Boolean): Int = {
    getSearchable().asInstanceOf[GoSearchable].getFinalTerritory(forPlayer1)
  }

  /**
   * Restore the game board back to its initial opening state.
   */
  override def reset(): Unit = {
    super.reset()
    if (getBoard.getHandicap > 0) {
      player1sTurn = false
    }
    scoreCache_ = new ScoreCache()
  }

  override def computerMovesFirst(): Unit = {
    val moveList = getSearchable().generateMoves(null, weights_.getPlayer1Weights)
    val m = moveList.get(0).asInstanceOf[GoMove]
    makeMove(m)
  }

  /**
   * save the current state of the go game to a file in SGF (4) format (standard game format).
   * This should some day be xml (xgf)
   */
  override def getExporter: TwoPlayerGameExporter[GoMove, GoBoard] = new GoGameExporter(this)

  @throws[IOException]
  @throws[SGFException]
  override def restoreFromStream(iStream: InputStream): Unit = {
    val importer = new GoGameImporter(this)
    importer.restoreFromStream(iStream)
  }

  override def createSearchable(board: GoBoard, players: PlayerList): GoSearchable = {
    new GoSearchable(board, players, scoreCache_)
  }

  private class BoardOptions(val size: Int, val numHandicaps: Int)
}

object GoController {
  val VERSION = "0.99"
  val USE_RELATIVE_GROUP_SCORING = true
  private val DEFAULT_SIZE = 9
  val WIN_THRESHOLD = 2000
}
