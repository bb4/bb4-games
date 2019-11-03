/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search;

import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.twoplayer.common.AbstractSearchable;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
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
public class SearchableStub extends AbstractSearchable<TwoPlayerMoveStub, TwoPlayerBoard<TwoPlayerMoveStub>> {

    private SearchOptions<TwoPlayerMoveStub, TwoPlayerBoard<TwoPlayerMoveStub>> options_;

    public SearchableStub(SearchOptions<TwoPlayerMoveStub, TwoPlayerBoard<TwoPlayerMoveStub>> options) {
        super(new MoveList<TwoPlayerMoveStub>());
        options_ = options;
    }

    /** Copy constructor */
    public SearchableStub(SearchableStub stub) {
        this(stub.getSearchOptions());
        moveList = new MoveList<>(stub.getMoveList());
    }

    @Override
    public SearchOptions<TwoPlayerMoveStub, TwoPlayerBoard<TwoPlayerMoveStub>> getSearchOptions() {
       return options_;
    }

    /** @return a copy of this instnace */
    @Override
    public Searchable<TwoPlayerMoveStub, TwoPlayerBoard<TwoPlayerMoveStub>> copy() {
        return new SearchableStub(this);
    }

    @Override
    public void makeInternalMove( TwoPlayerMoveStub m )  {
        moveList.add(m);
    }

    @Override
    public void undoInternalMove( TwoPlayerMoveStub m ) {
        moveList.removeLast();
    }

    @Override
    public boolean done( TwoPlayerMoveStub m, boolean recordWin ) {
        return m.getInheritedValue() >= SearchStrategy.WINNING_VALUE;
    }

    @Override
    public int worth(TwoPlayerMoveStub lastMove, ParameterArray weights) {
        return lastMove.getValue();
    }

    @Override
    public TwoPlayerBoard<TwoPlayerMoveStub> getBoard() {
        return null;
    }

    @Override
    public MoveList<TwoPlayerMoveStub> generateMoves(TwoPlayerMoveStub lastMove, ParameterArray weights) {
        return new MoveList<>(lastMove.getChildren());
    }

    @Override
    public MoveList<TwoPlayerMoveStub> generateUrgentMoves(TwoPlayerMoveStub lastMove, ParameterArray weights) {
        MoveList<TwoPlayerMoveStub> urgentMoves = new MoveList<>();
        for (Move m : lastMove.getChildren()) {
            TwoPlayerMoveStub move = (TwoPlayerMoveStub)m;
            if (move.isUrgent())  {
                urgentMoves.add(move);
            }
        }
        return urgentMoves;
    }

    @Override
    public boolean inJeopardy( TwoPlayerMoveStub lastMove, ParameterArray weights) {
        return lastMove.causedUrgency();
    }

    @Override
    public HashKey getHashKey() {
        HashKey key = new HashKey();
        for (Move m : moveList) {
            //key += m.hashCode();
            key.applyMove(((TwoPlayerMoveStub)m).getToLocation(), m.hashCode());
        }
        return key;
    }
}
