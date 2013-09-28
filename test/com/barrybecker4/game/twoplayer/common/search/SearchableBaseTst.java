/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search;

import junit.framework.TestCase;

/**
 * Verify that all the methods in the Searchable interface work as expected.
 * Derived test classes will exercise these methods for specific game instances.
 *
 * @author Barry Becker
 */
public abstract class SearchableBaseTst extends TestCase {

    /** The searchable instance under test. */
    protected Searchable searchable;

    protected ISearchableHelper helper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        helper = createSearchableHelper();
    }

    /**
     * @return the helper that will help us create the controller, options and other related info.
     */
    protected abstract ISearchableHelper createSearchableHelper();

    /** verify that we can retrieve the lookahead value. */
    public abstract void testLookaheadValue() throws Exception;

    /** verify that we can retrieve the lookahead value. */
    public abstract void testAlphaBetaValue() throws Exception;

    /** verify that we can retrieve the quiescence value. */
    public abstract void testQuiescenceValue() throws Exception;
}
