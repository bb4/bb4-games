/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search;

import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import org.junit.Before;
import org.junit.Test;

/**
 * Verify that all the methods in the Searchable interface work as expected.
 * Derived test classes will exercise these methods for specific game instances.
 *
 * @author Barry Becker
 */
public abstract class SearchableBaseTst {

    /** The searchable instance under test. */
    protected Searchable<TwoPlayerMove, TwoPlayerBoard> searchable;

    protected ISearchableHelper helper;

    @Before
    public void setUp() throws Exception {
        helper = createSearchableHelper();
    }

    /**
     * @return the helper that will help us create the controller, options and other related info.
     */
    protected abstract ISearchableHelper createSearchableHelper();

    /** verify that we can retrieve the lookahead value. */
    @Test
    public abstract void testLookaheadValue() throws Exception;

    /** verify that we can retrieve the lookahead value. */
    @Test
    public abstract void testAlphaBetaValue() throws Exception;

    /** verify that we can retrieve the quiescence value. */
    @Test
    public abstract void testQuiescenceValue() throws Exception;
}
