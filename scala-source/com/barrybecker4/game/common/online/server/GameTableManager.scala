// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.online.server

import com.barrybecker4.game.common.online.{OnlineGameTable, OnlineGameTableList}
import com.barrybecker4.game.common.player.Player

import scala.jdk.CollectionConverters._

/**
 * Handles operations on the list of online game tables.
 *
 * @author Barry Becker
 */
class GameTableManager {

  /** Maintain a list of game tables. */
  private val tables_ = new OnlineGameTableList()

  def getTables: OnlineGameTableList = tables_

  def removePlayer(player: String): Unit = {
    tables_.removePlayer(player)
  }

  def removeTable(table: OnlineGameTable): Unit = {
    tables_.remove(table)
    assert(tables_.isEmpty, "Game table removed on server. Should have been 0 now, but was " + tables_)
  }

  /**
   * If the table we are adding has the same name as an existing table change it to something unique.
   * If the player at this new table is already sitting at another table,
   * remove him from the other tables, and delete those other tables if no one else is there.
   * @param table the table to add
   */
  def addTable(table: OnlineGameTable): Unit = {
    val uniqueName = verifyUniqueName(table.getName)
    table.setName(uniqueName)
    assert(
      table.getPlayers.size >= 1,
      "It is expected that when you add a new table there is at least one player at it" +
        " (exactly one human owner and 0 or more robots).")
    tables_.removePlayer(table.getOwner)
    tables_.add(table)
  }

  /**
   * Get the most recently added human player from the table and have them join the table with the same name.
   * If the player at this new table is already sitting at another table,
   * remove him from the other tables(s) and delete those other tables (if no one else is there).
   */
  def joinTable(table: OnlineGameTable): Unit = {
    val p = table.getNewestHumanPlayer

    tables_.removePlayer(p)
    tables_.join(table.getName, p)
  }

  /**
   * Change the players name from oldName to newName.
   */
  def changeName(oldName: String, newName: String): Unit = {
    tables_.changeName(oldName, newName)
  }

  /**
   * @param name the name to verify uniqueness of.
   * @return a unique name if not unique already
   */
  private def verifyUniqueName(name: String): String = {
    var ct = 0
    tables_.iterator().asScala.foreach { t =>
      if (t.getName.indexOf(name + '_') == 0) {
        ct += 1
      }
    }
    name + '_' + ct
  }
}
