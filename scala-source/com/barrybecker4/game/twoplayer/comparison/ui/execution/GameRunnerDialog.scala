// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.ui.execution

import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPanel
import com.barrybecker4.ui.dialogs.AbstractDialog

import javax.swing.JComponent
import java.awt.Dimension

/**
 * Shows the two configurations playing against each other in the {@code TwoPlayerPanel}.
 * @author Barry Becker
 */
class GameRunnerDialog(panel: TwoPlayerPanel) extends AbstractDialog(panel) {

  private var gamePanel: TwoPlayerPanel = panel

  setMinimumSize(new Dimension(600, 500))
  showContent()

  override def createDialogContent: JComponent = gamePanel

  override def close(): Unit = {
    super.dispose()
    gamePanel = null
  }
}
