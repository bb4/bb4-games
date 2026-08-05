// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.ui.panel

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.ui.components.NumberInput
import javax.swing._
import java.awt.{BorderLayout, Component}

/**
 * Panel to show params for a game board that has a rectilinear grid.
 *
 * @author Barry Becker
 */
class GridBoardParamPanel(initialNumRows: Int, initialNumCols: Int, customConfigPanel: JPanel) 
  extends JPanel {

  var rowSizeField_ : NumberInput = _
  var colSizeField_ : NumberInput = _

  setLayout(new BorderLayout())
  val p = new JPanel(true)
  p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS))
  p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Board Configuration"))
  
  val label = new JLabel(GameContext.getLabel("BOARD_SIZE") + ": ")
  label.setAlignmentX(Component.LEFT_ALIGNMENT)
  p.add(label)

  rowSizeField_ = new NumberInput(GameContext.getLabel("NUMBER_OF_ROWS"), initialNumRows)
  colSizeField_ = new NumberInput(GameContext.getLabel("NUMBER_OF_COLS"), initialNumCols)

  rowSizeField_.setAlignmentX(Component.LEFT_ALIGNMENT)
  colSizeField_.setAlignmentX(Component.LEFT_ALIGNMENT)
  p.add(rowSizeField_)
  p.add(colSizeField_)

  if (customConfigPanel != null)
    p.add(customConfigPanel)

  add(p, BorderLayout.CENTER)
  add(new JPanel(true), BorderLayout.EAST)

  def getRowSize: Int = rowSizeField_.getIntValue

  def getColSize: Int = colSizeField_.getIntValue
}
