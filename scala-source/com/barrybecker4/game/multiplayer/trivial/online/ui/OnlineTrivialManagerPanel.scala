/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial.online.ui

import com.barrybecker4.game.common.GameViewModel
import com.barrybecker4.game.common.ui.dialogs.{GameOptionsDialog, GameStartListener}
import com.barrybecker4.game.multiplayer.common.online.ui.{MultiPlayerOnlineGameTablesTable, MultiPlayerOnlineManagerPanel}
import com.barrybecker4.game.multiplayer.trivial.ui.dialog.TrivialOptionsDialog
import com.barrybecker4.ui.table.TableButtonListener

/**
 * Manage online player games.
 * Player can join no more than one table at a time.
 *
 * @author Barry Becker
 */
class OnlineTrivialManagerPanel(viewer: GameViewModel, dlg: GameStartListener)
  extends MultiPlayerOnlineManagerPanel(viewer, dlg):

  override def createOnlineGamesTable(tableButtonListener: TableButtonListener): MultiPlayerOnlineGameTablesTable =
    new TrivialOnlineGameTablesTable(tableButtonListener)

  /**
   * You are free to set your own options for the table that you are creating.
   */
  override def createNewGameTableDialog(): GameOptionsDialog =
    new TrivialOptionsDialog(null, controller_)
