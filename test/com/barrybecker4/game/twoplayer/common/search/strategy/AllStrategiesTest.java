/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import static org.junit.Assert.assertTrue;


/**
 * Test node in in-memory UCT tree.
 *
 * Special notation for Mtd strategies because they take a memory strategy as a parameter:
 * "MtdStrategy:NegaScoutMemoryStrategy"
 *
 * TODO:
 *  - add default expected values in the xml
 *  - remove the old inheritance based examples and cases
 *
 *  What is working and what is not:
 *                  p1    p2   a/b   quiescense
 *                 ----  ----  ----  ------
 *  MiniMax        yes   yes
 *  NegaMax        yes   yes
 *  NegaScout
 *  NegaMaxMem
 *  NegaScoutMem
 *  UCT
 *  MtdNegaMax
 *  MtdNegaScout
 *
 * @author Barry Becker
 */
public class AllStrategiesTest  {

    @Test
    public void runAllTestCases() {
        Result result = JUnitCore.runClasses(AllStrategiesTestRunner.class);
        if (!result.wasSuccessful()) {
            System.out.println(" *** FAILURES (" + result.getFailureCount() + ") ***\n");
            for (Failure failure : result.getFailures()) {
                System.out.println("msg: " + failure.getException().getMessage());
                //System.out.println(failure.toString());
            }
        }
        assertTrue("There were failures.", result.wasSuccessful());
    }
}