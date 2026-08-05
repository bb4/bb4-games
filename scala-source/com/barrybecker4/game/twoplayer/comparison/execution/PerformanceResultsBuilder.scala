// Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.execution

import com.barrybecker4.game.common.{GameContext, GameViewModel}
import com.barrybecker4.game.common.persistence.GameExporter
import com.barrybecker4.game.common.player.{Player, PlayerList}
import com.barrybecker4.game.twoplayer.common.{TwoPlayerBoard, TwoPlayerController, TwoPlayerMove, TwoPlayerPlayerOptions, TwoPlayerViewModel}
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions
import com.barrybecker4.game.twoplayer.comparison.model.{PerformanceResults, PerformanceResultsPair}
import com.barrybecker4.game.twoplayer.comparison.model.config.{SearchOptionsConfig, SearchOptionsConfigList}
import com.barrybecker4.ui.util.GUIUtil

import javax.swing.JComponent
import java.awt.image.BufferedImage

/**
 * Creates performance results given the controller and list of configuration options to pair off
 * against each other.
 * @author Barry Becker
 */
class PerformanceResultsBuilder(controller: TwoPlayerController[?, ?], optionsList: SearchOptionsConfigList) {

  private val FILE_SIDE_DELIM = "_vs_"

  def getResultsForComparison(i: Int, j: Int): PerformanceResultsPair = {
    controller.reset()
    val players = controller.getPlayers
    controller.getOptions.setShowGameOverDialog(false)

    val player1 = players.getPlayer1
    val player2 = players.getPlayer2
    player1.setHuman(false)
    player2.setHuman(false)
    val config1 = optionsList.get(i)
    val config2 = optionsList.get(j)
    player1.getOptions.asInstanceOf[TwoPlayerPlayerOptions[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]]]
      .setSearchOptions(config1.getSearchOptions.asInstanceOf[SearchOptions[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]]])
    player2.getOptions.asInstanceOf[TwoPlayerPlayerOptions[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]]]
      .setSearchOptions(config2.getSearchOptions.asInstanceOf[SearchOptions[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]]])
    val description = config1.getName + FILE_SIDE_DELIM + config2.getName

    System.out.println("(" + i + ", " + j + ") round 1  starts:" + config1.getSearchOptions.getSearchStrategyMethod)
    val p1FirstResults = getResultsForRound(player1, player2, description)
    System.out.println("(" + i + ", " + j + ") round 2  starts:" + config2.getSearchOptions.getSearchStrategyMethod)
    val p2FirstResults = getResultsForRound(player2, player1, description)

    new PerformanceResultsPair(p1FirstResults, p2FirstResults)
  }

  private def getResultsForRound(player1: Player, player2: Player, description: String): PerformanceResults = {
    val startTime = System.currentTimeMillis()
    GameContext.setRandomSeed(1)

    val players = controller.getPlayers
    players.set(0, player1)
    players.set(1, player2)

    controller.getViewer.asInstanceOf[TwoPlayerViewModel[?, ?]].doComputerVsComputerGame()

    assert(controller.isDone)
    System.out.println("******** game is done = " + controller.isDone + " ******")
    val strengthOfWin = controller.getStrengthOfWin
    val numMoves = controller.getNumMoves
    val finalImage = GUIUtil.getSnapshot(controller.getViewer.asInstanceOf[JComponent])

    val exporter = controller.getExporter

    val elapsedMillis = System.currentTimeMillis() - startTime

    val isTie = !players.anyPlayerWon
    val player1Won = players.getWinningPlayer == player1
    controller.reset()

    new PerformanceResults(player1Won, isTie, strengthOfWin, numMoves, elapsedMillis, finalImage,
      exporter, description)
  }
}
