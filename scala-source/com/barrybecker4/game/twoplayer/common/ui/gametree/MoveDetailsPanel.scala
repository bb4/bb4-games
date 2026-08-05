/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui.gametree

import com.barrybecker4.common.format.FormatUtil
import com.barrybecker4.game.common.player.PlayerList
import com.barrybecker4.game.twoplayer.common.TwoPlayerController
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode
import com.barrybecker4.game.twoplayer.common.ui.AbstractTwoPlayerBoardViewer
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPieceRenderer
import com.barrybecker4.ui.util.ColorUtil

import javax.swing.*
import javax.swing.border.EtchedBorder
import java.awt.*

/**
 * Presents info to the user about the currently moused over node in the tree.
 *
 * @author Barry Becker
 */
class MoveDetailsPanel extends JPanel:

  private var infoLabel: JLabel = _
  private var leafDetailLabel: JLabel = _

  setLayout(BorderLayout())
  setBorder(
    BorderFactory.createCompoundBorder(
      BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
      BorderFactory.createEmptyBorder(5, 5, 5, 5)
    )
  )

  infoLabel = JLabel()
  leafDetailLabel = JLabel()
  add(panelWrap(infoLabel), BorderLayout.CENTER)
  add(panelWrap(leafDetailLabel), BorderLayout.EAST)

  private def panelWrap(label: JLabel): JPanel =
    val panel = JPanel()
    panel.setLayout(BoxLayout(panel, BoxLayout.Y_AXIS))
    panel.setBorder(BorderFactory.createEtchedBorder())
    panel.add(label)
    panel.add(Box.createVerticalGlue())
    panel

  /**
   *  Display all the relevant info for the moused over move.
   */
  def setText(viewer: AbstractTwoPlayerBoardViewer[?, ?], m: TwoPlayerMove, lastNode: SearchTreeNode): Unit =
    val renderer = viewer.getPieceRenderer.asInstanceOf[TwoPlayerPieceRenderer]
    val controller = viewer.getController.asInstanceOf[TwoPlayerController[?, ?]]
    val passSuffix = if m.isPassingMove then " (Pass)" else ""
    var entity = "Human's move"
    val numKids = if lastNode.getChildMoves == null then 0 else lastNode.getChildMoves.length

    var c = renderer.getPlayer2Color()
    if m.isPlayer1 then
      c = renderer.getPlayer1Color()
    val players = controller.getPlayers
    if (m.isPlayer1 && !players.getPlayer1.isHuman) || (!m.isPlayer1 && !players.getPlayer2.isHuman) then
      entity = "Computer's move"

    val sBuf = StringBuilder("<html>")
    sBuf.append("<font size=\"+1\" color=\"").append(ColorUtil.getHTMLColorFromColor(c))
      .append("\" bgcolor=\"#99AA99>\">").append(entity).append(passSuffix).append("</font><br>")
    sBuf.append("Static value = ").append(FormatUtil.formatNumber(m.getValue)).append("<br>")

    sBuf.append(lastNode.toString)

    sBuf.append("<br>Number of descendants = ").append(numKids).append("<br>")
    if m.isUrgent then
      sBuf.append("<font color=\"#FF6611\">Urgent move!</font>")
    sBuf.append("</html>")
    infoLabel.setText(sBuf.toString)

    setLeafDetail(m, numKids == 0)

  private def setLeafDetail(m: TwoPlayerMove, isLeaf: Boolean): Unit =
    val buf = StringBuilder("")
    if isLeaf then
      if m.getScoreDescription != null then
        buf.append(m.getScoreDescription)
    else
      buf.append("Not a leaf")
    leafDetailLabel.setText(buf.toString)
