/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.player;


import com.barrybecker4.game.multiplayer.poker.hand.HandScore;
import com.barrybecker4.game.multiplayer.poker.hand.HandType;
import com.barrybecker4.game.multiplayer.poker.model.PokerAction;
import com.barrybecker4.game.multiplayer.poker.PokerController;

import java.awt.*;
import java.util.Arrays;

import static com.barrybecker4.game.card.Rank.KING;
import static com.barrybecker4.game.card.Rank.NINE;
import static com.barrybecker4.game.card.Rank.QUEEN;

/**
 * Represents a Crazy Robot Poker player.
 *
 * @author Barry Becker
 */
public class CrazyRobotPlayer extends PokerRobotPlayer {

    private static final int DESIRED_RAISE = 20;

    private static final HandScore HIGH_THRESHOLD_SCORE = new HandScore(HandType.TWO_PAIR, Arrays.asList(NINE));
    private static final HandScore MEDIUM_THRESHOLD_SCORE = new HandScore(HandType.PAIR, Arrays.asList(KING));
    private static final HandScore LOW_THRESHOLD_SCORE = new HandScore(HandType.HIGH_CARD, Arrays.asList(QUEEN));

    public CrazyRobotPlayer(String name, int cash, Color color, RobotType rType) {
        super(name, cash, color, rType);
    }

    @Override
    protected PokerAction createAction(PokerController pc) {
        PokerAction.Name action;
        int raise = 0;
        if ((getCash() > getCallAmount(pc))
                && (getHandScore().compareTo(MEDIUM_THRESHOLD_SCORE) > 0 || Math.random() > 0.3)) {
            action =  PokerAction.Name.RAISE;
            raise = getRaise(pc);
        } else if (getHandScore().compareTo(LOW_THRESHOLD_SCORE) > 0 || Math.random() > 0.5 || allOthersFolded(pc)) {
            action =  PokerAction.Name.CALL;
        } else {
            action = PokerAction.Name.FOLD;
        }
        return new PokerAction(getName(), action, raise);
    }

    @Override
    protected int getRaise(PokerController pc) {
        int allInAmt = pc.getAllInAmount() - getContribution() - getCallAmount(pc);
        int raiseAmt = 0;
        if (getHandScore().compareTo(HIGH_THRESHOLD_SCORE) > 0) {
            raiseAmt = min(DESIRED_RAISE, getCash(), allInAmt);
        }
        else {
            raiseAmt = min(2, getCash(), allInAmt);
        }
        return raiseAmt;
    }

}



