/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.ui

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.{GameContext, GameController}
import com.barrybecker4.game.common.ui.viewer.{GameBoardRenderer, ViewerMouseListener}
import com.barrybecker4.game.twoplayer.common.ui.AbstractTwoPlayerBoardViewer
import com.barrybecker4.game.twoplayer.go.GoController
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.eye.IGoEye
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition
import com.barrybecker4.game.twoplayer.go.board.elements.string.IGoString
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import com.barrybecker4.game.twoplayer.go.ui.rendering.GoBoardRenderer
import java.awt.event.MouseEvent

/**
 *  Takes a GoController as input and displays the
 *  current state of the Go Game.
 *
 *  @author Barry Becker
 */
final class GoBoardViewer extends AbstractTwoPlayerBoardViewer[GoMove, GoBoard] {

  override def createViewerMouseListener(): ViewerMouseListener[GoMove, GoBoard] = {
    new GoViewerMouseListener(this)
  }

  /**
   * start over with a new game using the current options.
   */
  override def startNewGame(): Unit = {
    super.startNewGame()
    getBoardRenderer.setDraggedShowPiece(null)
  }

  override def createController(): GameController[GoMove, GoBoard] = {
    new GoController()
  }

  override def getBoardRenderer: GameBoardRenderer = {
    GoBoardRenderer.getRenderer
  }

  /**
   * perform a pass for the current player.
   */
  def pass(): Unit = {
    GameContext.log(1, "passing")
    val m = GoMove.createPassMove(0, get2PlayerController().isPlayer1sTurn)
    continuePlay(m)
  }

  /**
   * Current player resigns from the game.
   */
  def resign(): Unit = {
    GameContext.log(1, "player resigns")
    val m = GoMove.createResignationMove(get2PlayerController().isPlayer1sTurn)
    continuePlay(m)
  }

  /**
   * @return   the message to display at the completion of the game.
   */
  override def getGameOverMessage: String = {
    refresh()
    new GoGameOverMessage(controller.asInstanceOf[GoController]).getText()
  }

  /**
   * @return the tooltip for the panel given a mouse event.
   */
  override def getToolTipText(e: MouseEvent): String = {
    if (get2PlayerController().isProcessing) return ""

    val loc = getBoardRenderer.createLocation(e)
    val sb = new StringBuilder("<html><font=-3>")

    val space = controller.getBoard.getPosition(loc).asInstanceOf[GoBoardPosition]
    if (space != null && GameContext.getDebugMode > 0) {
      val spaceText = space.getDescription
      sb.append(spaceText)
      val string = space.getString
      val eye = space.getEye
      if (string != null) {
        appendStringText(sb, spaceText, string)
      }
      if (eye != null) {
        appendEyeText(sb, spaceText, eye)
      }
    } else {
      sb.append(loc)
    }
    sb.append("</font></html>")
    sb.toString
  }

  private def appendEyeText(sb: StringBuilder, spaceText: String, eye: IGoEye): Unit = {
    var eyeText = eye.toString
    sb.append("<br>")
    eyeText = eyeText.replaceAll(spaceText, "<b><font color=#991100>" + spaceText + "</font></b>")
    sb.append(eyeText)
    sb.append("<br>")
    sb.append("The group that contains this eye is ").append(eye.getGroup)
  }

  private def appendStringText(sb: StringBuilder, spaceText: String, string: IGoString): Unit = {
    sb.append("<br>")
    sb.append("string liberties = ").append(string.getNumLiberties(controller.getBoard))
    val stringText = string.toString
    if (string.getGroup != null) {
      sb.append("<br>")
      var groupText = string.getGroup.toHtml

      groupText = groupText.replaceAll(stringText, "<font color=#440000>" + stringText + "</font>")
      groupText = groupText.replaceAll(spaceText, "<b><font color=#991100>" + spaceText + "</font></b>")
      sb.append(groupText)
    }
  }
}
