/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.group;

import com.barrybecker4.game.twoplayer.go.GoTestCase;
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup;

/**
 * Mostly test that the scoring of groups works correctly.
 * @author Barry Becker
 */
public class TestLifeAnalyzer extends GoTestCase {

    private static final String PREFIX = "board/analysis/group/life/";

    // test for unconditional hab
    public void testUnconditionalLife1() throws Exception {
        verifyUnconditionalLife("unconditionalLife1", true, 12, true);
    }

    public void testUnconditionalLife2() throws Exception {
        verifyUnconditionalLife("unconditionalLife2", true, 13, false);
    }

    public void testUnconditionalLife3() throws Exception {
        verifyUnconditionalLife("unconditionalLife3", true, 11, false);
    }


    public void testUnconditionalLife4() throws Exception {
        verifyUnconditionalLife("unconditionalLife4", true, 8, false);
    }

    public void testUnconditionalLife5() throws Exception {
        verifyUnconditionalLife("unconditionalLife5", true, 7, false);
    }

    public void testUnconditionalLife6() throws Exception {
        verifyUnconditionalLife("unconditionalLife6", true, 7, true);
    }

    public void testUnconditionalLife7() throws Exception {
        verifyUnconditionalLife("unconditionalLife7", false, 8, true);
    }


    public void testUnconditionalLife8() throws Exception{
        verifyUnconditionalLife("unconditionalLife8", true, 13, true);
    }

    public void testUnconditionalLife9() throws Exception {
        verifyUnconditionalLife("unconditionalLife9", false, 9, false);
    }

    public void testUnconditionalLife11() throws Exception {

        verifyUnconditionalLife("unconditionalLife11", true, 14, true);
    }

    public void testUnconditionalLife12() throws Exception {
        verifyUnconditionalLife("unconditionalLife12", true, 21, true);
    }

     public void testUnconditionalLife13() throws Exception{
        verifyUnconditionalLife("unconditionalLife13", true, 15, false);
    }

     public void testUnconditionalLife14() throws Exception {
        verifyUnconditionalLife("unconditionalLife14", true, 13, false);
    }

     public void testUnconditionalLife15() throws Exception {
        verifyUnconditionalLife("unconditionalLife15", true, 12, true);
    }

    /**
     * Use Benson's algorithm1 for detecting unconditionally alive groups.
     */
    private void verifyUnconditionalLife(String file,
                                         boolean forBlackGroup, int expectedSizeOfGroup,
                                         boolean expectedUnconditionalyAlive) throws Exception {
        restore(PREFIX + file);

        // find the biggest black and white groups
        IGoGroup group = getBiggestGroup(forBlackGroup);

        int size = group.getNumStones();
        assertEquals("Unexpected size of test group.", expectedSizeOfGroup, size);

        LifeAnalyzer analyzer = new LifeAnalyzer(group, getBoard(), new GroupAnalyzerMap());
        boolean unconditionallyAlive = analyzer.isUnconditionallyAlive();

        if (expectedUnconditionalyAlive) {
            assertTrue("Expected this group be unconditionally alive, but its not. group=" + group,
                               unconditionallyAlive);
        } else {
            assertTrue("Did not expected this group be unconditionally alive, but it is. group=" + group,
                               !unconditionallyAlive);
        }
    }

}
