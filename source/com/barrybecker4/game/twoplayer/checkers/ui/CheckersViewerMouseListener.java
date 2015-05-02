/** Copyright by Barry G. Becker, 2000-2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.checkers.ui;

import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer;
import com.barrybecker4.game.twoplayer.checkers.CheckersBoard;
import com.barrybecker4.game.twoplayer.checkers.CheckersMove;
import com.barrybecker4.game.twoplayer.checkers.CheckersSearchable;
import com.barrybecker4.game.twoplayer.checkers.MoveGenerator;
import com.barrybecker4.game.twoplayer.common.TwoPlayerController;

import java.util.List;

/**
 *  Mouse handling for checkers game.
 *
 *  @author Barry Becker
 */
public class CheckersViewerMouseListener extends CheckersLikeViewerMouseListener<CheckersMove, CheckersBoard> {

    /**
     * Constructor.
     */
    public CheckersViewerMouseListener(GameBoardViewer<CheckersMove, CheckersBoard> viewer) {
        super(viewer);
    }

    /**
     * @param position place to consider possible moves from.
     * @return a list of all possible moves from the given position.
     */
    protected List<CheckersMove> getPossibleMoveList(BoardPosition position) {

        TwoPlayerController<CheckersMove, CheckersBoard> controller =
                (TwoPlayerController<CheckersMove, CheckersBoard>) viewer_.getController();

        CheckersMove lastMove = controller.getLastMove();
        MoveGenerator generator =
                new MoveGenerator((CheckersSearchable) controller.getSearchable(),
                        controller.getComputerWeights().getDefaultWeights());

        MoveList<CheckersMove> possibleMoveList = new MoveList<>();
        generator.addMoves(position, lastMove, possibleMoveList);
        return possibleMoveList;
    }

    protected boolean customCheckFails(BoardPosition position, BoardPosition destp) {
        return  ( (position == null) || (destp == null) || (destp.isOccupied()) );
    }
}