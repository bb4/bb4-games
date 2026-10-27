/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui.gametree

import com.barrybecker4.ui.util.ColorMap
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode
import com.barrybecker4.game.twoplayer.common.ui.AbstractTwoPlayerBoardViewer
import com.barrybecker4.ui.legend.ContinuousColorLegend

import javax.swing.*
import java.awt.*

/**
 * Contains the move details and color legend underneath
 *
 * @author Barry Becker
 */
final class MoveInfoPanel(colormap: ColorMap) extends JPanel:

  private val moveDetails = MoveDetailsPanel()

  private val colorLegend =
    ContinuousColorLegend("Relative Score for Player", colormap, true)

  this.setLayout(BorderLayout())
  this.add(moveDetails, BorderLayout.CENTER)
  this.add(colorLegend, BorderLayout.SOUTH)

  def setText(viewer: AbstractTwoPlayerBoardViewer[?, ?], m: TwoPlayerMove, lastNode: SearchTreeNode): Unit =
    moveDetails.setText(viewer, m, lastNode)
