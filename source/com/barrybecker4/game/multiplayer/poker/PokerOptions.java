/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker;

import com.barrybecker4.game.multiplayer.common.MultiGameOptions;

/**
 * @author Barry Becker
 */
public class PokerOptions extends MultiGameOptions {

    public static final int DEFAULT_ANTE = 2;
    public static final int DEFAULT_MAX_ABS_RAISE = 50;
    public static final int DEFAULT_INITIAL_CASH = 100;

    /** starting bid*/
    private int ante_ = DEFAULT_ANTE;

    /** can't raise by more than this */
    private int maxAbsoluteRaise_ = DEFAULT_MAX_ABS_RAISE;

    /** default starting chips for each player */
    private int initialChips_ = DEFAULT_INITIAL_CASH;


    /**
     * this constructor uses all default values.
     */
    public PokerOptions() {}

    /**
     * User specified values for options.
     */
    public PokerOptions(int maxNumPlayers, int numRobotPlayers,
                        int ante, int maxAbsoluteRaise, int initialCash) {
        super(maxNumPlayers, numRobotPlayers);
        setAnte(ante);
        setMaxAbsoluteRaise(maxAbsoluteRaise);
        setInitialChips(initialCash);
    }

    /**
     * Verify poker option constraints satisfied.
     * @return error messages to show in a dlg.
     */
    @Override
    public String testValidity() {
        String superMsgs = super.testValidity();
        String msgs = "" + (superMsgs != null ? superMsgs : "");
        if (getAnte() > getMaxAbsoluteRaise()) {
            msgs += "The ante cannot be larger than the maximum raise amount\n";
        }
        if (getAnte() > getInitialCash() >> 1) {
            msgs += "The initial cash for a player cannot be less than twice the ante.";
        }
        if (getInitialCash() < getMaxAbsoluteRaise()) {
            msgs += "The initial cash for a player cannot be less than the maximum raise amount.";
        }

        return (msgs.length() > 0) ? msgs : null;
    }

    public int getAnte() {
        return ante_;
    }

    void setAnte(int ante) {
        this.ante_ = ante;
    }

    public int getMaxAbsoluteRaise() {
        return maxAbsoluteRaise_;
    }

    void setMaxAbsoluteRaise(int maxAbsoluteRaise) {
        this.maxAbsoluteRaise_ = maxAbsoluteRaise;
    }

    public int getInitialCash() {
        return initialChips_;
    }

    void setInitialChips(int initialChips) {
        this.initialChips_ = initialChips;
    }

}
