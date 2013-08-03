/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.chess.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer;
import com.barrybecker4.game.common.ui.viewer.ViewerMouseListener;
import com.barrybecker4.game.twoplayer.checkers.ui.CheckersBoardViewer;
import com.barrybecker4.game.twoplayer.chess.ChessBoard;
import com.barrybecker4.game.twoplayer.chess.ChessController;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;

import javax.swing.*;


/**
 *  This class takes a ChessController as input and displays the
 *  Current state of the Chess Game. The ChessController contains a ChessBoard
 *  which describes this state.
 *  Since the chess board is very much like the checkers board viewer, we derive from that
 *  @see CheckersBoardViewer
 *
 *  @author Barry Becker
 */
public class ChessBoardViewer extends CheckersBoardViewer
{
    /**
     * Construct the viewer
     */
    public ChessBoardViewer()
    {}


    @Override
    protected GameController createController() {
        return new ChessController();
    }

    @Override
    protected ViewerMouseListener createViewerMouseListener() {
        return new ChessViewerMouseListener(this);
    }

    @Override
    protected GameBoardRenderer getBoardRenderer() {
        return ChessBoardRenderer.getRenderer();
    }

    /**
     * Some moves require that the human players be given some kind of notification.
     * We need to see if this move caused the opponents king to be put in check.
     * We need to check all of our pieces, not just the one moved, since the movement of one
     * piece may cause another (that was previously blocked by the piece we just moved) to put
     * the opponents king in jeopardy.
     * @param m the last move made
     */
    @Override
    public void warnOnSpecialMoves( TwoPlayerMove m )
    {
        super.warnOnSpecialMoves(m);
        // we don't show dialogs if both players are computers.
        if (get2PlayerController().getPlayers().allPlayersComputer())
            return;

        int row, col;
        ChessBoard b = (ChessBoard)controller_.getBoard();
        boolean checked = false;
        for ( row = 1; row <= b.getNumRows(); row++ ) {
            for ( col = 1; col <= b.getNumCols(); col++ ) {
                BoardPosition pos = b.getPosition( row, col );
                assert (pos != null) : "pos at row="+row+" col="+col +" is null";
                if ( pos.isOccupied() && pos.getPiece().isOwnedByPlayer1() == m.isPlayer1() ) {
                    // @@ second arg is not technically correct. it should be last move, but I don't think it matters.
                    checked = b.isKingCheckedByPosition(pos, m);
                }
                if (checked) {
                    JOptionPane.showMessageDialog( this,
                        GameContext.getLabel("KING_IN_CHECK"), GameContext.getLabel("INFORMATION"), JOptionPane.INFORMATION_MESSAGE );
                    return;
                }
            }
        }
    }

    @Override
    public void showLastMove()
    {
        refresh();
    }
}
