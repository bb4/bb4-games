/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui

import com.barrybecker4.common.format.FormatUtil
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.player.PlayerList
import com.barrybecker4.game.twoplayer.common.{TwoPlayerBoard, TwoPlayerController, TwoPlayerMove}

import java.text.MessageFormat

/**
 * Represents the message shown when the game is over.
 *
 * @author Barry Becker
 */
class GameOverMessage[M <: TwoPlayerMove, B <: TwoPlayerBoard[M]](protected val controller: TwoPlayerController[M, B]):

  /**
   * @return the message to display at the completion of the game.
   */
  def getText(): String =
    val players = controller.getPlayers
    var text: String = null

    if players.anyPlayerWon then
      text = createWonMessage(players)
    else
      text = GameContext.getLabel("TIE_MSG")
    text

  private def createWonMessage(players: PlayerList): String =
    val winningPlayer = if players.getPlayer1.hasWon then players.getPlayer1 else players.getPlayer2
    val losingPlayer = if players.getPlayer1.hasWon then players.getPlayer2 else players.getPlayer1

    val formatter = new MessageFormat(GameContext.getLabel("WON_MSG"))
    val args = new Array[Object](5)
    if players.allPlayersHuman then
      args(0) = ""
    else
      args(0) = if winningPlayer.isHuman then GameContext.getLabel("YOU") else GameContext.getLabel("THE_COMPUTER")
    args(1) = winningPlayer.getName
    args(2) = Integer.valueOf(controller.getNumMoves)
    args(3) = FormatUtil.formatNumber(controller.getStrengthOfWin)
    val text = formatter.format(args)

    assert(!losingPlayer.hasWon, "Both players should not win. Players=" + players)
    text

  override def toString(): String = getText()
