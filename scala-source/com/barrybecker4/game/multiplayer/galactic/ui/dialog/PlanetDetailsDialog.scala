/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.ui.dialog

import com.barrybecker4.game.common.ui.viewer.GameBoardViewer
import com.barrybecker4.ui.dialogs.AbstractDialog

import java.awt.Component
import javax.swing.{JComponent, JPanel}

/**
 * Show summary information about all planets (or maybe just the ones that you own).
 *
 * @author Barry Becker
 */
final class PlanetDetailsDialog(parent: Component, boardViewer: GameBoardViewer[?, ?])
  extends AbstractDialog(parent):

  boardViewer.getController
  showContent()

  override def createDialogContent: JComponent = JPanel(true)
