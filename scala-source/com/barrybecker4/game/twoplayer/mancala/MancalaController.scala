/* Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala

import com.barrybecker4.common.geometry.{ByteLocation, Location}
import com.barrybecker4.game.common.player.{PlayerList, PlayerOptions}
import com.barrybecker4.game.twoplayer.common.{TwoPlayerController, TwoPlayerOptions}
import com.barrybecker4.game.twoplayer.mancala.board.MancalaBoard
import com.barrybecker4.game.twoplayer.mancala.move.MancalaMove
import java.awt.{Color, Dimension}

/**
  * Defines everything the computer needs to know to play Mancala.
  *
  * @author Barry Becker
  */
class MancalaController(private val size: Dimension) extends TwoPlayerController[MancalaMove, MancalaBoard]:

  initializeData()

  /**
    * Constructor
    */
  def this() = this(new Dimension(MancalaController.DEFAULT_NUM_COLS, MancalaController.DEFAULT_NUM_ROWS))

  /**
    * Construct the mancala game controller given an initial board size
    * @param nrows number of rows on the board
    * @param ncols number of columns on the board.
    */
  def this(nrows: Int, ncols: Int) = this(new Dimension(nrows, ncols))

  override def createBoard: MancalaBoard =
    MancalaBoard(size.width)

  override def createOptions(): TwoPlayerOptions =
    TwoPlayerOptions()

  override def createPlayerOptions(playerName: String, color: Color): PlayerOptions =
    MancalaPlayerOptions(playerName, color)

  /**
    * This gets the mancala specific patterns and weights
    */
  override def initializeData(): Unit =
    weights_ = MancalaWeights()

  /**
    * If the players storage bin got the last stone, then they go again.
    * @param move the move to play.
    */
  override def makeMove(move: MancalaMove): Unit =
    getSearchable().makeInternalMove(move)
    val board = getBoard
    if !(getCurrentPlayer.isHuman && board.moveAgainAfterMove(move)) then
      player1sTurn = !move.isPlayer1

    if board.isEmpty then
      determineWinner(board)

  /**
    * After one players side is empty, the other players side is also cleared (with stones moved to their store)
    * now it's time to see who's store has more stones.
    * @param board the board
    */
  private def determineWinner(board: MancalaBoard): Unit =
    val p1Stones = board.getHomeBin(true).getNumStones
    val p2Stones = board.getHomeBin(false).getNumStones
    if p1Stones > p2Stones then
      getPlayers.get(0).setWon(true)
    else
      getPlayers.get(1).setWon(true)

  /**
    * the first move of the game (made by the computer)
    */
  override def computerMovesFirst(): Unit =
    val loc = ByteLocation(1, 1)
    val move = MancalaMove.createMove(true, loc, 0, getBoard.getBin(loc))
    makeMove(move)

  override def getSearchable(): MancalaSearchable =
    super.getSearchable().asInstanceOf[MancalaSearchable]

  override def createSearchable(board: MancalaBoard, players: PlayerList): MancalaSearchable =
    MancalaSearchable(board, players)

object MancalaController:
  private val DEFAULT_NUM_ROWS = 2
  private val DEFAULT_NUM_COLS = 8
