/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.persistence;

import com.barrybecker4.game.twoplayer.blockade.BlockadeController;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition;
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.common.persistence.TwoPlayerGameExporter;

/**
 * Exports the state of a Blockade game to a file.
 *
 * @author Barry Becker Date: Oct 28, 2006
 */
public class BlockadeGameExporter extends TwoPlayerGameExporter<BlockadeMove, BlockadeBoard> {

    public BlockadeGameExporter(BlockadeController controller) {
        super(controller);
    }


    /**
     * return the SGF (4) representation of the move
     * SGF stands for Smart Game Format and is commonly used for Go
     */
    @Override
    protected String getSgfForMove(BlockadeMove move) {

        // passes are not represented in SGF - so just skip it if the piece is null.
        if (move.getPiece() == null)
             return "[]";
        StringBuilder buf = new StringBuilder("");
        String player = "P2";
        if ( move.getPiece().isOwnedByPlayer1() )
        {
            player = "P1";
        }
        buf.append( ';' );
        buf.append( player );
         buf.append( '[' );
        buf.append( (char) ('a' + move.getFromCol() - 1) );
        buf.append( (char) ('a' + move.getFromRow() - 1) );
        buf.append( ']' );
        buf.append( '[' );
        buf.append( (char) ('a' + move.getToCol() - 1) );
        buf.append( (char) ('a' + move.getToRow() - 1) );
        buf.append( ']' );
        // also print the wall placement if there is one
        if (move.getWall() != null) {
            buf.append("wall");
            for (BlockadeBoardPosition pos : move.getWall().getPositions()) {
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
