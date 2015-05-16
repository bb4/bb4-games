/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search;

import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.twoplayer.common.AbstractSearchable;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy;
import com.barrybecker4.game.twoplayer.common.search.transposition.HashKey;
import com.barrybecker4.optimization.parameter.ParameterArray;


/**
 * Stub implementation of Searchable to help test the search strategy classes without needing
 * a specific game implementation.
 *
 * @author Barry Becker
 */
public class SearchableStub extends AbstractSearchable<TwoPlayerMove, TwoPlayerBoard<TwoPlayerMove>> {

    private SearchOptions options_;

    public SearchableStub(SearchOptions options) {
        super(new MoveList<TwoPlayerMove>());
        options_ = options;
    }

    /** Copy constructor */
    public SearchableStub(SearchableStub stub) {
        this(stub.getSearchOptions());
        moveList_ = new MoveList<>(stub.getMoveList());
    }

    @Override
    public SearchOptions getSearchOptions() {
       return options_;
    }

    /** @return a copy of this instnace */
    @Override
    public Searchable<TwoPlayerMove, TwoPlayerBoard<TwoPlayerMove>> copy() {
        return new SearchableStub(this);
    }

    @Override
    public void makeInternalMove( TwoPlayerMove m )  {
        moveList_.add(m);
    }

    @Override
    public void undoInternalMove( TwoPlayerMove m ) {
        moveList_.removeLast();
    }

    @Override
    public boolean done( TwoPlayerMove m, boolean recordWin ) {
        return m.getInheritedValue() >= SearchStrategy.WINNING_VALUE;
    }

    @Override
    public int worth(TwoPlayerMove lastMove, ParameterArray weights) {
        return lastMove.getValue();
    }

    /*
    public int worth(Move lastMove, ParameterArray weights, boolean player1sPerspective) {
        return lastMove.getValue();
    } */

    @Override
    public TwoPlayerBoard<TwoPlayerMove> getBoard() {
        return null;
    }

    @Override
    public MoveList<TwoPlayerMove> generateMoves(TwoPlayerMove lastMove, ParameterArray weights) {
        return new MoveList<>(((TwoPlayerMoveStub) lastMove).getChildren());
    }

    @Override
    public MoveList<TwoPlayerMove> generateUrgentMoves(TwoPlayerMove lastMove, ParameterArray weights) {
        MoveList<TwoPlayerMove> urgentMoves = new MoveList<>();
        for (Move m : ((TwoPlayerMoveStub) lastMove).getChildren()) {
            TwoPlayerMove move = (TwoPlayerMove)m;
            if (move.isUrgent())  {
                urgentMoves.add(move);
            }
        }
        return urgentMoves;
    }

    @Override
    public boolean inJeopardy( TwoPlayerMove lastMove, ParameterArray weights) {
        return ((TwoPlayerMoveStub)lastMove).causedUrgency();
    }

    @Override
    public HashKey getHashKey() {
        HashKey key = new HashKey();
        for (Move m : moveList_) {
            //key += m.hashCode();
            key.applyMove(((TwoPlayerMove)m).getToLocation(), m.hashCode());
        }
        return key;
    }
}
