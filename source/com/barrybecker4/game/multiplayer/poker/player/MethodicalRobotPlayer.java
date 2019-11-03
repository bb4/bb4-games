/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.player;


import com.barrybecker4.game.multiplayer.poker.hand.HandScore;
import com.barrybecker4.game.multiplayer.poker.hand.HandType;
import com.barrybecker4.game.multiplayer.poker.model.PokerAction;
import com.barrybecker4.game.multiplayer.poker.PokerController;

import java.awt.*;
import java.util.Arrays;

import static com.barrybecker4.game.card.Rank.*;


/**
 * Represents a robotic Poker Player.
 *
 * @author Barry Becker
 */
public class MethodicalRobotPlayer extends PokerRobotPlayer {

    private static final HandScore HIGH_THRESHOLD_SCORE = new HandScore(HandType.TWO_PAIR, Arrays.asList(NINE));
    private static final HandScore MEDIUM_THRESHOLD_SCORE = new HandScore(HandType.PAIR, Arrays.asList(KING));
    private static final HandScore LOW_THRESHOLD_SCORE = new HandScore(HandType.HIGH_CARD, Arrays.asList(JACK));

    public MethodicalRobotPlayer(String name, int cash, Color color, RobotType rType) {
        super(name, cash, color, rType);
    }

    /**
     * @return an appropriate action based on the situation
     */
    @Override
    protected PokerAction createAction(PokerController pc) {
        boolean othersFolded = allOthersFolded(pc);

        PokerAction.Name action;
        int raise = 0;
        if (getHandScore().compareTo(LOW_THRESHOLD_SCORE) > 0 || Math.random() > 0.6 || othersFolded) {
            action = PokerAction.Name.CALL;
        } else if (getCash() > getCallAmount(pc)
                && (getHandScore().compareTo(MEDIUM_THRESHOLD_SCORE) > 0 || Math.random() > 0.9)) {
            action = PokerAction.Name.RAISE;
            raise = getRaise(pc);
        } else {
            action = PokerAction.Name.FOLD;
        }
        return new PokerAction(getName(), action, raise);
    }

    @Override
    protected int getRaise(PokerController pc) {
        int allInAmt = pc.getAllInAmount() - this.getContribution();
        int max = getCash();

        if (getHandScore().compareTo(HIGH_THRESHOLD_SCORE) > 0 || Math.random() > 0.9) {
            return min(max/10, max, allInAmt);
        }
        else if (getHandScore().compareTo(LOW_THRESHOLD_SCORE) > 0 || Math.random() > 0.3) {
            return min(1 + max/40, max, allInAmt);
        } else {
            return min(1, max, allInAmt);
        }
    }

}



