/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.ui.dialog

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.player.{Player, PlayerList}
import com.barrybecker4.game.multiplayer.common.MultiGamePlayer
import com.barrybecker4.game.multiplayer.common.ui.{PlayerTable, PlayerTable as PT}
import com.barrybecker4.game.multiplayer.galactic.{Galaxy, Planet}
import com.barrybecker4.game.multiplayer.galactic.player.GalacticPlayer
import com.barrybecker4.ui.table.TableColumnMeta

import java.awt.Color
import javax.swing.ImageIcon
import javax.swing.event.{TableModelEvent, TableModelListener}

/**
 * GalacticPlayerTable contains a list of players.
 * All the cells are editable.
 *
 * @author Barry Becker
 */
class GalacticPlayerTable(players: PlayerList)
  extends PlayerTable(players, GalacticPlayerTable.galacticColumnNames)
  with TableModelListener:

  table.getModel.addTableModelListener(this)
  setRowHeight(GalacticPlayerTable.ROW_HEIGHT)

  override def updateColumnMeta(columnMeta: Array[TableColumnMeta]): Unit =
    columnMeta(GalacticPlayerTable.ICON_INDEX).setPreferredWidth(48)
    columnMeta(GalacticPlayerTable.HOME_PLANET_INDEX).setPreferredWidth(100)
    columnMeta(GalacticPlayerTable.NUM_SHIPS_INDEX).setPreferredWidth(100)
    columnMeta(GalacticPlayerTable.PRODUCTION_INDEX).setPreferredWidth(100)
    super.updateColumnMeta(columnMeta)

  override def getPlayers: PlayerList =
    val model = table.getModel
    val nRows = model.getRowCount
    val result = new PlayerList()
    for i <- 0 until nRows do
      val planetName = model.getValueAt(i, GalacticPlayerTable.HOME_PLANET_INDEX).asInstanceOf[Character]
      val planet = Galaxy.getPlanet(planetName)
      planet.setProductionCapacity(model.getValueAt(i, GalacticPlayerTable.PRODUCTION_INDEX).asInstanceOf[Integer].intValue())
      planet.setNumShips(model.getValueAt(i, GalacticPlayerTable.NUM_SHIPS_INDEX).asInstanceOf[Integer].intValue())
      val icon = model.getValueAt(i, GalacticPlayerTable.ICON_INDEX).asInstanceOf[ImageIcon]
      result.add(GalacticPlayer.createGalacticPlayer(
        model.getValueAt(i, PT.NAME_INDEX).asInstanceOf[String],
        planet,
        model.getValueAt(i, PT.COLOR_INDEX).asInstanceOf[Color],
        model.getValueAt(i, PT.HUMAN_INDEX).asInstanceOf[Boolean],
        icon))
    result

  override def addRow(player: Any): Unit =
    val p = player.asInstanceOf[GalacticPlayer]
    val d = new Array[AnyRef](getNumColumns)
    d(PT.NAME_INDEX) = p.getName
    d(PT.COLOR_INDEX) = p.getColor
    d(GalacticPlayerTable.ICON_INDEX) = p.getIcon
    d(GalacticPlayerTable.HOME_PLANET_INDEX) = Character.valueOf(p.getHomePlanet.getName)
    d(GalacticPlayerTable.NUM_SHIPS_INDEX) = Int.box(p.getHomePlanet.getNumShips)
    d(GalacticPlayerTable.PRODUCTION_INDEX) = Int.box(p.getHomePlanet.getProductionCapacity)
    d(PT.HUMAN_INDEX) = Boolean.box(p.isHuman)
    getPlayerModel.addRow(d)

  override def createPlayer(): Player =
    val ct = table.getRowCount
    val planet = Planet(
      ('A' + ct).toChar,
      GalacticPlayer.DEFAULT_NUM_SHIPS, 10, new ByteLocation(0, 0))
    val newColor = MultiGamePlayer.getNewPlayerColor(getPlayers)
    val player = GalacticPlayer.createGalacticPlayer(
      "Admiral " + (ct + 1), planet, newColor, true)
    planet.setOwner(player.asInstanceOf[GalacticPlayer])
    player

  override def tableChanged(e: TableModelEvent): Unit =
    GameContext.log(0, "table changed " + e.getFirstRow + " col=" + e.getColumn)
    if e.getColumn == PT.HUMAN_INDEX then
      val row = e.getFirstRow
      val m = table.getModel
      val isHuman = m.getValueAt(row, PT.HUMAN_INDEX).asInstanceOf[Boolean]
      val c = m.getValueAt(row, GalacticPlayerTable.HOME_PLANET_INDEX).asInstanceOf[Character]
      val p = Galaxy.getPlanet(c)
      val np = GalacticPlayer.createGalacticPlayer("", p, Color.WHITE, isHuman)
      m.setValueAt(np.getIcon, row, GalacticPlayerTable.ICON_INDEX)

object GalacticPlayerTable:
  private val ICON_INDEX = 3
  private val HOME_PLANET_INDEX = 4
  private val NUM_SHIPS_INDEX = 5
  private val PRODUCTION_INDEX = 6

  private val ICON = GameContext.getLabel("ICON")
  private val HOME_PLANET = GameContext.getLabel("HOME_PLANET")
  private val NUM_SHIPS = GameContext.getLabel("NUM_SHIPS")
  private val PRODUCTION = GameContext.getLabel("PRODUCTION")

  private val ROW_HEIGHT = 30

  private val galacticColumnNames = Array[String](
    PT.NAME,
    PT.COLOR,
    PT.HUMAN,
    ICON,
    HOME_PLANET,
    NUM_SHIPS,
    PRODUCTION
  )
