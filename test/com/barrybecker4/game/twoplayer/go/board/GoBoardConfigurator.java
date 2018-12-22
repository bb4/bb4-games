// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.go.board;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPositionList;

/**
 * Helps configure go boards for unit testing.
 * @author Barry Becker
 */
public class GoBoardConfigurator {

    private GoBoard board;

    public GoBoardConfigurator(int size) {
        board = new GoBoard(size, 0);
    }

    public GoBoardConfigurator(int size, int numHandicap) {
        board = new GoBoard(size, numHandicap);
    }

    public GoBoard getBoard() {
        return board;
    }

    /** Used in unit tests to initialize a board when we do not want to bother loading a file */
    public void setPositions(GoBoardPosition... positions) {
        for (GoBoardPosition position : positions) {
            board.setPosition(position);
        }
    }
    /** Used in unit tests to initialize a board when we do not want to bother loading a file */
    public void setPositions(GoBoardPositionList list) {
        for (GoBoardPosition position : list) {
            board.setPosition(position);
        }
    }

    /**
     * @param positions location of positions to create.
     * @return a list of go board positions with no stones in them.
     */
    public static GoBoardPositionList createPositionList(Location[] positions) {

        GoBoardPositionList spaces = new GoBoardPositionList();
        for (Location pos : positions) {
            spaces.add(new GoBoardPosition(pos.row(), pos.col(), null, null));
        }
        return spaces;
    }
}
