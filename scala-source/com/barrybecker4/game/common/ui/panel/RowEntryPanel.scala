// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.ui.panel

import javax.swing._
import java.awt.Dimension

/**
 *  A row entry panel in a game info panel.
 *
 *  @author Barry Becker
 */
class RowEntryPanel(firstComp: JComponent, secondComp: JComponent) extends JPanel {

  setOpaque(false)
  setLayout(new BoxLayout(this, BoxLayout.X_AXIS))
  setMaximumSize(new Dimension(300, 16))
  
  if (firstComp != null) {
    add(firstComp)
  }
  if (secondComp != null) {
    add(secondComp)
  }
}
