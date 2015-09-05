/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.chess.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.checkers.CheckersPiece;
import com.barrybecker4.game.twoplayer.chess.ChessPiece;
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPieceRenderer;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Singleton class that takes a chess piece and renders it for the ChessBoardViewer.
 * @see ChessBoardViewer
 * @author Barry Becker
 */
public class ChessPieceRenderer extends TwoPlayerPieceRenderer
{
    private static TwoPlayerPieceRenderer renderer_ = null;

    private static final Color DEFAULT_PLAYER1_COLOR = new Color( 30, 170, 10); //40, 190, 90 );
    private static final Color DEFAULT_PLAYER2_COLOR = new Color( 170, 0, 180); // 210, 60, 140 );

    // instead of rendering we can just show image icons which look even better.
    // @@ should we instead maintain an array of images indexed by type and player?
    private static ImageIcon[] pawnImage_ = new ImageIcon[2];
    private static ImageIcon[] rookImage_ = new ImageIcon[2];
    private static ImageIcon[] bishopImage_ = new ImageIcon[2];
    private static ImageIcon[] knightImage_ = new ImageIcon[2];
    private static ImageIcon[] queenImage_ = new ImageIcon[2];
    private static ImageIcon[] kingImage_ = new ImageIcon[2];

    private static final String IMAGE_DIR = GameContext.GAME_RESOURCE_ROOT + "twoplayer/chess/ui/images/";
    static {
        // gets the images from resources or the filesystem depending if we are running as an applet or application respectively.
        pawnImage_[0] = GUIUtil.getIcon(IMAGE_DIR + "pawn1.png");
        pawnImage_[1] = GUIUtil.getIcon(IMAGE_DIR + "pawn2.png");

        rookImage_[0] = GUIUtil.getIcon(IMAGE_DIR + "rook1.png");
        rookImage_[1] = GUIUtil.getIcon(IMAGE_DIR + "rook2.png");

        knightImage_[0] = GUIUtil.getIcon(IMAGE_DIR + "knight1.png");
        knightImage_[1] = GUIUtil.getIcon(IMAGE_DIR + "knight2.png");

        bishopImage_[0] = GUIUtil.getIcon(IMAGE_DIR + "bishop1.png");
        bishopImage_[1] = GUIUtil.getIcon(IMAGE_DIR + "bishop2.png");

        queenImage_[0] = GUIUtil.getIcon(IMAGE_DIR + "queen1.png");
        queenImage_[1] = GUIUtil.getIcon(IMAGE_DIR + "queen2.png");

        kingImage_[0] = GUIUtil.getIcon(IMAGE_DIR + "king1.png");
        kingImage_[1] = GUIUtil.getIcon(IMAGE_DIR + "king2.png");
    }

    /**
     * protected constructor because this class is a singleton.
     * Use getRenderer instead
     */
    private ChessPieceRenderer()
    {}

    public static TwoPlayerPieceRenderer getRenderer() {
        if (renderer_ == null)
            renderer_ = new ChessPieceRenderer();
        return renderer_;
    }

    /**
     *  determines what color the player1 pieces should be
     *  ignored if using icons to represent the pieces.
     */
    @Override
    public Color getPlayer1Color() {
        return DEFAULT_PLAYER1_COLOR;
    }

    /**
     *  determines what color the player2 pieces should be
     *  ignored if using icons to represent the pieces.
     */
    @Override
    public Color getPlayer2Color() {
        return DEFAULT_PLAYER2_COLOR;
    }

    /**
     * this draws the actual chess piece
     */
    @Override
    public void renderForShow(Graphics2D g2, Point pos, GamePiece piece, int pieceSize) {

        ChessPiece chessPiece = (ChessPiece) piece;
        int playerIdx = (chessPiece.isOwnedByPlayer1()? 0:1);

        switch (chessPiece.getPieceType()) {
            case PAWN :
                g2.drawImage(pawnImage_[playerIdx].getImage(), pos.x, pos.y, pieceSize, pieceSize, null); break;
            case ROOK :
                g2.drawImage(rookImage_[playerIdx].getImage(), pos.x, pos.y, pieceSize, pieceSize, null); break;
            case KNIGHT :
                g2.drawImage(knightImage_[playerIdx].getImage(), pos.x, pos.y, pieceSize, pieceSize, null); break;
            case BISHOP :
                g2.drawImage(bishopImage_[playerIdx].getImage(), pos.x, pos.y, pieceSize, pieceSize, null); break;
            case QUEEN :
                g2.drawImage(queenImage_[playerIdx].getImage(), pos.x, pos.y, pieceSize, pieceSize, null); break;
            case KING :
                g2.drawImage(kingImage_[playerIdx].getImage(), pos.x, pos.y, pieceSize, pieceSize, null); break;
            default:
                assert false:("bad chess piece type: "+piece.getType());
        }
    }
}
