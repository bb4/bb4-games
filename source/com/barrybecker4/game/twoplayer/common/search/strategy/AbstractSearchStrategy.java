/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.Searchable;
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions;
import com.barrybecker4.game.twoplayer.common.search.tree.IGameTreeViewable;
import com.barrybecker4.game.twoplayer.common.search.tree.NodeAttributes;
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 *  This is an abstract base class for a search strategy.
 *  It's subclasses define the key search algorithms for 2 player zero sum games with perfect information.
 *
 *  @author Barry Becker
 */
public abstract class AbstractSearchStrategy implements SearchStrategy {

    /** the interface implemented by the generic game controller that provides standard methods. */
    protected Searchable searchable;

    /** keep track of the number of moves searched so far. Long because there could be quite a few. */
    protected long movesConsidered = 0;

    /** approximate percent of search that is complete at given moment. */
    protected int percentDone = 0;

    /** weights coefficients for the evaluation polynomial that indirectly determines the best move.   */
    protected ParameterArray weights_;

    /** True when search is paused. */
    private volatile boolean paused_ = false;

    /** The optional ui component that will be updated to reflect the current search tree.  */
    private IGameTreeViewable gameTree_;


    /**
     * Construct the strategy.
     * do not call directly. Use createSearchStrategy factory method instead.
     * @param searchable the game controller that has options and can make/undo moves.
     * @param weights coefficients for the evaluation polynomial that indirectly determines the best move.
     */
    AbstractSearchStrategy( Searchable searchable, ParameterArray weights ) {
        this.searchable = searchable;
        weights_ = weights;
    }

    @Override
    public SearchOptions getOptions() {
        return searchable.getSearchOptions();
    }

    /**
     * Show the node in the game tree (if one is used. It is used if parent not null).
     *
     * @param list of pruned nodes
     * @param parent the tree node entry above the current position.
     * @param i th child.
     * @param attributes name value pairs
     *   type either PRUNE_ALPHA or PRUNE_BETA - pruned by comparison with Alpha or Beta.
     */
    protected void addPrunedNodesInTree( MoveList list, SearchTreeNode parent,
                                          int i, NodeAttributes attributes) {
        if (gameTree_ != null) {
           gameTree_.addPrunedNodes(list, parent, i, attributes);
        }
    }

    /**
     * add a move to the visual game tree (if parent not null).
     * @param parent of the node we are adding to the gameTree
     * @param theMove current move being added.
     * @param attributes arbitrary name value pairs to display for the new node in the tree.
     * @return the node added to the tree.
     */
    protected SearchTreeNode addNodeToTree( SearchTreeNode parent, TwoPlayerMove theMove,
                                            NodeAttributes attributes) {
        SearchTreeNode child = null;
        if (gameTree_ != null) {
            child = new SearchTreeNode(theMove, attributes);
            gameTree_.addNode(parent, child);
        }
        return child;
    }


    /**
     * @return true if the move list is empty.
     */
    protected static boolean emptyMoveList( MoveList list, TwoPlayerMove lastMove ) {
        if ( !list.isEmpty() ) return false;

        //If there are no next moves, the game is over and the last player to move won
        if ( lastMove.isPlayer1() )
            lastMove.setInheritedValue(WINNING_VALUE);
        else
            lastMove.setInheritedValue(-WINNING_VALUE);

        return true;
    }

    @Override
    public final long getNumMovesConsidered() {
        return movesConsidered;
    }

    @Override
    public final int getPercentDone() {
        return percentDone;
    }

    /**
     * Set an optional ui component that will update when the search tree is modified.
     * @param listener game tree listener
     */
    @Override
    public void setGameTreeEventListener(IGameTreeViewable listener) {
        gameTree_ = listener;
    }

    /**
     * For minimax this is always true, but it depends on the player for the nega type searches.
     * @return true if we should evaluate the board from the point of view of player one.
     */
    protected boolean fromPlayer1sPerspective(TwoPlayerMove lastMove) {
        return true;
    }

    protected boolean hasGameTree() {
        return gameTree_ != null;
    }

    // these methods give an external thread debugging controls over the search

    @Override
    public void pause() {
        paused_ = true;
    }


    @Override
    public final boolean isPaused() {
        return paused_;
    }


    @Override
    public void continueProcessing() {
        paused_ = false;
    }

    /**
     * pause if we are paused. Continue when not paused anymore.
     * The pause value is changed by the TwoPlayerBoardViewer
     * @return false right away if not paused. Returns true only if
     *  a long pause has been interrupted.
     */
    boolean pauseInterrupted() {
        try {
            while (paused_) {
                Thread.sleep(100);
            }
            return false;
        } catch (InterruptedException e) {
            GameContext.log(2, "interrupted"); //NON-NLS
            e.printStackTrace();
            return true;
        }
    }
}