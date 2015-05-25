/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy;

import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.search.SearchWindow;
import com.barrybecker4.game.twoplayer.common.search.Searchable;
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * This strategy class defines the MiniMax search algorithm.
 * This is the simplest search strategy to which the other variants are compared.
 * @author Barry Becker
 */
public final class MiniMaxStrategy<M extends TwoPlayerMove, B extends TwoPlayerBoard<M>>
        extends AbstractBruteSearchStrategy<M, B> {

    /**
     * Constructor for the strategy.
     */
    public MiniMaxStrategy(Searchable<M, B> controller, ParameterArray weights) {
        super(controller, weights);
    }

    @Override
    protected M findBestMove(M lastMove, int depth, MoveList<M> list,
                             SearchWindow window, SearchTreeNode parent) {
        int i = 0;
        int selectedValue;
        M selectedMove;
        // if player 1, then search for a high score, else search for a low score.
        boolean player1 = lastMove.isPlayer1();
        int bestInheritedValue = player1 ? SearchStrategy.INFINITY: -SearchStrategy.INFINITY;
        //System.out.println("(depth = " + depth + ")"+window+"  Find best moves among \n" + list);

        M bestMove = list.get(0);
        while (!list.isEmpty()) {
            if (pauseInterrupted())
                return lastMove;

            M theMove = getNextMove(list);
            updatePercentDone(depth, list);

            searchable.makeInternalMove( theMove );
            SearchTreeNode child = addNodeToTree(parent, theMove, window);
            i++;

            // recursive call
            selectedMove = searchInternal( theMove, depth-1, window.copy(), child );

            searchable.undoInternalMove( theMove );

            if (selectedMove != null) {
                selectedValue = selectedMove.getInheritedValue();
                if ( player1 ) {
                    if ( selectedValue < bestInheritedValue ) {
                        bestMove = theMove;
                        bestInheritedValue = bestMove.getInheritedValue();
                    }
                }
                else if ( selectedValue > bestInheritedValue ) {
                    bestMove = theMove;
                    bestInheritedValue = bestMove.getInheritedValue();
                }

                if (alphaBeta_ && pruneAtCurrentNode(window, selectedValue, player1)) {
                    showPrunedNodesInTree(list, parent, i, selectedValue, window);
                    break;
                }
            }
        }

        //System.out.println("(" + depth + ")Best move selected = " + bestMove);
        bestMove.setSelected(true);
        lastMove.setInheritedValue(bestMove.getInheritedValue());
        return bestMove;
    }

    /**
     * Note: The SearchWindow may be adjusted as a side effect.
     * @return  whether or not we should prune the current subtree.
     */
    private boolean pruneAtCurrentNode(SearchWindow window, int selectedValue, boolean player1) {
        if (player1 && (selectedValue < window.alpha)) {
            if ( selectedValue < window.beta ) {
                return true;
            }
            else {
                window.alpha = selectedValue;
            }
        }
        if (!player1 && (selectedValue > window.beta)) {
            if ( selectedValue > window.alpha ) {
                return true;
            }
            else {
                window.beta = selectedValue;
            }
        }
        return false;
    }

    @Override
    protected boolean fromPlayer1sPerspective(M lastMove) {
        return true;
    }

    @Override
    public EvaluationPerspective getEvaluationPerspective() {
        return EvaluationPerspective.ALWAYS_PLAYER1;
    }
}