/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.twoplayer.common.TwoPlayerController;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.TwoPlayerOptions;
import com.barrybecker4.game.twoplayer.common.TwoPlayerPlayerOptions;
import com.barrybecker4.game.twoplayer.common.search.options.BestMovesSearchOptions;
import com.barrybecker4.game.twoplayer.common.search.options.BruteSearchOptions;
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions;
import com.barrybecker4.optimization.parameter.ParameterArray;

import java.util.List;


/**
 * Verify that all the methods in the Searchable interface work as expected.
 * Derived test classes will exercise these methods for specific game instances.
 * @author Barry Becker
 */
@SuppressWarnings({"ClassWithTooManyMethods", "UnusedDeclaration"})
public abstract class TwoPlayerSearchableBaseTst extends SearchableBaseTst {

    private static final int DEFAULT_DEBUG_LEVEL = 2;
    private static final int DEFAULT_LOOKAHEAD = 2;
    private static final int DEFAULT_BEST_PERCENTAGE = 100;

    private TwoPlayerController controller;
    private SearchOptions searchOptions_;

    /**
     * common initialization for all go test cases.
     * Override setOptionOverides if you want different search parameters.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        searchable = getController().getSearchable();

        setPlayerSearchOptions();

        GameContext.setDebugMode(getDebugLevel());
    }

    private void setPlayerSearchOptions() {

        searchOptions_ = createDefaultOptions();
        PlayerList players = controller.getPlayers();
        ((TwoPlayerPlayerOptions) players.getPlayer1().getOptions()).setSearchOptions(searchOptions_);
        ((TwoPlayerPlayerOptions) players.getPlayer2().getOptions()).setSearchOptions(searchOptions_);
    }

    @Override
    protected abstract ISearchableHelper createSearchableHelper();

    protected SearchOptions createDefaultOptions() {
        SearchOptions options = helper.createSearchOptions();
        options.getBestMovesSearchOptions().setPercentageBestMoves(DEFAULT_BEST_PERCENTAGE);
        options.getBestMovesSearchOptions().setPercentLessThanBestThresh(0);
        options.getBestMovesSearchOptions().setMinBestMoves(2);
        BruteSearchOptions sOptions = options.getBruteSearchOptions();
        sOptions.setLookAhead(DEFAULT_LOOKAHEAD);
        sOptions.setAlphaBeta(true);

        sOptions.setQuiescence(false);
        return options;
    }

    /**
     * Restore a game file
     * @param problemFileBase the saved game to restore and test.
     */
    protected void restore(String problemFileBase) throws Exception {
        getController().restoreFromStream(helper.getTestResource(problemFileBase));
    }

    protected TwoPlayerController getController() {
        if (controller == null)
            controller = helper.createController();
        return controller;
    }

    /**
     * @return an initial move by player one.
     */
    protected abstract TwoPlayerMove createInitialMove();


    protected int getDebugLevel() {
        return DEFAULT_DEBUG_LEVEL;
    }

    protected TwoPlayerOptions getTwoPlayerOptions()  {
        return  (TwoPlayerOptions)getController().getOptions();
    }

    protected SearchOptions getSearchOptions() {
        return searchOptions_;
    }

    protected BestMovesSearchOptions getBestMovesOptions() {
        return searchOptions_.getBestMovesSearchOptions();
    }

    /** verify that we can retrieve the lookahead value. */
    @Override
    public void testLookaheadValue() throws Exception {

        BruteSearchOptions opts = getSearchOptions().getBruteSearchOptions();
        assertEquals("Unexpected lookahead value.", DEFAULT_LOOKAHEAD, opts.getLookAhead());
        opts.setLookAhead(7);
        assertEquals("Unexpected lookahead value.", 7, opts.getLookAhead());
    }

    /** verify that we can retrieve the lookahead value. */
    @Override
    public void testAlphaBetaValue() throws Exception{

        BruteSearchOptions opts = getSearchOptions().getBruteSearchOptions();
        assertEquals("Unexpected alphabeta value.", true, opts.getAlphaBeta());
        opts.setAlphaBeta(false);
        assertEquals("Unexpected alphabeta value.", false, opts.getAlphaBeta());
    }

    /** verify that we can retrieve the quiescence value. */
    @Override
    public void testQuiescenceValue() throws Exception {
        BruteSearchOptions opts = getSearchOptions().getBruteSearchOptions();
        assertEquals("Unexpected quiessence value.", false, opts.getQuiescence());
        opts.setQuiescence(true);
        assertEquals("Unexpected quiessence value.", true, opts.getQuiescence());
    }


    /**verify that we are not done if we are at the very start of the game.  */
    public void testDoneBeforeAnyMovesMade() throws Exception{

        assertFalse("We cannot be done if no moves have been made yet. ",
                searchable.done(null, false));
    }

    /** If next move is null, and we have at least one move made, then done will be true.  */
    public void testDoneNullAfterFirstMove() throws Exception {

        controller.computerMovesFirst();

        assertTrue("We expect to be done if our next move is null and at least one move has been made. ",
                searchable.done(null, false));
    }

    /** Verify not done after first move.  */
    public void testDoneStartGame() {
        assertFalse("We don't expect to be done after making the very first move. ",
               searchable.done(createInitialMove(), false));
    }

    /**  Load a game in the middle and verify that a legal midgame move doesn't return true.  */
    public abstract void testNotDoneMidGame() throws Exception;

    /** Load a game that was won in the middle and verify that done returns true.  */
    public abstract void testDoneForMidGameWin() throws Exception;

    /** Load a game that does not have any more valid moves and verify that done == true  */
    public abstract void testDoneEndGame() throws Exception;

    /**
     * Verify that we generate a reasonable list of moves to try next.
     * check that we can generate a list of initial moves and do not fail when the last move is null.
     */
   public void testGenerateMovesBeforeFirstMove() {

       List moves = searchable.generateMoves(null, weights());
       assertTrue("We expect the move list to be non-null at the very start of the game.", moves!= null);

       // usually we have a special way to generate the first move (see computerMovesFirst).
       int exp = getExpectedNumGeneratedMovesBeforeFirstMove();
       assertEquals("Unexpected number of generated moves before the first move has been played.",
               exp, moves.size() );
   }

   protected int getExpectedNumGeneratedMovesBeforeFirstMove() {
       return 0;
   }

   /**
     * Verify that we generate a reasonable list of moves to try next.
     * check that we can generate a list of initial moves and do not fail after the very first move.
     */
   public void testGenerateMovesAfterFirstMove() {
       controller.computerMovesFirst();
       ParameterArray wts = weights();
       TwoPlayerMove lastMove = (TwoPlayerMove)controller.getLastMove();
       List moves = searchable.generateMoves(lastMove, wts);

       assertTrue("We expect the move list to be non-null very start of the game.", moves!= null);
       assertTrue("We expected some valid next moves at the very start of the game.",  moves.size() > 0);
   }

    /**  Load a game in the middle and verify that we can get all the reasonable next moves. */
   public void testGenerateAllP1MovesMidGame() throws Exception {
       assertFalse(false);
   }

   /**  Load a game in the middle and verify that we can get the expected high value next moves. */
   public void testGenerateTopP1MovesMidGame() throws Exception {
       assertFalse(false);
   }

    /** Load a game at the end and verify that we can get all the reasonable next moves. */
   public void testGenerateAllP1MovesEndGame() throws Exception {
       assertFalse(false);
   }

   /** Load a game at the end and verify that we can get all the high value next moves. */
   public void testGenerateTopP1MovesEndGame() throws Exception {
       assertFalse(false);
   }

   /**  Load a game in the middle and verify that we can get all the reasonable next moves. */
   public void testGenerateAllP2MovesMidGame() throws Exception {
       assertFalse(false);
   }

   /**  Load a game in the middle and verify that we can get the expected high value next moves. */
   public void testGenerateTopP2MovesMidGame() throws Exception {
       assertFalse(false);
   }

   /** Load a game at the end and verify that we can get all the reasonable next moves. */
   public void testGenerateAllP2MovesEndGame() throws Exception {
       assertFalse(false);
   }

   /** Load a game at the end and verify that we can get all the high value next moves. */
   public void testGenerateTopP2MovesEndGame() throws Exception {
       assertFalse(false);
   }

   /**  There should not be any urgent moves at the very start of the game.  */
   public void  testGenerateUrgentMovesAtStartOfGame() {
         List moves = searchable.generateUrgentMoves(null, weights());
         assertTrue("We expected move list to be non-null.",
                 moves != null );
         assertTrue("We expected no urgent moves at the start of the game, but was:" + moves,
                 moves.size() == 0);
    }

   /**  Verify that we generate a correct list of urgent moves.  */
   public void  testGenerateUrgentMoves() throws Exception {
         assertFalse(false);
         // load a typical game in the beginning and verify that there are no urgent next moves.

         // load a critical game in the middle and verify that there are urgent next moves.

         // load a game at the end and verify that there are no urgent next moves.
    }

    /**  Verify that we can detect when a player is in jeopardy. */
    public void testInJeopardy() {
        boolean actualInJeopardy =
                searchable.inJeopardy(null, weights());
        assertFalse("We don't expect anything to be in jeopardy at the very start of the game.", actualInJeopardy);

        TwoPlayerMove lastMove = createInitialMove();
        searchable.makeInternalMove(lastMove);
        actualInJeopardy =
                searchable.inJeopardy(lastMove,
                                      getController().getComputerWeights().getPlayer2Weights());
        // fix
        ////assertFalse("We don't expect anything to be in jeopardy at the very start of the game.", actualInJeopardy);

        // todo
        // load a typical game in the middle and verify a move that does not put anything in jeopardy.
        // load a critical game in the middle and verify a move that does put the other player in jeopardy.
    }


    protected void checkMoveListAgainstExpected(String title, TwoPlayerMove[] expectedMoves,
                                                MoveList moves) {
        if (expectedMoves.length != moves.size()) {
            printMoves(title, moves);
        }

        assertEquals("Unexpected number of generated moves.",
                expectedMoves.length, moves.size());

        StringBuilder diffs = new StringBuilder("");
        for (int i=0; i<moves.size(); i++) {
            TwoPlayerMove move = (TwoPlayerMove) moves.get(i);
            TwoPlayerMove expMove = expectedMoves[i];
            if (!move.equals(expMove)) {
                diffs.append(i);
                diffs.append(") Unexpected moves.\n Expected ");
                diffs.append(expMove);
                diffs.append(" \nBut got ");
                diffs.append(move);
                diffs.append("\n");
            }
        }
        if (diffs.length() > 0) {
            printMoves( title, moves);
        }
        assertTrue("There were unexpected generated moves for " + title +"\n" + diffs,
                    diffs.length() == 0);
    }

    protected ParameterArray weights() {
       return getController().getComputerWeights().getPlayer1Weights();
    }

    protected void printMoves(String name, MoveList moves) {
        System.out.println("generated moves for "+ name + " were:" );
        for (Move m : moves) {
             System.out.println(((TwoPlayerMove)m).getConstructorString());
        }
    }
}
