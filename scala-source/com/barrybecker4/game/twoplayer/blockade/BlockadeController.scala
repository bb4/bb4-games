/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade

import com.barrybecker4.ca.dj.jigo.sgf.SGFException
import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.common.board.BoardPosition
import com.barrybecker4.game.common.player.{PlayerList, PlayerOptions}
import com.barrybecker4.game.twoplayer.blockade.board.{BlockadeBoard, BlockadeBoardPosition}
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove
import com.barrybecker4.game.twoplayer.blockade.persistence.{BlockadeGameExporter, BlockadeGameImporter}
import com.barrybecker4.game.twoplayer.common.{TwoPlayerController, TwoPlayerOptions}
import com.barrybecker4.game.twoplayer.common.persistence.TwoPlayerGameExporter
import com.barrybecker4.game.twoplayer.common.search.Searchable
import java.awt.Color
import java.io.InputStream

object BlockadeController:
  private val NUM_ROWS = 14
  private val NUM_COLS = 11

class BlockadeController extends TwoPlayerController[BlockadeMove, BlockadeBoard]:
  
  initializeData()

  override def createBoard: BlockadeBoard =
    BlockadeBoard(BlockadeController.NUM_ROWS, BlockadeController.NUM_COLS)

  override def initializeData(): Unit =
    weights_ = BlockadeWeights()

  override def createOptions(): TwoPlayerOptions =
    TwoPlayerOptions()

  override def createPlayerOptions(playerName: String, color: Color): PlayerOptions =
    BlockadePlayerOptions(playerName, color)

  override def computerMovesFirst(): Unit =
    val moveList = getSearchable().generateMoves(null, weights_.getPlayer1Weights)
    makeMove(moveList.getRandomMove)

  override def getStrengthOfWin: Int =
    if !getPlayers.anyPlayerWon then
      0
    else
      getSearchable().worth(getLastMove, weights_.getDefaultWeights)

  override def getExporter: TwoPlayerGameExporter[BlockadeMove, BlockadeBoard] =
    BlockadeGameExporter(this)

  override def restoreFromStream(iStream: InputStream): Unit =
    val importer = BlockadeGameImporter(this)
    importer.restoreFromStream(iStream)

  def getPossibleMoveList(position: BoardPosition): java.util.List[BlockadeMove] =
    getBoard.getPossibleMoveList(
      position.asInstanceOf[BlockadeBoardPosition],
      !position.getPiece.isOwnedByPlayer1
    )

  override def createSearchable(board: BlockadeBoard, players: PlayerList): Searchable[BlockadeMove, BlockadeBoard] =
    BlockadeSearchable(board, players)
