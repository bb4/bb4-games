/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.ui.dialog

import com.barrybecker4.game.common.ui.viewer.GameBoardViewer
import com.barrybecker4.ui.dialogs.AbstractDialog

import java.awt.Component
import javax.swing.{JComponent, JPanel}

/**
 * Draw stats about the players and planet ownership in the Galaxy.
 *
 * @author Barry Becker
 */
final class StatsDialog(parent: Component, boardViewer: GameBoardViewer[?, ?])
  extends AbstractDialog(parent):

  showContent()

  override def createDialogContent: JComponent = JPanel(true)
