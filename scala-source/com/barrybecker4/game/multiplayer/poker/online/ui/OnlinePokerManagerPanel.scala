/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.online.ui

import com.barrybecker4.game.common.GameViewModel
import com.barrybecker4.game.common.ui.dialogs.{GameOptionsDialog, GameStartListener}
import com.barrybecker4.game.multiplayer.common.online.ui.{MultiPlayerOnlineGameTablesTable, MultiPlayerOnlineManagerPanel}
import com.barrybecker4.game.multiplayer.poker.ui.dialog.PokerOptionsDialog
import com.barrybecker4.ui.table.TableButtonListener

class OnlinePokerManagerPanel(viewer: GameViewModel, dlg: GameStartListener) 
    extends MultiPlayerOnlineManagerPanel(viewer, dlg) {

  override def createOnlineGamesTable(tableButtonListener: TableButtonListener): MultiPlayerOnlineGameTablesTable = 
    PokerOnlineGameTablesTable(tableButtonListener)

  override def createNewGameTableDialog(): GameOptionsDialog = 
    PokerOptionsDialog(null, controller_)
}
