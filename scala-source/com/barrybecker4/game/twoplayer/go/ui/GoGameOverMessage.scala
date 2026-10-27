/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.ui

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.common.ui.GameOverMessage
import com.barrybecker4.game.twoplayer.go.GoController
import com.barrybecker4.game.twoplayer.go.board.GoSearchable

/**
 *  Takes a GoController as input and displays the
 *  current state of the Go Game.
 *
 *  @author Barry Becker
 */
final class GoGameOverMessage(controller: GoController) extends GameOverMessage(controller) {

  private val STONES_CAPTURED = GameContext.getLabel("CAPTURES_EQUALS")
  private val TERRITORY = GameContext.getLabel("TERRITORY_EQUALS")
  private val SCORE = GameContext.getLabel("SCORE_EQUALS")

  /**
   * @return   the message to display at the completion of the game.
   */
  override def getText(): String = {
    val gc = controller.asInstanceOf[GoController]

    var message = "\n"

    val searchable = controller.getSearchable().asInstanceOf[GoSearchable]
    val blackCaptures = searchable.getNumCaptures(true) + searchable.getNumDeadStonesOnBoard(true)
    val whiteCaptures = searchable.getNumCaptures(false) + searchable.getNumDeadStonesOnBoard(false)

    val p1Name = gc.getPlayers.getPlayer1.getName
    val p2Name = gc.getPlayers.getPlayer2.getName

    message += p1Name + ' ' + STONES_CAPTURED + blackCaptures + '\n'
    message += p2Name + ' ' + STONES_CAPTURED + whiteCaptures + "\n\n"

    val blackTerritory = gc.getFinalTerritory(true)
    val whiteTerritory = gc.getFinalTerritory(false)
    message += p1Name + ' ' + TERRITORY + blackTerritory + '\n'
    message += p2Name + ' ' + TERRITORY + whiteTerritory + "\n\n"

    message += p1Name + ' ' + SCORE + gc.getFinalScore(true) + '\n'
    message += p2Name + ' ' + SCORE + gc.getFinalScore(false) + '\n'

    super.getText() + '\n' + message
  }
}
