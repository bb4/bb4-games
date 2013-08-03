/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy;

import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.SearchWindow;
import com.barrybecker4.game.twoplayer.common.search.Searchable;
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 *  This strategy class defines the NegaMax search algorithm.
 *  Negamax is very much like minimax, but it avoids having separate
 *  sections of code for minimizing and maximizing search.
 *  The game tree it produces should be identical to minimax, but it is slightly more efficient.
 *  @author Barry Becker
 */
public class NegaMaxStrategy extends AbstractBruteSearchStrategy {
    /**
     * Construct NegaMax the strategy given a controller interface.
     */
    public NegaMaxStrategy( Searchable controller, ParameterArray weights ) {
        super( controller , weights);
    }

    @Override
    public TwoPlayerMove search( TwoPlayerMove lastMove, SearchTreeNode parent ) {

        SearchWindow window = getOptions().getBruteSearchOptions().getInitialSearchWindow();
        return searchInternal( lastMove, lookAhead_, new SearchWindow(window.beta, window.alpha), parent);
    }

    @Override
    protected TwoPlayerMove findBestMove(TwoPlayerMove lastMove, int depth, MoveList list,
                                       SearchWindow window, SearchTreeNode parent) {
        int i = 0;
        int bestInheritedValue = -SearchStrategy.INFINITY;
        TwoPlayerMove selectedMove;
        TwoPlayerMove bestMove = (TwoPlayerMove)list.get( 0 );

        while ( !list.isEmpty() ) {
            TwoPlayerMove theMove = getNextMove(list);
            if (pauseInterrupted())
                return lastMove;
            updatePercentDone(depth, list);

            searchable.makeInternalMove( theMove );
            SearchTreeNode child = addNodeToTree(parent, theMove, window); i++;

            selectedMove = searchInternal( theMove, depth-1,
                     new SearchWindow(-window.beta, -Math.max(window.alpha, bestInheritedValue)), child );

            searchable.undoInternalMove( theMove );

            if (selectedMove != null) {
                int selectedValue = -selectedMove.getInheritedValue();
                theMove.setInheritedValue( selectedValue );

                if ( selectedValue > bestInheritedValue ) {
                    bestMove = theMove;
                    bestInheritedValue = selectedValue;
                    if ( alphaBeta_ && bestInheritedValue >= window.beta) {
                        showPrunedNodesInTree(list, parent, i, selectedValue, window);
                        break;
                    }
                }
            }
        }
        bestMove.setSelected(true);
        lastMove.setInheritedValue(-bestMove.getInheritedValue());
        return bestMove;
    }

    @Override
    protected boolean fromPlayer1sPerspective(TwoPlayerMove lastMove) {
        return !lastMove.isPlayer1();
    }
}