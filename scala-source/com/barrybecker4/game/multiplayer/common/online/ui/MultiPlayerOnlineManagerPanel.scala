/* Copyright by Barry G. Becker, 2000-2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.common.online.ui

import com.barrybecker4.game.common.{GameContext, GameViewModel}
import com.barrybecker4.game.common.online.GameCommand
import com.barrybecker4.game.common.online.ui.OnlineGameManagerPanel
import com.barrybecker4.game.common.ui.dialogs.{GameOptionsDialog, GameStartListener}
import com.barrybecker4.game.multiplayer.common.MultiGameOptions
import com.barrybecker4.ui.components.GradientButton
import com.barrybecker4.ui.table.TableButtonListener

import javax.swing._
import java.awt._
import java.awt.event.{ActionEvent, ActionListener, KeyEvent, KeyListener}

/**
 * Manage multi-player online game tables.
 * Shows a list of the currently active "tables" to the user in a table.
 * Used only on the client to show list of active game tables.
 *
 * @author Barry Becker
 */
abstract class MultiPlayerOnlineManagerPanel(viewer: GameViewModel, dlg: GameStartListener)
  extends OnlineGameManagerPanel(viewer, dlg)
  with ActionListener with KeyListener with TableButtonListener {

  private val DEFAULT_NAME = "<name>"

  private var namePanel_ : NamePanel = null
  private var createTableButton_ : GradientButton = null
  private var tableManager: MultiPlayerTableManager = null
  private var createGameTableDialog_ : GameOptionsDialog = null
  private var oldName_ : String = null

  override def createPlayOnlinePanel(): JPanel = {
    createGameTableDialog_ = createNewGameTableDialog()

    val playOnlinePanel = new JPanel(new BorderLayout())
    playOnlinePanel.setBorder(
      BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(),
        GameContext.getLabel("ONLINE_DLG_TITLE")))

    val headerPanel = new JPanel(new BorderLayout())
    val buttonsPanel = new JPanel(new BorderLayout())

    createTableButton_ = new GradientButton(GameContext.getLabel("CREATE_TABLE"))
    createTableButton_.setToolTipText(GameContext.getLabel("CREATE_TABLE_TIP"))
    createTableButton_.addActionListener(this)
    buttonsPanel.add(createTableButton_, BorderLayout.EAST)

    namePanel_ = new NamePanel()
    namePanel_.addKeyListener(this)

    val bottomFill = new JPanel()
    bottomFill.setPreferredSize(new Dimension(100, 10))

    headerPanel.add(namePanel_, BorderLayout.CENTER)
    headerPanel.add(bottomFill, BorderLayout.SOUTH)
    headerPanel.add(buttonsPanel, BorderLayout.EAST)
    playOnlinePanel.add(headerPanel, BorderLayout.NORTH)

    val onlineGameTablesTable = createOnlineGamesTable(this)
    tableManager = new MultiPlayerTableManager(
      controller_.getServerConnection, onlineGameTablesTable, gameStartedListener_)

    playOnlinePanel.setPreferredSize(new Dimension(600, 300))
    playOnlinePanel.add(new JScrollPane(onlineGameTablesTable.getTable), BorderLayout.CENTER)

    if (controller_.getServerConnection.isConnected) {
      controller_.getServerConnection.enterRoom()
    }
    playOnlinePanel
  }

  def createOnlineGamesTable(tableButtonListener: TableButtonListener): MultiPlayerOnlineGameTablesTable

  def createNewGameTableDialog(): GameOptionsDialog

  override def handleServerUpdate(cmd: GameCommand): Boolean =
    tableManager.handleServerUpdate(cmd)

  override def actionPerformed(e: ActionEvent): Unit = {
    val source = e.getSource

    if (namePanel_.nameChecksOut) {
      if (source eq createTableButton_) {
        createNewGameTable()
      }
    }
  }

  override def tableButtonClicked(row: Int, col: Int, id: String): Unit = {
    if (namePanel_.nameChecksOut) {
      tableManager.joinDifferentTable(row)
    }
  }

  def createNewGameTable(): Unit = {
    val currentName = namePanel_.getCurrentName

    if (currentName == DEFAULT_NAME) {
      JOptionPane.showMessageDialog(
        this, "You need to select a name for yourself first.",
        "Information", JOptionPane.INFORMATION_MESSAGE)
      return
    }
    if (currentName != oldName_) {
      controller_.getServerConnection.nameChanged(oldName_, currentName)
      tableManager.setCurrentName(currentName)
      oldName_ = currentName
    }

    val canceled = createGameTableDialog_.showDialog

    if (!canceled) {
      val options = createGameTableDialog_.getOptions.asInstanceOf[MultiGameOptions]
      tableManager.createNewGameTable(options)
    }
  }

  override def closing(): Unit = {
    val name = namePanel_.getCurrentName
    System.out.println(name + " cancelled online dlg")
  }

  override def keyTyped(key: KeyEvent): Unit = {}
  override def keyPressed(key: KeyEvent): Unit = {}
  override def keyReleased(key: KeyEvent): Unit = {
    if (key.getKeyChar == '\n') {
      val currentName = namePanel_.getCurrentName
      controller_.getServerConnection.nameChanged(oldName_, currentName)
      tableManager.setCurrentName(currentName)
      oldName_ = currentName
    }
  }
}
