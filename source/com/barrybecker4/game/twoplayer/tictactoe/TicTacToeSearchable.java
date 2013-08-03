/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.tictactoe;

import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.pente.PenteSearchable;
import com.barrybecker4.game.twoplayer.pente.pattern.Patterns;

/**
 * Defines everything the computer needs to know to play TicTacToe.
 *
 * @author Barry Becker
*/
public class TicTacToeSearchable extends PenteSearchable {

    /**
     *  Constructor
     */
    public TicTacToeSearchable(TwoPlayerBoard board, PlayerList players) {
        super(board, players);
    }

    public TicTacToeSearchable(PenteSearchable searchable) {
        super(searchable);
    }

    @Override
    protected Patterns createPatterns() {
        return new TicTacToePatterns();
    }

    @Override
    public TicTacToeSearchable copy() {
        return new TicTacToeSearchable(this);
    }

    @Override
    protected int getJeopardyWeight()  {
        return TicTacToeWeights.JEOPARDY_WEIGHT;
    }
}
