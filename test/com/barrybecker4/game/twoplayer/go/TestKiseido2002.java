/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.twoplayer.go.board.move.GoMove;
import com.barrybecker4.ui.file.GenericFileFilter;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * @author Barry Becker
 */
public class TestKiseido2002 extends GoTestCase {

    /*
    // just do january for now. Its a lot as it is.
    public void testJanuary() {
        check("2002-01");
    }  */


    // just do february for now. It has a lot as it is.
    public void testFebruary() throws Exception {
        check("2002-02", 10);
    }

    /*
    public void testMarch() {
        check("2002-03");
    }

    public void testApril() {
        check("2002-04");
    }

    public void testMay() {
        check("2002-05");
    }

    public void testJune() {
        check("2002-06");
    }

    public void testJuly() {
        check("2002-07");
    }

    public void testAugust() {
        check("2002-08");
    }

    public void testSeptember() {
        check("2002-09");
    }

    public void testOctober() {
        check("2002-10");
    }

    public void testNovember() {
        check("2002-11");
    }

    public void testDecember() {
        check("2002-12");
    }
    */

    /**
     * Verify that we can load all the files with the specified pattern
     * @param problemPattern verify loading of all files that match this pattern.
     */
    private void check(String problemPattern) throws Exception {
       check(problemPattern, Integer.MAX_VALUE);
    }

    /**
     * Verify that we can load all the files with the specified pattern
     * @param problemPattern verify loading of all files that match this pattern.
     * @param limit don't load more files that this (since there may be a lot).
     */
    private void check(String problemPattern, int limit) throws Exception {

        GameContext.log(0, "Now checking "+ problemPattern);
        List<String> files = getFilesMatching("games2002/", problemPattern);

        int ct = 0;
        for (String file : files) {
            if (ct++ >= limit) break;
            String filename = file.substring(0, file.length() - 4);
            GameContext.log(0, " about to restore :" + filename);
            try {
                restore("games2002/" + filename);
            } catch (AssertionError e) {
                System.out.println("error on " + filename);
                e.printStackTrace();
            }
        }

        // must check the worth of the board once to update the scoreContributions fo empty spaces.
        controller.getMoveList();
        //double w = controller.worth((GoMove)moves.get(moves.size()-3), controller.getDefaultWeights(), true);
        controller.getSearchable().done(GoMove.createResignationMove(true), true);
        //controller.updateLifeAndDeath();   // this updates the groups and territory as well.

        assertTrue(true);
    }

    /**
     * @return all the files matching the supplied pattern in the specified directory
     */
    protected List<String> getFilesMatching(String directory, String pattern) throws IOException, URISyntaxException {

        return getResourcesForPattern(GoTestCase.EXTERNAL_TEST_CASE_DIR + directory, pattern);
    }

    private List<String> getResourcesForPattern(String path, String pattern) throws IOException, URISyntaxException {

        URL dirURL = getClass().getResource(path);

        if (dirURL != null && dirURL.getProtocol().equals("file")) {
            String directory = new File(dirURL.toURI()).getAbsolutePath();
            return Arrays.asList(GenericFileFilter.getFilesMatching(directory, pattern));
        }

        return null;
    }
}