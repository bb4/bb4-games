/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import static org.junit.Assert.assertTrue;


/**
 * Test node in in-memory UCT tree.
 * @author Barry Becker
 */
public class AllStrategiesTest  {

    @Test
    public void runAllTestCases() {
        Result result = JUnitCore.runClasses(AllStrategiesTestRunner.class);
        System.out.println("Num failures = " + result.getFailureCount());
        for (Failure failure : result.getFailures()) {
             System.out.println(failure.toString());
        }
        assertTrue("There were failures.", result.wasSuccessful());
    }
}