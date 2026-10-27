/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.common.{GameContext, GameOptions}
import com.barrybecker4.game.common.player.{Player, PlayerList}
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer
import com.barrybecker4.game.multiplayer.common.{MultiGameController, MultiGamePlayer}
import com.barrybecker4.game.multiplayer.galactic.player.GalacticPlayer
import com.barrybecker4.game.multiplayer.galactic.ui.GalaxyViewer

import java.util.{ArrayList, List as JList}

class GalacticController
  extends MultiGameController[GalacticTurn, Galaxy](
    GalacticController.DEFAULT_NUM_ROWS, GalacticController.DEFAULT_NUM_COLS):

  override def createTable(nrows: Int, ncols: Int): Galaxy = new Galaxy(nrows, ncols)

  override def initPlayers(): Unit =
    if getPlayers == null then
      val players = new PlayerList()
      var homePlanet = new Planet('A', GalacticPlayer.DEFAULT_NUM_SHIPS, 10, new ByteLocation(5, 5))
      players.add(GalacticPlayer.createGalacticPlayer(
        "Admiral 1", homePlanet, MultiGamePlayer.getNewPlayerColor(players), true))
      homePlanet.setOwner(players.get(0).asInstanceOf[GalacticPlayer])

      homePlanet = new Planet('B', GalacticPlayer.DEFAULT_NUM_SHIPS, 10, new ByteLocation(10, 10))
      players.add(GalacticPlayer.createGalacticPlayer(
        "Admiral 2", homePlanet, MultiGamePlayer.getNewPlayerColor(players), false))
      homePlanet.setOwner(players.get(1).asInstanceOf[GalacticPlayer])
      setPlayers(players)

    currentPlayerIndex = 0
    getBoard.initPlanets(getPlayers, getOptions.asInstanceOf[GalacticOptions])

  def getNumberOfYearsRemaining: Int =
    val m = getLastMove
    getOptions.asInstanceOf[GalacticOptions].getMaxYearsToPlay -
      (if m != null then getNumMoves else 0) - 2

  override def isOnlinePlayAvailable: Boolean = false

  override def isDone: Boolean =
    if getLastMove == null then return false
    val numMoves = getNumMoves
    System.out.println(s"num moves = $numMoves")
    if (numMoves + 2) >= getOptions.asInstanceOf[GalacticOptions].getMaxYearsToPlay then
      return true
    Galaxy.allPlanetsOwnedByOnePlayer()

  override def determineWinners(): JList[? <: MultiGamePlayer] =
    var maxCriteria = -1.0
    val scorer = new Scorer()
    val pit = getPlayers.iterator()
    while pit.hasNext do
      val criteria = scorer.score(pit.next().asInstanceOf[GalacticPlayer])
      if criteria > maxCriteria then maxCriteria = criteria

    val winners = new ArrayList[GalacticPlayer]()
    val pit2 = getPlayers.iterator()
    while pit2.hasNext do
      val player = pit2.next().asInstanceOf[GalacticPlayer]
      if scorer.score(player) == maxCriteria then winners.add(player)
    winners

  override def doAdvanceToNextPlayer(): Unit =
    val gviewer = getViewer.asInstanceOf[GalaxyViewer]
    if isDone then
      GameContext.log(1, "advanceToNextPlayer done")
      getViewer.asInstanceOf[GameBoardViewer[GalacticTurn, ?]]
        .sendGameChangedEvent(null.asInstanceOf[GalacticTurn])

    advanceToNextPlayerIndex()

    if getCurrentPlayer == getPlayers.getFirstPlayer then
      val gmove = gviewer.createMove(null)
      makeMove(gmove)

    if !getCurrentPlayer.isHuman then
      gviewer.doComputerMove(getCurrentPlayer)

    getViewer.asInstanceOf[GameBoardViewer[GalacticTurn, ?]]
      .sendGameChangedEvent(null.asInstanceOf[GalacticTurn])


  override def advanceToNextPlayerIndex(): Int =
    currentPlayerIndex = (currentPlayerIndex + 1) % getPlayers.size
    currentPlayerIndex

  override def createOptions: GameOptions = new GalacticOptions()

object GalacticController:
  private val DEFAULT_NUM_ROWS = 16
  private val DEFAULT_NUM_COLS = 16
