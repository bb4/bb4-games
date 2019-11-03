/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.persistence;

import com.barrybecker4.ca.dj.jigo.sgf.SGFGame;
import com.barrybecker4.ca.dj.jigo.sgf.SGFLoader;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.InfoToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.SGFToken;
import com.barrybecker4.ca.dj.jigo.sgf.tokens.TextToken;
import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.blockade.BlockadeController;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition;
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.blockade.board.move.wall.BlockadeWall;
import com.barrybecker4.game.twoplayer.blockade.persistence.tokens.BlockadeMoveToken;
import com.barrybecker4.game.twoplayer.blockade.persistence.tokens.Player1BlockadeMoveToken;
import com.barrybecker4.game.twoplayer.common.persistence.TwoPlayerGameImporter;
import com.barrybecker4.game.twoplayer.common.persistence.tokens.Player1NameToken;
import com.barrybecker4.game.twoplayer.common.persistence.tokens.Player2NameToken;
import com.barrybecker4.game.twoplayer.common.persistence.tokens.Size2Token;

import java.util.Enumeration;

/**
 * Imports the stat of a Go game from a file.
 *
 * @author Barry Becker Date
 */
public class BlockadeGameImporter extends TwoPlayerGameImporter<BlockadeMove, BlockadeBoard> {

    public BlockadeGameImporter(BlockadeController controller) {
        super(controller);
    }

    @Override
     protected SGFLoader createLoader() {
        return new BlockadeSGFLoader();
    }


    /**
     * Initialize the board based on the SGF game.
     */
    @Override
    protected void parseSGFGameInfo( SGFGame game) {

        BlockadeController gc = (BlockadeController) controller_;

        Enumeration e = game.getInfoTokens();
        int numRows = 15; // default unless specified
        int numCols = 12; // default unless specified
        while (e.hasMoreElements()) {
            InfoToken token = (InfoToken) e.nextElement();
            if (token instanceof Size2Token) {
                Size2Token sizeToken = (Size2Token)token;
                numRows = sizeToken.getNumRows();
                numCols = sizeToken.getNumColumns();
            }
            else if (token instanceof Player2NameToken) {
                Player2NameToken nameToken = (Player2NameToken) token;
                gc.getPlayers().getPlayer2().setName(nameToken.getName());
            }
            else if (token instanceof Player1NameToken) {
                Player1NameToken nameToken = (Player1NameToken) token;
                gc.getPlayers().getPlayer1().setName(nameToken.getName());
            }
        }
        gc.getBoard().setSize(numRows, numCols);
    }

    /**
     *
     */
    @Override
    protected boolean processToken(SGFToken token, MoveList<BlockadeMove> moveList) {

        boolean found = false;
        if (token instanceof BlockadeMoveToken ) {
            moveList.add( createMoveFromToken( token ) );
            found = true;
        }
        else if (token instanceof TextToken ) {
            TextToken textToken = (TextToken) token;
            GameContext.log(1, "text=" + textToken.getText());
        } else {
            GameContext.log(1, "\nignoring token "+token.getClass().getName());
        }
        return found;
    }


    /**
     * Create a cblockade more from the MoveToken
     */
    @Override
    protected BlockadeMove createMoveFromToken( SGFToken token) {

         BlockadeMoveToken mvToken = (BlockadeMoveToken) token;

         boolean player1 = token instanceof Player1BlockadeMoveToken;
         BlockadeWall wall = null;
         if (mvToken.hasWall())
             wall = new BlockadeWall(new BlockadeBoardPosition(mvToken.getWallPoint1().y, mvToken.getWallPoint1().x),
                                                       new BlockadeBoardPosition(mvToken.getWallPoint2().y, mvToken.getWallPoint2().x));

         return BlockadeMove.createMove(new ByteLocation(mvToken.getFromY(), mvToken.getFromX()),
                 new ByteLocation(mvToken.getToY(), mvToken.getToX()),
                 0, new GamePiece(player1), wall);
    }

}
