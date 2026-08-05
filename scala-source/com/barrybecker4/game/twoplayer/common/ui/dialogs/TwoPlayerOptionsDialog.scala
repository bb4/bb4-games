/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui.dialogs

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.ui.dialogs.GameOptionsDialog
import com.barrybecker4.game.twoplayer.common.{TwoPlayerController, TwoPlayerOptions}

import javax.swing.{JCheckBox, JPanel}
import java.awt.event.{ActionListener, ItemListener}
import java.awt.{BorderLayout, Component}
import scala.compiletime.uninitialized

/**
 * Use this modal dialog to let the user choose from among the
 * different game options.
 *
 * @author Barry Becker
 */
class TwoPlayerOptionsDialog(parent: Component, controller: TwoPlayerController[?, ?])
  extends GameOptionsDialog(parent, controller) with ActionListener with ItemListener:

  // Must not use `= null` — parent ctor calls createDebugParamPanel which sets this field.
  private var gameTreeCheckbox_ : JCheckBox = uninitialized

  /**
   * @return controller
   */
  private def get2PlayerController(): TwoPlayerController[?, ?] =
    controller_.asInstanceOf[TwoPlayerController[?, ?]]

  override def getOptions: TwoPlayerOptions =
    val options = getTwoPlayerOptions()

    options.setShowGameTree(gameTreeCheckbox_.isSelected())
    options

  private def getTwoPlayerOptions(): TwoPlayerOptions =
    controller_.getOptions.asInstanceOf[TwoPlayerOptions]

  override def createControllerParamPanel(): JPanel =
    null

  /**
   * @return debug params tab panel
   */
  override def createDebugParamPanel(): JPanel =
    val outerContainer = new JPanel(new BorderLayout())
    val p = createDebugOptionsPanel()

    addDebugLevel(p)
    addLoggerSection(p)
    addProfileCheckBox(p)

    // show game tree option
    val options = get2PlayerController().getOptions
    gameTreeCheckbox_ = new JCheckBox(GameContext.getLabel("SHOW_GAME_TREE"), options.getShowGameTree())
    gameTreeCheckbox_.setToolTipText(GameContext.getLabel("SHOW_GAME_TREE_TIP"))
    gameTreeCheckbox_.addActionListener(this)
    gameTreeCheckbox_.setAlignmentX(Component.LEFT_ALIGNMENT)
    p.add(gameTreeCheckbox_)

    outerContainer.add(p, BorderLayout.NORTH)

    outerContainer
