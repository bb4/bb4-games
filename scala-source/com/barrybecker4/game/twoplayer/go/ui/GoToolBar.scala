/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.ui

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.ui.panel.GameToolBar
import com.barrybecker4.ui.components.GradientButton
import javax.swing.{ImageIcon, JButton}
import java.awt.event.ActionListener

/**
 * @author Barry Becker
 */
class GoToolBar(texture: ImageIcon, listener: ActionListener) extends GameToolBar(texture, listener) {

  private var passButton_ : GradientButton = scala.compiletime.uninitialized
  private var resignButton_ : GradientButton = scala.compiletime.uninitialized

  /**
   * add the button for passing and resigning.
   */
  override final def addCustomToolBarButtons(): Unit = {
    passButton_ = createToolBarButton(GameContext.getLabel("PASS_BTN"),
      GameContext.getLabel("PASS_BTN_TIP"),
      null)
    add(passButton_)

    resignButton_ = createToolBarButton(GameContext.getLabel("RESIGN_BTN"),
      GameContext.getLabel("RESIGN_BTN_TIP"),
      null)
    add(resignButton_)
  }

  def getPassButton: JButton = passButton_

  def getResignButton: JButton = resignButton_
}
