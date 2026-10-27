/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.board.{Board, GamePiece}
import com.barrybecker4.game.common.player.{PlayerList, PlayerOptions}
import com.barrybecker4.game.twoplayer.common.{TwoPlayerController, TwoPlayerMove, TwoPlayerOptions}
import com.barrybecker4.game.twoplayer.gomoku.pattern.{GoMokuPatterns, GoMokuWeights}

import java.awt.{Color, Dimension}

/**
 * Defines everything the computer needs to know to play GoMoku.
 *
 * @author Barry Becker
 */
class GoMokuController[B <: GoMokuBoard] extends TwoPlayerController[TwoPlayerMove, B] {

  private var size: Dimension = new Dimension(GoMokuController.DEFAULT_NUM_ROWS, GoMokuController.DEFAULT_NUM_ROWS)

  initializeData()

  def this(nrows: Int, ncols: Int) = {
    this()
    size = new Dimension(nrows, ncols)
    initializeData()
  }

  override def createBoard: B = {
    new GoMokuBoard(size.width, size.height).asInstanceOf[B]
  }

  override def createOptions(): TwoPlayerOptions = {
    new TwoPlayerOptions()
  }

  override def createPlayerOptions(playerName: String, color: Color): PlayerOptions = {
    new GoMokuPlayerOptions(playerName, color)
  }

  /**
   * This gets the gomoku specific patterns and weights
   */
  override def initializeData(): Unit = {
    weights_ = new GoMokuWeights()
  }

  /**
   * the first move of the game (made by the computer)
   */
  override def computerMovesFirst(): Unit = {
    val delta = getWinRunLength - 1
    val board = getBoard
    val col = (GameContext.random().nextFloat() * (board.getNumCols - 2 * delta) + delta + 1).toInt
    val row = (GameContext.random().nextFloat() * (board.getNumRows - 2 * delta) + delta + 1).toInt
    val move = TwoPlayerMove.createMove(row, col, 0, new GamePiece(true))
    makeMove(move)
  }

  def getWinRunLength: Int = {
    GoMokuPatterns.WIN_RUN_LENGTH
  }

  override def getSearchable(): GoMokuSearchable[B] = {
    super.getSearchable().asInstanceOf[GoMokuSearchable[B]]
  }

  override def createSearchable(board: B, players: PlayerList): GoMokuSearchable[B] = {
    new GoMokuSearchable[B](board, players)
  }
}

object GoMokuController {
  private val DEFAULT_NUM_ROWS = 20
}
