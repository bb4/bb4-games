/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy;

import com.barrybecker4.common.format.FormatUtil;
import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.TwoPlayerMoveStub;
import junit.framework.TestCase;
import static com.barrybecker4.game.twoplayer.common.search.options.MonteCarloSearchOptions.MaximizationStyle.WIN_RATE;

/**
 * Test node in in-memory UCT tree.
 * @author Barry Becker
 */
public class UctNodeTest extends TestCase  {

    /** instance under test */
    UctNode<TwoPlayerMove> uctNode;

    /** a Player2 move to put in the node */
    private static final TwoPlayerMove P1_MOVE =
            new TwoPlayerMoveStub(10, true, new ByteLocation(1,1), null);

    /** a Player2 move to put in the node */
    private static final TwoPlayerMove P2_MOVE =
            new TwoPlayerMoveStub(10, false, new ByteLocation(1,1), null);

    private static final double TOL = 0.0001;


    public void testConstructionOfNodeWithNoChildren() {

        uctNode = new UctNode<>(P2_MOVE);

        assertEquals("Unexpected move", P2_MOVE, uctNode.move);
        assertEquals("Unexpected bestNode", null, uctNode.findBestChildMove(WIN_RATE));
        assertFalse("Unexpected children", uctNode.hasChildren());
        assertEquals("Unexpected numVisits", 0, uctNode.getNumVisits());
        // The winrate is 0.5 (tie) + 10/WINNING = 0.50122
        assertEquals("Unexpected winRate", 0.5012207f, uctNode.getWinRate());
        assertEquals("Unexpected attrs", "{winRate=0.50122, visits=0, wins=0.0}", uctNode.getAttributes().toString());
        assertEquals("Unexpected uctValue",
                1000.50122, uctNode.calculateUctValue(1.0, 1), TOL);
    }

    public void testUpdateWinP1() {

        uctNode = new UctNode<>(P1_MOVE);

        uctNode.update(1.0);
        assertEquals("Unexpected numWins", 1.0f, uctNode.getWinRate());

        uctNode.update(0.0);
        assertEquals("Unexpected numWins", 0.5f, uctNode.getWinRate());
    }


    public void testUpdateWinP2() {

        uctNode = new UctNode<>(P2_MOVE);

        uctNode.update(1.0);
        assertEquals("Unexpected numWins", 0.0f, uctNode.getWinRate());

        uctNode.update(0.0);
        assertEquals("Unexpected numWins", 0.5f, uctNode.getWinRate());
    }

    public void testSetBestNodeWithAllZeroWinrate() {

        uctNode = new UctNode<>(P2_MOVE);

        MoveList<TwoPlayerMove> moves = new MoveList<>();
        TwoPlayerMove firstMove = new TwoPlayerMoveStub(5, false, new ByteLocation(1,1), null);
        TwoPlayerMove goodMove = new TwoPlayerMoveStub(10, false, new ByteLocation(1,2), null);
        moves.add(firstMove);
        moves.add(goodMove);
        moves.add(new TwoPlayerMoveStub(9, false, new ByteLocation(1,3), null));

        uctNode.addChildren(moves);

        // good move is selected first because it has a better winrate using static evaluation.
        assertEquals("Unexpected bestMove", goodMove, uctNode.findBestChildMove(WIN_RATE));
    }

    public void testSetBestNode() {

        uctNode = new UctNode<>(P2_MOVE);

        MoveList<TwoPlayerMove> moves = new MoveList<>();
        TwoPlayerMove firstMove = new TwoPlayerMoveStub(5, false, new ByteLocation(1,1), null);
        TwoPlayerMove goodMove = new TwoPlayerMoveStub(10, false, new ByteLocation(1,2), null);
        moves.add(firstMove);
        moves.add(goodMove);
        moves.add(new TwoPlayerMoveStub(9, false, new ByteLocation(1,3), null));

        uctNode.addChildren(moves);
        UctNode secondNode = uctNode.getChildren().get(1);
        secondNode.update(0.0);
        secondNode.update(1.0);
        secondNode.update(0.0);

        // secondMove is selected since it has a winrate of 0.6667
        assertEquals("Unexpected bestMove", secondNode.move, uctNode.findBestChildMove(WIN_RATE));
    }

    /** It should be a large value in this case so it gets selected to be visited first. */
    public void testCalcUctValueWhenNoVisits() {
        uctNode = new UctNode<>(P2_MOVE);

        assertTrue("Unexpected uctValue",
                uctNode.calculateUctValue(1.0, 1) > 100);
    }

    public void testCalcUctValueWithOneVisitNoWinsOneParentVisitP1() {
        uctNode = new UctNode<>(P1_MOVE);
        uctNode.update(0.0);

        assertEquals("Unexpected uctValue",
                0.0, uctNode.calculateUctValue(1.0, 1) );
    }

    public void testCalcUctValueWithOneVisitNoWinsOneParentVisitP2() {
        uctNode = new UctNode<>(P2_MOVE);
        uctNode.update(1.0);

        assertEquals("Unexpected uctValue",
                0.0, uctNode.calculateUctValue(1.0, 1) );
    }

    public void testCalcUctValueWithOneVisitNoWinsTwoParentVisits() {
        uctNode = new UctNode<>(P2_MOVE);
        uctNode.update(1.0);

        assertEquals("Unexpected uctValue",
                0.3723297411, uctNode.calculateUctValue(1.0, 2), TOL);
    }

    public void testCalcUctValueWithOneVisitNoWins32ParentVisitsP1Won() {
        uctNode = new UctNode<>(P2_MOVE);
        uctNode.update(1.0);

        assertEquals("Unexpected uctValue",
                0.83255461, uctNode.calculateUctValue(1.0, 32), TOL);
    }

    public void testCalcUctValueWithOneVisitNoWins32ParentVisitsP2Won() {
        uctNode = new UctNode<>(P2_MOVE);
        uctNode.update(0.0);

        assertEquals("Unexpected uctValue",
                1.83255461, uctNode.calculateUctValue(1.0, 32), TOL);
    }

    public void testCalcUctValueWith10Visits1Win32ParentVisitsP1WonAll() {
        uctNode = new UctNode<>(P2_MOVE);
        for (int i=0; i<10; i++) {
            uctNode.update(1.0);
        }

        verifyUctValues(0.026328, 0.26328, 2.6328);
    }

    public void testCalcUctValueWith10Visits1Win32ParentVisitsP2WonAll() {
        uctNode = new UctNode<>(P2_MOVE);
        for (int i=0; i<10; i++) {
            uctNode.update(0.0);
        }

        verifyUctValues(1.026328, 1.26328, 3.6328);
    }

    /**
     * Check a mix of calculated UCT values.
     */
    private void verifyUctValues(double expMoreExploitValue, double expBalancedValue, double expMoreExploreValue)
    {
        assertEquals("Unexpected uctValue (more exploit)",
                expMoreExploitValue, uctNode.calculateUctValue(0.1, 32), TOL);
        assertEquals("Unexpected uctValue (balanced)",
                expBalancedValue, uctNode.calculateUctValue(1.0, 32), TOL);
        assertEquals("Unexpected uctValue (more explore)",
                expMoreExploreValue, uctNode.calculateUctValue(10.0, 32), TOL);
    }


    /**
     * Used to produce data for visualizing the effect of parameters on the UCT value.
     *
    public void CalcUctTable() {
        //int parentVisits = 1024; // 2^25 = 33,554,432
        System.out.println("parentVisits, numVisits, winRate, eeRatio, uctValue");
        calcUctTable(2);
        calcUctTable(32);
        calcUctTable(1024);
        System.out.println("done");
    } */

    private void calcUctTable(int parentVisits) {
        for (int numVisits = 5; numVisits <= 25; numVisits += 5) {
            uctNode = new UctNode<>(P2_MOVE);
            uctNode.update(1.0);
            for (double winRate = 0; winRate <=1.0; winRate+=0.20) {

                // get the winrate to where it is supposed to be
                while (Math.round(uctNode.getWinRate()*100.0) < Math.round(100.0*winRate)) {
                    uctNode.update(0.0);
                }
                for (double eeRatio = 0; eeRatio<=2.0; eeRatio += 0.5) {
                    double v = uctNode.calculateUctValue(eeRatio, parentVisits);
                    System.out.println("t"+parentVisits + "\tt"
                            + numVisits + "\tt"+uctNode.getWinRate()
                            +"\tt" + eeRatio + "\t"  + FormatUtil.formatNumber(v));
                }
            }
        }
    }
}