/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.persistence;

import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.twoplayer.blockade.BlockadeController;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition;
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove;
import com.barrybecker4.game.twoplayer.common.persistence.TwoPlayerGameExporter;

/**
 * Exports the state of a Blockade game to a file.
 *
 * @author Barry Becker Date: Oct 28, 2006
 */
public class BlockadeGameExporter extends TwoPlayerGameExporter {


    public BlockadeGameExporter(BlockadeController controller) {
        super(controller);
    }


    /**
     * return the SGF (4) representation of the move
     * SGF stands for Smart Game Format and is commonly used for Go
     */
    @Override
    protected String getSgfForMove(Move move) {
        BlockadeMove m = (BlockadeMove) move;
        // passes are not represented in SGF - so just skip it if the piece is null.
        if (m.getPiece() == null)
             return "[]";
        StringBuilder buf = new StringBuilder("");
        String player = "P2";
        if ( m.getPiece().isOwnedByPlayer1() )
        {
            player = "P1";
        }
        buf.append( ';' );
        buf.append( player );
         buf.append( '[' );
        buf.append( (char) ('a' + m.getFromCol() - 1) );
        buf.append( (char) ('a' + m.getFromRow() - 1) );
        buf.append( ']' );
        buf.append( '[' );
        buf.append( (char) ('a' + m.getToCol() - 1) );
        buf.append( (char) ('a' + m.getToRow() - 1) );
        buf.append( ']' );
        // also print the wall placement if there is one
        if (m.getWall() != null) {
            buf.append("wall");
            for (BlockadeBoardPosition pos : m.getWall().getPositions()) {
                serializePosition(pos.getLocation(), buf);
            }
        }
        else {
            buf.append("nowall");
        }

        buf.append( '\n' );
        return buf.toString();
    }

}
