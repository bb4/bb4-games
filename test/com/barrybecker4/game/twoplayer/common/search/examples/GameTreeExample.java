/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.examples;

import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;

/**
 * An abstract game tree for testing search strategies.
 * Note that in all the sample trees. Only the leaf values really matter, the non-leaf values should be ignored.
 * Though tht non-leaf values may be used for move ordering, they should not effect the final inherited values.
 *
 * @author Barry Becker
 */
public interface GameTreeExample {


    /**
     * @return  the root move in the game tree.
     */
    TwoPlayerMove getInitialMove();

    /**
     * Print the tree in depth first search for debugging purposes
     */
    void print();
}
