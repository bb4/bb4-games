/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set;


import java.awt.*;

/**
 * Represents a Robot Set player.
 * @author Barry Becker
 */
public class SetRobotPlayer extends SetPlayer {

    public enum RobotType { SLOW_ROBOT, AVERAGE_ROBOT, EXPERT_ROBOT }

    private RobotType robotType_;


    private SetRobotPlayer(String name, RobotType robotType, Color color) {
        super(name, color, false);
        robotType_ = robotType;
    }


    private static int seq_ = 0;

    /**
     * @return  robot players in round robin order (not randomly)
     */
    public static SetRobotPlayer getSequencedRobotPlayer(String name, Color color) {
        int r = seq_++ % RobotType.values().length;
        return new SetRobotPlayer(name, RobotType.values()[r], color);
    }

    public String getRobotType() {
        return robotType_.name();
    }

    /**
     * @return speed in milliseconds in which the robot player will find the next pair
     */
    private int getSpeed() {
        switch (robotType_) {
            case SLOW_ROBOT: return 30000;
            case AVERAGE_ROBOT: return 15000;
            case EXPERT_ROBOT: return 5000;
        }
        return 1000; // never called
    }
}
