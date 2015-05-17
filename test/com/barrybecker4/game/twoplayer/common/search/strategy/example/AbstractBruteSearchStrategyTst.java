/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy.example;

import com.barrybecker4.game.twoplayer.common.search.TwoPlayerMoveStub;
import com.barrybecker4.game.twoplayer.common.search.examples.*;
import com.barrybecker4.game.twoplayer.common.search.options.BruteSearchOptions;
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions;
import com.barrybecker4.game.twoplayer.common.search.strategy.testcase.SearchResult;

/**
 * Test minimax strategy independent of any particular game implementation.
 *
 * @author Barry Becker
 */
@SuppressWarnings({"ClassWithTooManyMethods"})
public abstract class AbstractBruteSearchStrategyTst extends AbstractSearchStrategyTst {

    protected BruteSearchOptions bruteSearchOptions;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        bruteSearchOptions = searchOptions.getBruteSearchOptions();
    }

    /**
     * @return default search options for all games
     */
    @Override
    public SearchOptions createSearchOptions() {

        SearchOptions opts =  super.createSearchOptions();
        BruteSearchOptions options = opts.getBruteSearchOptions();
        options.setLookAhead(3);
        options.setAlphaBeta(false);
        options.setQuiescence(false);
        options.setMaxQuiescentDepth(3);
        return opts;
    }

    /**
     * Edge case where no searching is actually done. The found move will be the root.
     */
    public void testZeroLookAheadSearch() {
        bruteSearchOptions.setLookAhead(0);
        verifyResult(new ZeroLevelGameTreeExample(false),
                getZeroLookAheadResult());
    }

    /**
     * Look ahead one level and get the best move.
     */
    public void testOneLevelLookAheadPlayer1Search() {
        bruteSearchOptions.setLookAhead(1);
        verifyResult(new OneLevelGameTreeExample(true),
                getOneLevelLookAheadPlayer1Result());
    }

    /**
     * Look ahead one level and get the best move.
     */
    public void testOneLevelLookAheadPlayer2Search() {
        bruteSearchOptions.setLookAhead(1);
        verifyResult(new OneLevelGameTreeExample(false),
                getOneLevelLookAheadPlayer2Result());
    }

    public void testTwoLevelPlayer1Search() {
        bruteSearchOptions.setLookAhead(2);
        verifyResult(new TwoLevelGameTreeExample(true),
                getTwoLevelPlayer1Result());
    }

    public void testTwoLevelPlayer2Search() {
        bruteSearchOptions.setLookAhead(2);
        verifyResult(new TwoLevelGameTreeExample(false),
                getTwoLevelPlayer2Result());
    }

    public void testTwoLevelQuiescensePlayer1Search() {
        bruteSearchOptions.setLookAhead(2);
        bruteSearchOptions.setQuiescence(true);
        verifyResult(new TwoLevelQuiescentExample(true),
                getTwoLevelQuiescensePlayer1Result());
    }

    public void testTwoLevelQuiescensePlayer2Search() {
        bruteSearchOptions.setLookAhead(2);
        bruteSearchOptions.setQuiescence(true);
        verifyResult(new TwoLevelQuiescentExample(false),
                getTwoLevelQuiescensePlayer2Result());
    }

    public void testTwoLevelQuiescenseABPlayer1Search() {
        bruteSearchOptions.setLookAhead(2);
        bruteSearchOptions.setQuiescence(true);
        bruteSearchOptions.setAlphaBeta(true);
        verifyResult(new TwoLevelQuiescentExample(true),
                getTwoLevelQuiescenseABPlayer1Result());
    }

    public void testTwoLevelQuiescenseABPlayer2Search() {
        bruteSearchOptions.setLookAhead(2);
        bruteSearchOptions.setQuiescence(true);
        bruteSearchOptions.setAlphaBeta(true);
        verifyResult(new TwoLevelQuiescentExample(false),
                getTwoLevelQuiescenseABPlayer2Result());
    }

    public void testLadderMax3QuiescensePlayer1Search() {
        bruteSearchOptions.setLookAhead(2);
        bruteSearchOptions.setQuiescence(true);
        bruteSearchOptions.setMaxQuiescentDepth(3); // max 3 plies beyond normal look-ahead.
        verifyResult(new LadderQuiescentExample(true),
                getLadderMax3QuiescensePlayer1Result());
    }

    public void testLadderMax3QuiescensePlayer2Search() {
        bruteSearchOptions.setLookAhead(2);
        bruteSearchOptions.setQuiescence(true);
        bruteSearchOptions.setMaxQuiescentDepth(3); // max 3 plies beyond normal look-ahead.
        verifyResult(new LadderQuiescentExample(false),
                getLadderMax3QuiescensePlayer2Result());
    }

    public void testLadderMax4QuiescensePlayer1Search() {
        bruteSearchOptions.setLookAhead(2);
        bruteSearchOptions.setQuiescence(true);
        bruteSearchOptions.setMaxQuiescentDepth(4); // max 4 plies beyond normal look-ahead.
        verifyResult(new LadderQuiescentExample(true),
                getLadderMax4QuiescensePlayer1Result());
    }

    public void testLadderMax4QuiescensePlayer2Search() {
        bruteSearchOptions.setLookAhead(2);
        bruteSearchOptions.setQuiescence(true);
        bruteSearchOptions.setMaxQuiescentDepth(4); // max 4 plies beyond normal look-ahead.
        verifyResult(new LadderQuiescentExample(false),
                getLadderMax4QuiescensePlayer2Result());
    }

    public void testPruneTwoLevelWithoutABSearchPlayer1() {
        bruteSearchOptions.setLookAhead(2);
        bruteSearchOptions.setAlphaBeta(false);
        verifyResult(new AlphaPruneExample(true),
                getPruneTwoLevelWithoutABResultPlayer1());
    }

    public void testPruneTwoLevelWithABSearchPlayer1() {
        bruteSearchOptions.setLookAhead(2);
        bruteSearchOptions.setAlphaBeta(true);
        verifyResult(new AlphaPruneExample(true),
                getPruneTwoLevelWithABSearchPlayer1());
    }

    public void testPruneTwoLevelWithABSearchPlayer2() {
        bruteSearchOptions.setLookAhead(2);
        bruteSearchOptions.setAlphaBeta(true);
        verifyResult(new AlphaPruneExample(false),
                getPruneTwoLevelWithABSearchPlayer2());
    }

    public void testPruneFourLevelWithABSearchPlayer1() {
        bruteSearchOptions.setLookAhead(4);
        bruteSearchOptions.setAlphaBeta(true);
        verifyResult(new AlphaBetaPruneExample(true),
                getPruneFourLevelWithABSearchPlayer1());
    }

    public void testPruneFourLevelWithABSearchPlayer2() {
        bruteSearchOptions.setLookAhead(4);
        bruteSearchOptions.setAlphaBeta(true);
        verifyResult(new AlphaBetaPruneExample(false),
                getPruneFourLevelWithABSearchPlayer2());
    }

    public void testThreeLevelPlayer1Search() {
        verifyResult(new ThreeLevelGameTreeExample(true),
                getThreeLevelPlayer1Result());
    }

    public void testThreeLevelPlayer2Search() {
        verifyResult(new ThreeLevelGameTreeExample(false),
                getThreeLevelPlayer2Result());
    }

    public void testThreeLevelPlayer1WithABSearch() {
        bruteSearchOptions.setLookAhead(3);
        bruteSearchOptions.setAlphaBeta(true);
        verifyResult(new ThreeLevelGameTreeExample(true),
                getThreeLevelPlayer1WithABResult());
    }

    public void testThreeLevelPlayer2WithABSearch() {
        bruteSearchOptions.setLookAhead(3);
        bruteSearchOptions.setAlphaBeta(true);
        verifyResult(new ThreeLevelGameTreeExample(false),
                getThreeLevelPlayer2WithABResult());
    }

    public void testFourLevelSearchPlayer1() {
        bruteSearchOptions.setLookAhead(4);
        verifyResult(new FourLevelGameTreeExample(true),
                getFourLevelPlayer1Result());
    }

    public void testFourLevelSearchPlayer2() {
        bruteSearchOptions.setLookAhead(4);
        verifyResult(new FourLevelGameTreeExample(false),
                getFourLevelPlayer2Result());
    }

    public void testFourLevelABSearchPlayer1() {
        bruteSearchOptions.setLookAhead(4);
        bruteSearchOptions.setAlphaBeta(true);
        verifyResult(new FourLevelGameTreeExample(true),
                getFourLevelABPlayer1Result());
    }

    public void testFourLevelABSearchPlayer2() {
        bruteSearchOptions.setLookAhead(4);
        bruteSearchOptions.setAlphaBeta(true);
        verifyResult(new FourLevelGameTreeExample(false),
                getFourLevelABPlayer2Result());
    }

    // the following results are for minimax and negamax. Other algorithms may be slightly different (better)

    protected SearchResult getZeroLookAheadResult() {
        return new SearchResult(TwoPlayerMoveStub.ROOT_ID, 6, 0);
    }

    protected SearchResult getOneLevelLookAheadPlayer1Result() {
        return new SearchResult("1", -2, 2);
    }
    protected SearchResult getOneLevelLookAheadPlayer2Result() {
        return new SearchResult("0", -8, 2);
    }
    protected SearchResult getTwoLevelPlayer1Result() {
        return new SearchResult("1", 2, 6);
    }
    protected SearchResult getTwoLevelPlayer2Result() {
        return new SearchResult("0", 7, 6);
    }

    protected SearchResult getTwoLevelQuiescensePlayer1Result() {
        return new SearchResult("0", 3, 12);
    }
    protected SearchResult getTwoLevelQuiescensePlayer2Result() {
        return new SearchResult("1", 4, 12);
    }
    protected SearchResult getTwoLevelQuiescenseABPlayer1Result() {
        return new SearchResult("0", 3, 12);
    }
    protected SearchResult getTwoLevelQuiescenseABPlayer2Result() {
        return new SearchResult("1", 4, 11);
    }
    protected SearchResult getLadderMax3QuiescensePlayer1Result() {
        return new SearchResult("0", 3, 13);
    }
    protected SearchResult getLadderMax3QuiescensePlayer2Result() {
        return new SearchResult("1", 4, 13);
    }
    protected SearchResult getLadderMax4QuiescensePlayer1Result() {
        return new SearchResult("0", 3, 15);
    }
    protected SearchResult getLadderMax4QuiescensePlayer2Result() {
        return new SearchResult("1", 2, 15);
    }

    protected SearchResult getPruneTwoLevelWithoutABResultPlayer1() {
        return new SearchResult("0", 5, 6);
    }
    protected SearchResult getPruneTwoLevelWithABSearchPlayer1() {
        return new SearchResult( "0", 5, 5);
    }
    protected SearchResult getPruneTwoLevelWithABSearchPlayer2() {
        return new SearchResult( "1", 4, 6);
    }
    // probably wrong
    protected SearchResult getPruneFourLevelWithABSearchPlayer1() {
        return new SearchResult( "1", 2, 19);
    }
    // probably wrong
    protected SearchResult getPruneFourLevelWithABSearchPlayer2() {
        return new SearchResult( "1", 5, 20);
    }
    protected SearchResult getThreeLevelPlayer1Result() {
        return new SearchResult("0", -4, 14);
    }
    protected SearchResult getThreeLevelPlayer2Result() {
        return new SearchResult("0", -5, 14);
    }
    protected SearchResult getThreeLevelPlayer1WithABResult() {
        return new SearchResult( "0", -4, 11);
    }
    protected SearchResult getThreeLevelPlayer2WithABResult() {
        return new SearchResult( "0", -5, 13);
    }

    protected SearchResult getFourLevelPlayer1Result() {
        return new SearchResult("0", 27, 30);
    }
    protected SearchResult getFourLevelPlayer2Result() {
        return new SearchResult("1", 14, 30);
    }
    protected SearchResult getFourLevelABPlayer1Result() {
        return new SearchResult("0", 27, 18);
    }
    protected SearchResult getFourLevelABPlayer2Result() {
        return new SearchResult("1", 14, 26);
    }
}