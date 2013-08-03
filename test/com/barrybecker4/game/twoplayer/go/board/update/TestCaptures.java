/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.update;

import com.barrybecker4.game.twoplayer.go.GoTestCase;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone;

/**
 * @author Barry Becker
 */
public class TestCaptures extends GoTestCase {

    /** instance under test */
    private CaptureCounts captures;

    /**
     * common initialization
     */
    @Override
    protected void setUp() throws Exception {
        captures = new CaptureCounts();
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testNoCaptures() {

        assertEquals("Unexpected number of black captures" , 0, captures.getNumCaptures(true));
        assertEquals("Unexpected number of white captures" , 0, captures.getNumCaptures(false));
    }

    public void testIncBlackCaptures() {

        verifyIncCaptures(true);
    }

    public void testIncWhiteCaptures() {

        verifyIncCaptures(false);
    }

    public void testDecrBlackCaptures() {

        verifyDecrCaptures(true);
    }

    public void testDecrWhiteCaptures() {

        verifyDecrCaptures(false);
    }

    public void verifyIncCaptures(boolean isPlayer1) {

        GoMoveStub move = new GoMoveStub(new GoStone(!isPlayer1));
        move.setNumCaptures(2);

        captures.updateCaptures(move, true);
        String color = isPlayer1 ? "balck":"white";
        assertEquals("Unexpected number of "+color+" captures" , 2, captures.getNumCaptures(isPlayer1));

        captures.updateCaptures(move, true);
        assertEquals("Unexpected number of  "+color+" captures" , 4, captures.getNumCaptures(isPlayer1));
    }

    public void verifyDecrCaptures(boolean isPlayer1) {

        // first add some captures so they can be decremented
        GoMoveStub move = new GoMoveStub(new GoStone(!isPlayer1));
        move.setNumCaptures(3);
        captures.updateCaptures(move, true);

        move.setNumCaptures(1);

        captures.updateCaptures(move, false);
        String color = isPlayer1 ? "balck":"white";
        assertEquals("Unexpected number of "+color+" captures" , 2, captures.getNumCaptures(isPlayer1));

        captures.updateCaptures(move, false);
        assertEquals("Unexpected number of "+color+" captures" , 1, captures.getNumCaptures(isPlayer1));
    }
}