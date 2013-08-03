/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.twoplayer.common.search.examples.EvaluationPerspective;
import com.barrybecker4.game.twoplayer.common.search.examples.FourLevelGameTreeExample;
import com.barrybecker4.game.twoplayer.common.search.examples.OneLevelGameTreeExample;
import com.barrybecker4.game.twoplayer.common.search.examples.TwoLevelGameTreeExample;
import com.barrybecker4.game.twoplayer.common.search.options.MonteCarloSearchOptions;
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions;

/**
 * Test minimax strategy independent of any particular game implementation.
 *
 * @author Barry Becker
 */
public abstract class MonteCarloSearchStrategyTst extends AbstractSearchStrategyTst {

    protected MonteCarloSearchOptions monteCarloOptions;


    @Override
   protected void setUp() throws Exception {
       super.setUp();
       monteCarloOptions = searchOptions.getMonteCarloSearchOptions();
       GameContext.setRandomSeed(0);
   }

   /**
    * @return default search options for all games
    */
   @Override
   public SearchOptions createSearchOptions() {
       SearchOptions opts = super.createSearchOptions();
       // consider all moves (effectively)
       opts.getBestMovesSearchOptions().setMinBestMoves(100);

       MonteCarloSearchOptions options = opts.getMonteCarloSearchOptions();
       options.setExploreExploitRatio(1.0);
       options.setMaxSimulations(10);
       return opts;
   }


    @Override
    protected EvaluationPerspective getEvaluationPerspective() {
        return EvaluationPerspective.ALWAYS_PLAYER1;
    }


    /** It fails if you try with just one random simulation. */
    public void testTwoSimulationSearch1LevelPlayer1() {
        monteCarloOptions.setMaxSimulations(2);
        verifyResult(new OneLevelGameTreeExample(true, getEvaluationPerspective()),
                getTwoSimulationSearch1LevelPlayer1Result());
    }
    public void testTwoSimulationSearch1LevelPlayer2() {
        monteCarloOptions.setMaxSimulations(2);
        verifyResult(new OneLevelGameTreeExample(false, getEvaluationPerspective()),
                getTwoSimulationSearch1LevelPlayer2Result());
    }

    /** It fails if you try with just one random simulation. */
    public void testTwoSimulationSearch2LevelPlayer1() {
        monteCarloOptions.setMaxSimulations(2);
        verifyResult(new TwoLevelGameTreeExample(true, getEvaluationPerspective()),
                getTwoSimulationSearch2LevelPlayer1Result());
    }
    public void testTwoSimulationSearch2LevelPlayer2() {
        monteCarloOptions.setMaxSimulations(2);
        verifyResult(new TwoLevelGameTreeExample(false, getEvaluationPerspective()),
                getTwoSimulationSearch2LevelPlayer2Result());
    }

    /** It fails if you try with just one random simulation. */
    public void testTwoSimulationSearch4LevelPlayer1() {
        monteCarloOptions.setMaxSimulations(2);
        verifyResult(new FourLevelGameTreeExample(true, getEvaluationPerspective()),
                getTwoSimulationSearch4LevelPlayer1Result());
    }
    public void testTwoSimulationSearch4LevelPlayer2() {
        monteCarloOptions.setMaxSimulations(2);
        verifyResult(new FourLevelGameTreeExample(false, getEvaluationPerspective()),
                getTwoSimulationSearch4LevelPlayer2Result());
    }

    public void testFiveSimulationSearch2LevelPlayer1() {
        monteCarloOptions.setMaxSimulations(5);
        verifyResult(new TwoLevelGameTreeExample(true, getEvaluationPerspective()),
                getFiveSimulationSearch2LevelPlayer1Result());
    }
    public void testFiveSimulationSearch2LevelPlayer2() {
        monteCarloOptions.setMaxSimulations(5);
        verifyResult(new TwoLevelGameTreeExample(false, getEvaluationPerspective()),
                getFiveSimulationSearch2LevelPlayer2Result());
    }

    public void testFiveSimulationSearch4LevelPlayer1() {
        monteCarloOptions.setMaxSimulations(5);
        verifyResult(new FourLevelGameTreeExample(true, getEvaluationPerspective()),
                getFiveSimulationSearch4LevelPlayer1Result());
    }
    public void testFiveSimulationSearch4LevelPlayer2() {
        monteCarloOptions.setMaxSimulations(5);
        verifyResult(new FourLevelGameTreeExample(false, getEvaluationPerspective()),
                getFiveSimulationSearch4LevelPlayer2Result());
    }

    public void testTenSimulationSearch2LevelPlayer1() {
        monteCarloOptions.setMaxSimulations(10);
        verifyResult(new TwoLevelGameTreeExample(true, getEvaluationPerspective()),
                getTenSimulationSearch2LevelPlayer1Result());
    }
    public void testTenSimulationSearch2LevelPlayer2() {
        monteCarloOptions.setMaxSimulations(10);
        verifyResult(new TwoLevelGameTreeExample(false, getEvaluationPerspective()),
                getTenSimulationSearch2LevelPlayer2Result());
    }

    public void testTenSimulationSearch4LevelPlayer1() {
        monteCarloOptions.setMaxSimulations(10);
        verifyResult(new FourLevelGameTreeExample(true, getEvaluationPerspective()),
                getTenSimulationSearch4LevelPlayer1Result());
    }
    public void testTenSimulationSearch4LevelPlayer2() {
        monteCarloOptions.setMaxSimulations(10);
        verifyResult(new FourLevelGameTreeExample(false, getEvaluationPerspective()),
                getTenSimulationSearch4LevelPlayer2Result());
    }

    public void testFiftySimulationSearch2LevelPlayer1() {
        monteCarloOptions.setMaxSimulations(10);
        verifyResult(new TwoLevelGameTreeExample(true, getEvaluationPerspective()),
                getFiftySimulationSearch2LevelPlayer1Result());
    }
    public void testFiftySimulationSearch2LevelPlayer2() {
        monteCarloOptions.setMaxSimulations(10);
        verifyResult(new TwoLevelGameTreeExample(false, getEvaluationPerspective()),
                getFiftySimulationSearch2LevelPlayer2Result());
    }

    public void testFiftySimulationSearch4LevelPlayer1() {
        monteCarloOptions.setMaxSimulations(50);
        verifyResult(new FourLevelGameTreeExample(true, getEvaluationPerspective()),
                getFiftySimulationSearch4LevelPlayer1Result());
    }
    public void testFiftySimulationSearch4LevelPlayer2() {
        monteCarloOptions.setMaxSimulations(50);
        verifyResult(new FourLevelGameTreeExample(false, getEvaluationPerspective()),
                getFiftySimulationSearch4LevelPlayer2Result());
    }


    // the following expected results are for UCT search. Derivations may differ.

    protected SearchResult getTwoSimulationSearch1LevelPlayer1Result() {
        return new SearchResult("1", -2, 2);     // Should be "0", but not enough sims to be accurate.
    }
    protected SearchResult getTwoSimulationSearch1LevelPlayer2Result() {
        return new SearchResult("1", -2, 2);
    }

    protected SearchResult getTwoSimulationSearch2LevelPlayer1Result() {
        return new SearchResult("1", -2, 2);
    }
    protected SearchResult getTwoSimulationSearch2LevelPlayer2Result() {
        return new SearchResult("0", -8, 2);
    }

    protected SearchResult getTwoSimulationSearch4LevelPlayer1Result() {
        return new SearchResult("1", -2, 2);
    }
    protected SearchResult getTwoSimulationSearch4LevelPlayer2Result() {
        return new SearchResult("0", -8, 2);
    }

    protected SearchResult getFiveSimulationSearch2LevelPlayer1Result() {
        return new SearchResult("1", -2, 5);
    }
    protected SearchResult getFiveSimulationSearch2LevelPlayer2Result() {
        return new SearchResult("0", -8, 5);
    }

    protected SearchResult getFiveSimulationSearch4LevelPlayer1Result() {
        return new SearchResult("0", -8, 5);
    }
    protected SearchResult getFiveSimulationSearch4LevelPlayer2Result() {
        return new SearchResult("1", -2, 5);
    }

    protected SearchResult getTenSimulationSearch2LevelPlayer1Result() {
        return new SearchResult("1", -2, 7);
    }
    protected SearchResult getTenSimulationSearch2LevelPlayer2Result() {
        return new SearchResult("0", -8, 7);
    }

    protected SearchResult getTenSimulationSearch4LevelPlayer1Result() {
        return new SearchResult("0", -8, 10);
    }
    protected SearchResult getTenSimulationSearch4LevelPlayer2Result() {
        return new SearchResult("1", -2, 10);
    }

    protected SearchResult getFiftySimulationSearch2LevelPlayer1Result() {
        return new SearchResult("1", -2, 7);
    }
    protected SearchResult getFiftySimulationSearch2LevelPlayer2Result() {
        return new SearchResult("0", -8, 7);
    }

    protected SearchResult getFiftySimulationSearch4LevelPlayer1Result() {
        return new SearchResult("0", -8, 31);
    }
    protected SearchResult getFiftySimulationSearch4LevelPlayer2Result() {
        return new SearchResult("1", -2, 31);
    }
}

