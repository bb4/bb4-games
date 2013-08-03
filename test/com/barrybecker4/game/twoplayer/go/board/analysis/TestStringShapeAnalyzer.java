/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis;

import com.barrybecker4.game.twoplayer.go.GoTestCase;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;


/**
 * @author Barry Becker
 */
public class TestStringShapeAnalyzer extends GoTestCase {


    public void testShape1() throws Exception {
        restore("shape/problem_shape1");

        checkShape(4, 4, 0);
        checkShape(9, 4, 0);

        checkShape(10, 8, 1);
        checkShape(11, 8, 1);
        checkShape(11, 9, 1);
    }

    public void testShape2() throws Exception {
            restore("shape/problem_shape1");

            checkShape(4, 9, 3);
            checkShape(5, 9, 6);
            checkShape(5, 10, 6);
            checkShape(6, 10, 3);
        }

    private void checkShape(int r, int c, int expectedShapeScore) {
        GoBoard board = getBoard();
        GoBoardPosition position = (GoBoardPosition)board.getPosition(r, c);
        StringShapeAnalyzer sa = new StringShapeAnalyzer(board);
        int n = sa.formsBadShape(position);
        assertTrue("Expected "+expectedShapeScore+" but got "+n+" for "+position, n == expectedShapeScore);
    }
}