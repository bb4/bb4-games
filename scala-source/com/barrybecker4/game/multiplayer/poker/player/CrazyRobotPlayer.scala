/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.player

import com.barrybecker4.game.multiplayer.poker.hand.{HandScore, HandType}
import com.barrybecker4.game.multiplayer.poker.model.PokerAction
import com.barrybecker4.game.multiplayer.poker.PokerController
import java.awt.Color
import java.util.Arrays
import com.barrybecker4.game.card.Rank.*

class CrazyRobotPlayer(name: String, cash: Int, color: Color, rType: RobotType) 
    extends PokerRobotPlayer(name, cash, color, rType) {
  import CrazyRobotPlayer.*

  override def createAction(pc: PokerController): PokerAction = {
    var action: PokerAction.Name = null
    var raise = 0
    
    if ((getCash > getCallAmount(pc)) 
        && (getHandScore.compareTo(MEDIUM_THRESHOLD_SCORE) > 0 || Math.random() > 0.3)) {
      action = PokerAction.Name.RAISE
      raise = getRaise(pc)
    } else if (getHandScore.compareTo(LOW_THRESHOLD_SCORE) > 0 || Math.random() > 0.5 || allOthersFolded(pc)) {
      action = PokerAction.Name.CALL
    } else {
      action = PokerAction.Name.FOLD
    }
    PokerAction(getName, action, raise)
  }

  override def getRaise(pc: PokerController): Int = {
    val allInAmt = pc.getAllInAmount - getContribution - getCallAmount(pc)
    val raiseAmt = if (getHandScore.compareTo(HIGH_THRESHOLD_SCORE) > 0) {
      min(DESIRED_RAISE, getCash, allInAmt)
    } else {
      min(2, getCash, allInAmt)
    }
    raiseAmt
  }
}

object CrazyRobotPlayer {
  private val DESIRED_RAISE = 20
  private val HIGH_THRESHOLD_SCORE = HandScore(HandType.TWO_PAIR, Arrays.asList(NINE))
  private val MEDIUM_THRESHOLD_SCORE = HandScore(HandType.PAIR, Arrays.asList(KING))
  private val LOW_THRESHOLD_SCORE = HandScore(HandType.HIGH_CARD, Arrays.asList(QUEEN))
}
