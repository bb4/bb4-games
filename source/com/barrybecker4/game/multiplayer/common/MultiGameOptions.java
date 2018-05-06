/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.common;

import com.barrybecker4.game.common.GameOptions;

/**
 * @author Barry Becker
 */
public class MultiGameOptions extends GameOptions {

    private static final int DEFAULT_PLAYER_LIMIT = 8;
    private static final int DEFAULT_NUM_ROBOT_PLAYERS = 1;

    /** no more than this many allowed at the table. */
    private int maxNumPlayers = DEFAULT_PLAYER_LIMIT;

    /**
     * The number of robot players at the table.
     * You can change this in the new game dlg if stand alone.
     */
    private int numRobotPlayers = DEFAULT_NUM_ROBOT_PLAYERS;


    protected MultiGameOptions() {
          this(DEFAULT_PLAYER_LIMIT, DEFAULT_NUM_ROBOT_PLAYERS);
    }

    protected MultiGameOptions(int maxNumPlayers, int numRobotPlayers) {
         this.maxNumPlayers = maxNumPlayers;
         this.numRobotPlayers = numRobotPlayers;
    }

    /**
     * Usually 2 but we allow for override.
     */
    public int getMinNumPlayers() {
        return 2;
    }

    @Override
    public int getMaxNumPlayers() {
        return maxNumPlayers;
    }

    /**
     * You wont be able to add more than this many players.
     * @param playerLimit no more than this many players allowed
     */
    protected void setMaxNumPlayers(int playerLimit) {
        maxNumPlayers = playerLimit;
    }

    public int getNumRobotPlayers() {
        return numRobotPlayers;
    }

    public void setNumRobotPlayers(int numRobotPlayers) {
        this.numRobotPlayers = numRobotPlayers;
    }

    /**
     * Check constraints on options to verify validity.
     * @return  null if no errors, return error messages if constraints violated.
     */
    @Override
    public String testValidity() {
        String msgs = "";
        if (getNumRobotPlayers() > getMaxNumPlayers())  {
            msgs += "The number of robot players cannot can exceed the total number of players.";
        }
        return (msgs.length() > 0)? msgs : null;
    }
}
