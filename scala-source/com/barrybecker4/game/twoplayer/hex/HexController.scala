package com.barrybecker4.game.twoplayer.hex

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.common.player.{PlayerList, PlayerOptions}
import com.barrybecker4.game.twoplayer.common.search.Searchable
import com.barrybecker4.game.twoplayer.common.{TwoPlayerController, TwoPlayerMove, TwoPlayerOptions}

import java.awt.Color

/**
 * Defines everything the computer needs to know to play Hex.
 */
class HexController() extends TwoPlayerController[TwoPlayerMove, HexBoard]:

  initializeData()

  override def createBoard: HexBoard =
    HexBoard()

  override def createOptions(): TwoPlayerOptions =
    TwoPlayerOptions()

  override def createPlayerOptions(playerName: String, color: Color): PlayerOptions =
    HexPlayerOptions(playerName, color)

  /**
   *  this gets the game specific patterns and weights
   */
  override def initializeData(): Unit =
    weights_ = HexWeights()

  override def getSearchable(): HexSearchable =
    super.getSearchable().asInstanceOf[HexSearchable]

  override def createSearchable(board: HexBoard, players: PlayerList): Searchable[TwoPlayerMove, HexBoard] =
    HexSearchable(board, players)

  override def computerMovesFirst(): Unit =
    val board = getBoard
    val col = (GameContext.random().nextFloat() * board.getNumRows + 1).toInt
    val row = (GameContext.random().nextFloat() * board.getNumRows + 1).toInt
    val move = TwoPlayerMove.createMove(row, col, 0, GamePiece(true))
    makeMove(move)
