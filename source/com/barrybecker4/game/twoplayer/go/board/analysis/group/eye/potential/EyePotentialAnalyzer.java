/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.group.eye.potential;

import com.barrybecker4.common.geometry.Box;
import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzerMap;
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup;
import com.barrybecker4.game.twoplayer.go.board.elements.string.IGoString;

/**
 * Figure out how likely (the potential) that a group can form two eyes.
 *
 * @author Barry Becker
 */
public class EyePotentialAnalyzer {

    /** The group of go stones that we are analyzing. */
    private IGoGroup group;

    private GoBoard board;

    /** bounding box around our group that we are analyzing. */
    private Box boundingBox;

    private GroupAnalyzerMap analyzerMap;

    /**
     * Constructor.
     */
    public EyePotentialAnalyzer(IGoGroup group, GroupAnalyzerMap analyzerMap) {
        this.group = group;
        this.analyzerMap = analyzerMap;
    }

    public void setBoard(GoBoard board) {
        this.board = board;
        boundingBox = group.findBoundingBox();
    }

    /**
     * Expand the bbox by one in all directions.
     * if the bbox is within one space of the edge, extend it all the way to the edge.
     * loop through the rows and columns calculating distances from group stones
     * to the edge and to other stones.
     * if there is a (mostly living) enemy stone in the run, don't count the run.
     * @return eye potential - a measure of how easily this group can make 2 eyes
     *    (0 - 2; 2 meaning it already has 2 guaranteed eyes or can easily get them).
     */
    public float calculateEyePotential() {

        assert board != null : "The board must be set before calculating potential";
        int numRows = board.getNumRows();
        int numCols = board.getNumCols();
        boundingBox = boundingBox.expandGloballyBy(1, numRows, numCols);
        boundingBox = boundingBox.expandBordersToEdge(1, numRows, numCols);

        return findTotalEyePotential();
    }

    /**
     * Make sure that every internal enemy stone is really an enemy and not just dead.
     * compare it with one of the group strings.
     * @return eyePotential - a measure of how easily this group can make 2 eyes (0 - 2; 2 meaning has 2 eyes).
     */
    private float findTotalEyePotential() {

        if (group.getMembers().isEmpty()) return 0;
        IGoString groupString = group.getMembers().iterator().next();

        int rMin = boundingBox.getMinRow();
        int rMax = boundingBox.getMaxRow();
        int cMin = boundingBox.getMinCol();
        int cMax = boundingBox.getMaxCol();

        float totalPotential = 0;
        totalPotential += getTotalRowPotentials(groupString, rMin, rMax, cMin, cMax);
        totalPotential += getTotalColumnPotentials(groupString, rMin, rMax, cMin, cMax);

        return (float)Math.min(1.9, Math.sqrt(totalPotential)/1.3);
    }

    /**
     * @return  total of all the row run potentials.
     */
    private float getTotalRowPotentials(IGoString groupString, int rMin, int rMax, int cMin, int cMax) {

        float totalPotential = 0;
        RunPotentialAnalyzer runAnalyzer = new RunPotentialAnalyzer(groupString, board, analyzerMap);

        for ( int r = rMin; r <= rMax; r++ ) {
            totalPotential += runAnalyzer.getRunPotential(new ByteLocation(r, cMin), 0, 1, rMax, cMax);
        }
        return totalPotential;
    }

    /**
     * @return total of all the column run potentials.
     */
    private float getTotalColumnPotentials(IGoString groupString, int rMin, int rMax, int cMin, int cMax) {

        float totalPotential = 0;
        RunPotentialAnalyzer runAnalyzer = new RunPotentialAnalyzer(groupString, board, analyzerMap);

        for ( int c = cMin; c <= cMax; c++ ) {
            totalPotential += runAnalyzer.getRunPotential(new ByteLocation(rMin, c), 1, 0, rMax, cMax);
        }
        return totalPotential;
    }
}