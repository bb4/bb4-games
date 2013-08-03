/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy.integration;

import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategyType;

/**
 * Verify that all the methods in the Searchable interface work as expected.
 * Derived test classes will excersize these methods for specific game instances.
 *
 * @author Barry Becker
 */
public abstract class NegaScoutStrategyTst extends AbstractStrategyTst {

    @Override
    protected SearchStrategyType getSearchStrategyToTest() {
        return SearchStrategyType.NEGASCOUT;
    }

}