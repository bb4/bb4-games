/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.group.eye.potential;

import com.barrybecker4.common.geometry.IntLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzer;
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzerMap;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.barrybecker4.game.twoplayer.go.board.elements.string.IGoString;

/**
 * Figure out the "eye potential" contribution from a horizontal or vertical run within a string.
 *
 * @author Barry Becker
 */
class RunPotentialAnalyzer {

    /** We will analyze the potential of a run within this string. */
    private IGoString groupString;
    private GoBoard board;
    private GroupAnalyzerMap analyzerMap;


    /**
     * Constructor.
     */
    RunPotentialAnalyzer(IGoString groupString, GoBoard board, GroupAnalyzerMap analyzerMap) {
        this.groupString = groupString;
        this.board = board;
        this.analyzerMap = analyzerMap;
    }

    /**
     * Find the potential for one of the bbox's rows or columns.
     * @return eye potential for row and column at pos
     */
    float getRunPotential(Location position, int rowInc, int colInc, int maxRow, int maxCol) {
        IntLocation pos = new IntLocation(position);
        float runPotential = 0;
        int breadth = (rowInc == 1) ? (maxRow - pos.row()) : (maxCol - pos.col());
        GoBoardPosition startSpace = (GoBoardPosition) board.getPosition( pos );

        do {
            GoBoardPosition nextSpace = (GoBoardPosition) board.getPosition( pos );
            GoBoardPosition firstSpace = nextSpace;
            boolean containsEnemy = false;
            int runLength = 0;
            boolean player1 = groupString.isOwnedByPlayer1();
            while (inRun(pos, maxRow, maxCol, nextSpace, player1)) {
                if (containsEnemy(player1, nextSpace)) {
                    containsEnemy = true;
                }
                runLength++;
                pos = pos.incrementOnCopy(rowInc, colInc);
                nextSpace = (GoBoardPosition) board.getPosition( pos );
            }
            boolean bounded = isBounded(startSpace, nextSpace, firstSpace);
            runPotential += accrueRunPotential(rowInc, pos, breadth, firstSpace, containsEnemy, runLength, bounded);

            pos = pos.incrementOnCopy(rowInc, colInc);

        } while (pos.col() <= maxCol && pos.row() <= maxRow);

        return runPotential;
    }

    private boolean containsEnemy(boolean ownedByPlayer1, GoBoardPosition space) {
        GroupAnalyzer groupAnalyzer = analyzerMap.getAnalyzer(groupString.getGroup());
        return space.isOccupied() && space.getPiece().isOwnedByPlayer1() != ownedByPlayer1
                && groupAnalyzer.isTrueEnemy(space);
    }

    private boolean inRun(Location pos, int maxRow, int maxCol, GoBoardPosition space, boolean ownedByPlayer1) {
        return (pos.col() <= maxCol && pos.row() <= maxRow
                && (space.isUnoccupied() ||
                   (space.isOccupied() && space.getPiece().isOwnedByPlayer1() != ownedByPlayer1)));
    }

    private boolean isBounded(GoBoardPosition startSpace, GoBoardPosition space, GoBoardPosition firstSpace) {
        return !(firstSpace.equals(startSpace)) && space!=null && space.isOccupied();
    }

    /**
     * Accumulate the potential for this run.
     * @return  accrued run potential.
     */
    private float accrueRunPotential(int rowInc, Location pos, int breadth,
                                     GoBoardPosition firstSpace, boolean containsEnemy,
                                     int runLength, boolean bounded) {
        float runPotential = 0;

        if (!containsEnemy && runLength < breadth && runLength > 0) {
            int firstPos, max, currentPos;
            if (rowInc == 1) {
                firstPos = firstSpace.getRow();
                max = board.getNumRows();
                currentPos = pos.row();
            } else {
                firstPos = firstSpace.getCol();
                max = board.getNumCols();
                currentPos = pos.col();
            }

            runPotential = new Run(firstPos, currentPos, max, bounded).getPotential();
        }

        return runPotential;
    }
}
