/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui.dialogs

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.ui.components.GradientButton
import com.barrybecker4.ui.dialogs.AbstractDialog
import com.barrybecker4.ui.util.GUIUtil

import javax.swing._
import javax.swing.border.{EmptyBorder, EtchedBorder}
import java.awt._
import java.awt.event.{ActionEvent, ActionListener}
import java.text.MessageFormat

/**
 * A dialog to show version information and instructions on how to play the game.
 *
 * @author Barry Becker
 */
final class HelpDialog(parent: Component, gameName: String, comments: String, text: String)
  extends AbstractDialog(parent) with ActionListener {

  private val VERSION = GameContext.getLabel("VERSION")
  private val COPYRIGHT = GameContext.getLabel("COPYRIGHT")
  private val FONT = new Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.PLAIN, 12)

  private val okButton_ = new GradientButton()

  private var gameName_ = gameName
  private var comments_ = comments
  private val overviewText_ = text

  showContent()

  /**
   * builds the ui.
   */
  override def createDialogContent: JComponent = {
    val arg = Array[AnyRef](gameName_)
    this.setTitle(MessageFormat.format(GameContext.getLabel("ABOUT"), arg: _*))

    setResizable(false)
    val overviewPanel = new JPanel(true)
    overviewPanel.setLayout(new BorderLayout())

    val flowLayout1 = new FlowLayout()
    val bottomButtonPanel = new JPanel(true)
    bottomButtonPanel.setLayout(flowLayout1)

    val logoInsetsPanel = new JPanel(true)
    logoInsetsPanel.setLayout(flowLayout1)
    logoInsetsPanel.setBorder(new EmptyBorder(10, 10, 10, 10))

    okButton_.setText(GameContext.getLabel("OK"))
    okButton_.addActionListener(this)

    val overviewTextArea = createOverviewTextArea()
    overviewTextArea.setFont(FONT)

    val summaryPanel = createSummaryPanel()

    val logo = new JLabel()
    logo.setIcon(GUIUtil.getIcon(GameContext.GAME_RESOURCE_ROOT + "common/ui/images/help.gif"))
    logo.setForeground(Color.GREEN)

    logoInsetsPanel.add(logo, null)
    summaryPanel.add(logoInsetsPanel, BorderLayout.WEST)

    overviewPanel.add(overviewTextArea, BorderLayout.CENTER)

    bottomButtonPanel.add(okButton_, null)
    overviewPanel.add(bottomButtonPanel, BorderLayout.SOUTH)
    overviewPanel.add(summaryPanel, BorderLayout.NORTH)

    overviewPanel
  }

  /**
   * @return the body overview text area.
   */
  private def createOverviewTextArea(): JTextArea = {
    val overviewTextArea = new JTextArea()
    overviewTextArea.setText(overviewText_)
    overviewTextArea.setBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
        BorderFactory.createEmptyBorder(5, 5, 5, 5)))
    overviewTextArea.setMaximumSize(new Dimension(500, 700))
    overviewTextArea.setPreferredSize(new Dimension(450, 300))

    overviewTextArea.setWrapStyleWord(true)
    overviewTextArea.setLineWrap(true)

    overviewTextArea.setAutoscrolls(true)
    overviewTextArea.setEditable(false)

    overviewTextArea
  }

  private def createSummaryPanel(): JPanel = {
    val summaryPanel = new JPanel(true)

    summaryPanel.setLayout(new BorderLayout())

    val summaryInsetsPanel = new JPanel(true)
    val label1 = new JLabel()
    val label2 = new JLabel()
    val label3 = new JLabel()
    val label4 = new JLabel()
    label1.setText(gameName_)
    label2.setText(VERSION)
    label3.setText(COPYRIGHT)
    label4.setText(comments_)

    summaryInsetsPanel.add(label1, null)
    summaryInsetsPanel.add(label2, null)
    //summaryInsetsPanel.add( label3, null );
    summaryInsetsPanel.add(label4, null)

    summaryInsetsPanel.setLayout(new GridLayout(4, 1))
    summaryInsetsPanel.setBorder(new EmptyBorder(10, 60, 10, 10))

    summaryPanel.add(summaryInsetsPanel, BorderLayout.CENTER)

    summaryPanel
  }

  override def actionPerformed(e: ActionEvent): Unit = {
    super.actionPerformed(e)
    if (e.getSource eq okButton_) {
      cancel()
    }
  }
}
