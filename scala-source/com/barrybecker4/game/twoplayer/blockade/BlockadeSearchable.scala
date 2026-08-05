/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade

import com.barrybecker4.game.common.{GameContext, MoveList}
import com.barrybecker4.game.common.board.{BoardPosition, GamePiece}
import com.barrybecker4.game.common.player.PlayerList
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard
import com.barrybecker4.game.twoplayer.blockade.board.move.{BlockadeMove, MoveGenerator}
import com.barrybecker4.game.twoplayer.blockade.board.path.PlayerPathLengths
import com.barrybecker4.game.twoplayer.common.TwoPlayerSearchable
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy
import com.barrybecker4.optimization.parameter.ParameterArray
import scala.jdk.CollectionConverters.*

class BlockadeSearchable(board: BlockadeBoard, players: PlayerList) 
    extends TwoPlayerSearchable[BlockadeMove, BlockadeBoard](board, players):

  private def this(searchable: BlockadeSearchable) =
    this(searchable.board_, searchable.players)

  override def copy(): BlockadeSearchable =
    BlockadeSearchable(this)

  override def getBoard: BlockadeBoard = board_

  override def worth(lastMove: BlockadeMove, weights: ParameterArray): Int =
    getProfiler.startCalcWorth()
    val player1Moved = lastMove.isPlayer1

    if checkForWin(player1Moved) then
      GameContext.log(1, "FOUND WIN!!!")
      return if player1Moved then SearchStrategy.WINNING_VALUE else -SearchStrategy.WINNING_VALUE

    val pathLengths = getBoard.findPlayerPathLengths()
    val worthValue = pathLengths.determineWorth(SearchStrategy.WINNING_VALUE, weights)
    getProfiler.stopCalcWorth()
    worthValue

  private def checkForWin(player1: Boolean): Boolean =
    val homes = getBoard.getPlayerHomes(!player1)
    for home <- homes do
      val p = home.getPiece
      if p != null && p.isOwnedByPlayer1 == player1 then
        return true
    false

  override def generateMoves(lastMove: BlockadeMove, weights: ParameterArray): MoveList[BlockadeMove] =
    getProfiler.startGenerateMoves()

    val generator = MoveGenerator(weights, getBoard)
    val moveList = generator.generateMoves(lastMove)

    val bestMoves = bestMoveFinder_.getBestMoves(moveList)

    getProfiler.stopGenerateMoves()
    bestMoves

  override def done(move: BlockadeMove, recordWin: Boolean): Boolean =
    if getNumMoves() > 0 && move == null then
      GameContext.log(1, "Game is over because there are no more moves.")
      return true

    val p1Won = checkForWin(player1 = true)
    val p2Won = checkForWin(player1 = false)

    if recordWin then
      if p1Won then
        players.getPlayer1.setWon(true)
      else if p2Won then
        players.getPlayer2.setWon(true)
    
    p1Won || p2Won

  override def generateUrgentMoves(lastMove: BlockadeMove, weights: ParameterArray): MoveList[BlockadeMove] =
    MoveList[BlockadeMove]()
