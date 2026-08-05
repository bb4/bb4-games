/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial

import com.barrybecker4.game.common.{GameContext, GameOptions}
import com.barrybecker4.game.common.player.PlayerList
import com.barrybecker4.game.multiplayer.common.{MultiGameController, MultiGamePlayer}
import com.barrybecker4.game.multiplayer.trivial.player.{TrivialPlayer, TrivialRobotPlayer}

import java.util.{Arrays, List as JList}

/**
 * Defines everything the computer needs to know to play Trivial game.
 * In this simplistic game, players either keep their card hidden or revealed
 * when it is their turn. When all cards are revealed the highest value card
 * wins. The purpose is to test multi-player game without complex rules.
 *
 * @author Barry Becker
 */
class TrivialController extends MultiGameController {

  this.initializeData()

  override def createBoard: TrivialTable =
    createTable(TrivialController.DEFAULT_NUM_ROWS, TrivialController.DEFAULT_NUM_COLS)

  /**
   *  Construct the game controller given an initial board size
   */
  override def createTable(nrows: Int, ncols: Int): TrivialTable =
    new TrivialTable(nrows, ncols)

  override def createOptions: GameOptions =
    new TrivialOptions()

  /**
   * By default we start with one human and one robot player.
   * Just initialize the first time.
   * After that, they can change manually to get different players.
   */
  override def initPlayers(): Unit =
    if this.getPlayers == null then
      val players = new PlayerList()

      players.add(TrivialPlayer.createTrivialPlayer("Player 1",
                        MultiGamePlayer.getNewPlayerColor(players), true))

      players.add(TrivialPlayer.createTrivialPlayer("Player 2",
                        MultiGamePlayer.getNewPlayerColor(players), false))
      players.get(1).setName(players.get(1).getName + '(' + 
        players.get(1).asInstanceOf[TrivialRobotPlayer].getType + ')')
      this.setPlayers(players)

    this.currentPlayerIndex = 0
    this.getBoard.asInstanceOf[TrivialTable].initPlayers(this.getPlayers)

  override def getServerPort: Int = TrivialController.TRIVIAL_SERVER_PORT

  /**
   * Game is over when only one player has enough money left to play
   *
   * @return true if the game is over.
   */
  override def isDone: Boolean =
    var numPlayersStillHidden = 0
    val it = this.getPlayers.iterator()
    while it.hasNext do
      val p = it.next()
      val tp = p.getActualPlayer.asInstanceOf[TrivialPlayer]
      if !tp.isRevealed then
        numPlayersStillHidden += 1

    numPlayersStillHidden == 0

  /**
   * @return the player with the best Trivial hand
   */
  override def determineWinners(): JList[? <: MultiGamePlayer] =
    val players = this.getPlayers
    var winner: TrivialPlayer = null
    var maxValue = -1

    val it = players.iterator()
    while it.hasNext do
      val player = it.next()
      val p = player.asInstanceOf[TrivialPlayer]
      if p.getValue > maxValue then
        maxValue = p.getValue
        winner = p

    Arrays.asList(winner)

  /**
   * make it the next players turn
   * @return the index of the next player
   */
  override def advanceToNextPlayerIndex(): Int =
    this.playIndex += 1
    var player = this.getPlayer(this.currentPlayerIndex).getActualPlayer
    
    while
      this.currentPlayerIndex = (this.currentPlayerIndex + 1) % this.getPlayers.size
      player = this.getPlayer(this.currentPlayerIndex).getActualPlayer
      GameContext.log(0, "currentPlayerIndex=" + this.currentPlayerIndex)
      player.asInstanceOf[TrivialPlayer].isRevealed
    do ()

    this.currentPlayerIndex
}

object TrivialController:
  private val DEFAULT_NUM_ROWS = 32
  private val DEFAULT_NUM_COLS = 32
  private val TRIVIAL_SERVER_PORT = 4447
