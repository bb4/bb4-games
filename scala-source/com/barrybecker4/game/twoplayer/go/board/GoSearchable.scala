/* Copyright by Barry G. Becker, 2000-2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board

import com.barrybecker4.game.common.{GameContext, MoveList}
import com.barrybecker4.game.common.board.BoardPosition
import com.barrybecker4.game.common.player.PlayerList
import com.barrybecker4.game.twoplayer.common.TwoPlayerSearchable
import com.barrybecker4.game.twoplayer.common.cache.ScoreCache
import com.barrybecker4.game.twoplayer.go.board.analysis.BoardEvaluator
import com.barrybecker4.game.twoplayer.go.board.move.{GoMove, GoMoveGenerator, UrgentMoveGenerator}
import com.barrybecker4.game.twoplayer.go.board.update.DeadStoneUpdater
import com.barrybecker4.optimization.parameter.ParameterArray
import scala.jdk.CollectionConverters._

/**
 * For searching go game's search space.
 *
 * @author Barry Becker
 */
class GoSearchable(board: GoBoard, players: PlayerList, cache: ScoreCache) 
  extends TwoPlayerSearchable[GoMove, GoBoard](board, players) {

  /** keeps track of dead stones. */
  private var deadStoneUpdater: DeadStoneUpdater = _
  private var boardEvaluator: BoardEvaluator = _

  init(cache)

  def this(searchable: GoSearchable) = {
    this(searchable.getBoard, searchable.players, searchable.getScoreCache)
  }

  override def copy(): GoSearchable = new GoSearchable(this)

  override def getBoard: GoBoard = board_

  def getScoreCache: ScoreCache = boardEvaluator.getCache

  private def init(cache: ScoreCache): Unit = {
    deadStoneUpdater = new DeadStoneUpdater(getBoard)
    boardEvaluator = new BoardEvaluator(getBoard, cache)
  }

  override def getProfiler: GoProfiler = GoProfiler.getInstance

  /**
   * Given a move determine whether the game is over.
   * If recordWin is true then the variables for player1/2HasWon can get set.
   * Sometimes, like when we are looking ahead, we do not want to set these.
   * The game is over if we have a resignation move, or the last two moves were passing moves.
   *
   * @param move the move to check
   * @param recordWin if true then the controller state will record wins
   * @return true if the game is over
   */
  override def done(move: GoMove, recordWin: Boolean): Boolean = {
    var gameOver = false

    if (move == null) {
      gameOver = true
    } else if (move.isResignationMove) {
      if (recordWin) {
        setWinner(!move.isPlayer1)
      }
      gameOver = true
    } else if (twoPasses(move)) {
      if (recordWin) {
        setWinner(getFinalScore(true) > getFinalScore(false))
      }
      gameOver = true
    }
    if (!gameOver) {
      // try normal handling
      gameOver = super.done(move, recordWin)
    }

    if (gameOver && recordWin) {
      doFinalBookKeeping()
    }

    gameOver
  }

  /**
   * Update final territory and number of dead stones.
   * Include this in calcWorth because we call updateTerritory which is under calcWorth for timing.
   */
  private def doFinalBookKeeping(): Unit = {
    getProfiler.startCalcWorth()
    boardEvaluator.updateTerritoryAtEndOfGame()

    //we should not call this twice
    if (getNumDeadStonesOnBoard(true) > 0 || getNumDeadStonesOnBoard(false) > 0) {
      GameContext.log(0, " Error: should not update life and death twice.")
    }
    // now that we are finally at the end of the game,
    // update the life and death of all the stones still on the board
    GameContext.log(1, "about to update life and death.")
    deadStoneUpdater.determineDeadStones()
    getProfiler.stopCalcWorth()
  }

  /**
   * Statically evaluate the board position.
   * @return statically evaluated value for the board.
   *   a positive value means that player1 has the advantage.
   *   A big negative value means a good move for p2.
   */
  override def worth(lastMove: GoMove, weights: ParameterArray): Int = {
    boardEvaluator.worth(lastMove, weights, getHashKey())
  }

  /**
   * get the number of black (player1=true) or white (player1=false) stones that were captured and removed.
   * @param player1sStones if true, get the captures for player1, else for player2.
   * @return num captures of the specified color
   */
  def getNumCaptures(player1sStones: Boolean): Int = {
    getBoard.getNumCaptures(player1sStones)
  }

  /**
   * @param move the move to play.
   */
  override def makeInternalMove(move: GoMove): Unit = {
    super.makeInternalMove(move)
    updateHashIfCaptures(move)
  }

  /**
   * takes back the most recent move.
   * @param move move to undo
   */
  override def undoInternalMove(move: GoMove): Unit = {
    super.undoInternalMove(move)
    updateHashIfCaptures(move)
  }

  /**
   * Whether we are removing captures from the board or adding them back, the operation is the same: XOR.
   */
  private def updateHashIfCaptures(goMove: GoMove): Unit = {
    if (goMove.getNumCaptures > 0) {
      goMove.getCaptures.asScala.foreach { pos =>
        hash.applyMove(pos.getLocation, getBoard.getStateIndex(pos))
      }
      // this is needed to disambiguate ko's and positions that have captures.
      if (goMove.isKo(getBoard) || goMove.getNumCaptures > 0) {
        hash.applyMoveNumber(getNumMoves() + goMove.getNumCaptures)
      }
    }
  }

  /**
   * @param move last move
   * @return true if last two moves were passing moves.
   */
  private def twoPasses(move: GoMove): Boolean = {
    if (move.isPassingMove && moveList.size > 2) {
      val secondToLast = moveList.get(moveList.size - 2)
      if (secondToLast.isPassingMove) {
        GameContext.log(0, s"Done: The last 2 moves were passes :$move, $secondToLast")
        return true
      }
    }
    false
  }

  /**
   * @param player1 if true, set player1 as the winner, else player2.
   */
  private def setWinner(player1: Boolean): Unit = {
    if (player1) {
      players.getPlayer1.setWon(true)
    } else {
      players.getPlayer2.setWon(true)
    }
  }

  /**
   * get a territory estimate for player1 or player2
   * When the game is over, this should return a precise value for the amount of territory
   * (not yet filled with captures).
   * So the estimate will be very rough at the beginning of the game, but should get better as more pieces are played.
   *
   * Since this can be called while we are processing, we return cached values in
   * those cases to avoid a ConcurrentModificationException.
   *
   * @param forPlayer1 if true, get the captures for player1, else for player2
   * @return estimate of the amount of territory the player has
   */
  def getTerritoryEstimate(forPlayer1: Boolean): Int = {
    val m = moveList.getLastMove
    if (m == null)
      return 0

    boardEvaluator.getTerritoryEstimate(forPlayer1, false)
  }

  /**
   * Call this at the end of the game when we need to try to get an accurate score.
   * @param forPlayer1 true if player one (black)
   * @return the actual score (each empty space counts as one)
   */
  def getFinalTerritory(forPlayer1: Boolean): Int = {
    boardEvaluator.getTerritoryEstimate(forPlayer1, true)
  }

  /**
   * Only valid after final bookkeeping has been done at the end of the game.
   * @param forPlayer1 player to get dead stones for.
   * @return number of dead stones of specified players color.
   */
  def getNumDeadStonesOnBoard(forPlayer1: Boolean): Int = {
    deadStoneUpdater.getNumDeadStonesOnBoard(forPlayer1)
  }

  /**
   * @param player1 if true, then the score for player one is returned else player2's score is returned
   * @return the score (larger is better regardless of player)
   */
  def getFinalScore(player1: Boolean): Double = {
    val numDead = getNumDeadStonesOnBoard(player1)
    val totalCaptures = numDead + getNumCaptures(!player1)
    val p1Territory = getFinalTerritory(player1)

    val side = if (player1) "black" else "white"
    GameContext.log(1, "----")
    GameContext.log(1, s"final score for $side")
    GameContext.log(2, s"getNumCaptures($side)=${getNumCaptures(player1)}")
    GameContext.log(2, s"num dead $side stones on board: $numDead")
    GameContext.log(2, s"getTerritory($side)=$p1Territory")
    GameContext.log(0, s"terr + totalEnemyCaptures=${p1Territory + totalCaptures}")
    p1Territory + totalCaptures
  }

  /**
   * @return any moves that take captures or get out of atari.
   */
  override def generateUrgentMoves(lastMove: GoMove, weights: ParameterArray): MoveList[GoMove] = {
    val generator = new UrgentMoveGenerator(getBoard)
    generator.generateUrgentMoves(generateMoves(lastMove, weights), lastMove)
  }

  /**
   * True if the specified move caused CRITICAL_GROUP_SIZE or more opponent pieces to become jeopardized
   * For go, if the specified move caused a sufficiently large group of stones to become in atari, then we return true.
   *
   * @return true if the last move created a big change in the score
   */
  override def inJeopardy(move: GoMove, weights: ParameterArray): Boolean = {
    UrgentMoveGenerator.inJeopardy(move, getBoard)
  }

  /**
   * generate all good next moves (statically evaluated)
   */
  override def generateMoves(lastMove: GoMove, weights: ParameterArray): MoveList[GoMove] = {
    val generator = new GoMoveGenerator(this)
    generator.generateEvaluatedMoves(lastMove, weights)
  }
}
