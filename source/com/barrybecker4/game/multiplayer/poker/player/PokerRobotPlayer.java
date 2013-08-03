/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.player;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerAction;
import com.barrybecker4.game.multiplayer.common.MultiGameController;
import com.barrybecker4.game.multiplayer.poker.model.PokerAction;
import com.barrybecker4.game.multiplayer.poker.PokerController;

import java.awt.*;

/**
 * Represents a Robot poker player.
 * These Robot Admirals have there own unique strategy for playing.
 * Abstract base class for other robot player types.
 *
 * @author Barry Becker
 */
public abstract class PokerRobotPlayer extends PokerPlayer {

    private RobotType robotType_;

    PokerRobotPlayer(String name, int money, Color color, RobotType rType) {
        super(name, money, color, false);
        robotType_ = rType;
    }

    @Override
    public void setAction(PlayerAction action) {
        assert action != null;
        action_ = (PokerAction) action;
    }

    /**
     * @return an appropriate action based on the situation
     */
    @Override
    public PlayerAction getAction(MultiGameController controller) {

        PokerAction a;
        if (action_ == null) {
            a = createAction((PokerController) controller);
        }
        else {
            a = action_;
        }
        action_ = null;
        GameContext.log(0, "in PokerRobotPlayer.getAction a=" + a);
        return a;
    }

    protected abstract PokerAction createAction(PokerController pc);

    /**
     * @return a string describing the type of robot.
     */
    public String getType() {
        return  robotType_.getName();
    }

    /**
     *
     * @return the amount that the robot raises if he raises.
     */
    protected abstract int getRaise(PokerController pc);

    /**
     * just pass in options instead of money
     * @return a random robot player
     */
    public static PokerRobotPlayer getRandomRobotPlayer(String name, int money, Color color) {
        int r = (int)(RobotType.values().length * Math.random());
        return getRobotPlayer(RobotType.values()[r], name, money, color);
    }


    private static int seq_ = 0;
    /**
     *
     * @return  robot players in round robin order (not randomly)
     */
    public static PokerRobotPlayer getSequencedRobotPlayer(String name, int money, Color color) {
        int r = seq_++ % RobotType.values().length;
        return getRobotPlayer(RobotType.values()[r], name, money, color);
    }


    private static PokerRobotPlayer getRobotPlayer(RobotType type, String name, int money, Color color) {
         switch (type) {
            case CRAZY_ROBOT:
                return new CrazyRobotPlayer(name, money, color, RobotType.CRAZY_ROBOT);
            case METHODICAL_ROBOT:
                return new MethodicalRobotPlayer(name, money, color, RobotType.METHODICAL_ROBOT);
            case TIMID_ROBOT:
                return new TimidRobotPlayer(name, money, color, RobotType.TIMID_ROBOT);
        }
        return null;
    }


    int min(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    boolean allOthersFolded(PokerController pc) {

        for (Player player : pc.getPlayers()) {
            PokerPlayer pp = (PokerPlayer) player;
            if (!pp.hasFolded() && (pp != this)) {
                return false;
            }
        }
        return true;
    }
}



