/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set;

import com.barrybecker4.game.multiplayer.common.MultiGameOptions;

/**
 * Set Game options
 * Some other possibilities to add
 *   - use only one color (need to specify which color).
 *   -
 *
 * @author Barry Becker Date: Sep 2, 2006
 */
public class SetOptions extends MultiGameOptions {


    // initial number of cards shown face up on the board.
    private static final int INITIAL_NUM_CARDS_SHOWN = 12;

    private static final int DEFAULT_SET_PLAYER_LIMIT = 5;

    private int initialNumCardsShown_ = INITIAL_NUM_CARDS_SHOWN;

    public SetOptions() {
        setMaxNumPlayers(DEFAULT_SET_PLAYER_LIMIT);
    }

    public SetOptions(int maxNumPlayers, int numRobotPlayers, int initialNumCards) {
        super(maxNumPlayers, numRobotPlayers);
        setInitialNumCardsShown(initialNumCards);
    }

    public int getInitialNumCardsShown() {
        return initialNumCardsShown_;
    }

    void setInitialNumCardsShown(int initialNumCardsShown) {
        this.initialNumCardsShown_ = initialNumCardsShown;
    }
}
