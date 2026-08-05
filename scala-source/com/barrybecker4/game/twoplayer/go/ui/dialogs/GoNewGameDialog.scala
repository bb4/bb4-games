/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.ui.dialogs

import com.barrybecker4.game.common.{GameContext, GameViewModel}
import com.barrybecker4.game.twoplayer.common.ui.dialogs.{PlayerAssignmentPanel, TwoPlayerNewGameDialog}
import com.barrybecker4.game.twoplayer.go.GoController
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.ui.components.NumberInput
import javax.swing.{BoxLayout, JPanel}
import java.awt.Component
import java.awt.event.ActionListener

final class GoNewGameDialog(parent: Component, viewer: GameViewModel) 
  extends TwoPlayerNewGameDialog(parent, viewer) with ActionListener {

  private var handicapField_ : NumberInput = scala.compiletime.uninitialized

  override def createCustomBoardConfigPanel(): JPanel = {
    val p = new JPanel()
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS))

    handicapField_ = new NumberInput(GameContext.getLabel("HANDICAP_LABEL"), 
      board_.asInstanceOf[GoBoard].getHandicap)

    p.add(handicapField_)
    assert(handicapField_ != null)
    p
  }

  override def createPlayerAssignmentPanel(): PlayerAssignmentPanel = {
    new GoPlayerAssignmentPanel(get2PlayerController(), this.myParent)
  }

  override def ok(): Unit = {
    val gcontroller = controller_.asInstanceOf[GoController]

    assert(handicapField_ != null)
    gcontroller.setHandicap(handicapField_.getIntValue)

    GameContext.log(2, "GoOptionsDlg: the handicap is:" + handicapField_.getIntValue)
    super.ok()
  }
}
