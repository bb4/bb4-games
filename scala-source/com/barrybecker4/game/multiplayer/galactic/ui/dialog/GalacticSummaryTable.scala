/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.ui.dialog

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.player.PlayerList
import com.barrybecker4.game.multiplayer.common.ui.SummaryTable
import com.barrybecker4.game.multiplayer.galactic.Galaxy
import com.barrybecker4.game.multiplayer.galactic.player.GalacticPlayer

/**
 * Shows a list of the players stats at the end of the game.
 * None of the cells are editable.
 *
 * @see GalacticPlayer
 * @author Barry Becker
 */
class GalacticSummaryTable(players: PlayerList) extends SummaryTable(players, GalacticSummaryTable.COLUMN_NAMES):

  override def addRow(player: Any): Unit =
    val p = player.asInstanceOf[GalacticPlayer]
    val d = new Array[AnyRef](getNumColumns)
    val planets = Galaxy.getPlanets(p)

    d(NAME_INDEX) = p.getName
    d(COLOR_INDEX) = p.getColor
    d(GalacticSummaryTable.NUM_PLANETS_INDEX) = Int.box(planets.size())
    d(GalacticSummaryTable.SHIPS_INDEX) = Int.box(p.getTotalNumShips)
    d(GalacticSummaryTable.PRODUCTION_INDEX) = Int.box(p.getTotalProductionCapacity)
    getPlayerModel.addRow(d)

object GalacticSummaryTable:
  val NUM_PLANETS_INDEX = 2
  val SHIPS_INDEX = 3
  val PRODUCTION_INDEX = 4

  private val NUM_PLANETS = GameContext.getLabel("NUM_PLANETS")
  private val NUM_SHIPS = GameContext.getLabel("NUM_SHIPS")
  private val PRODUCTION = GameContext.getLabel("PRODUCTION")

  val COLUMN_NAMES = Array[String](
    GameContext.getLabel("NAME"),
    GameContext.getLabel("COLOR"),
    NUM_PLANETS,
    NUM_SHIPS,
    PRODUCTION
  )
