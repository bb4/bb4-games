/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.hex;

import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.TwoPlayerSearchable;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * Defines everything the computer needs to know to play Hex.
 *
 * @author Barry Becker
*/
public class HexSearchable extends TwoPlayerSearchable<TwoPlayerMove, HexBoard> {

    /**
     *  Constructor
     */
    public HexSearchable(HexBoard board, PlayerList players) {
        super(board, players);
    }

    public HexSearchable(HexSearchable searchable) {
        super(searchable);
    }

    @Override
    public HexSearchable copy() {
        return new HexSearchable(this);
    }


    @Override
    public int worth(TwoPlayerMove lastMove, ParameterArray weights) {
        // TODO
        return 0;
    }

    @Override
    public MoveList<TwoPlayerMove> generateMoves(TwoPlayerMove lastMove, ParameterArray weights) {
        // TODO
        return null;
    }

    @Override
    public MoveList<TwoPlayerMove> generateUrgentMoves(TwoPlayerMove lastMove, ParameterArray weights) {
        return null;
    }
}
