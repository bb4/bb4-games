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
 *
 * Need to add notation for Mtd . Something like strategy="MtdStrategy(NegaScoutMemoryStrategy)"
 *
 *
        <test-case strategy="MtdNegaMaxStrategy" notes="Winning next move for depth 3">
              <brute-force-options look-ahead="3" alpha-beta="false" quiescence="false" max-quiescent-depth="3"/>
              <expected-search-result move-id="0" inherited-value="4096" num-moves-considered="2"/>
        </test-case>
        <test-case strategy="MtdNegaScoutStrategy" notes="Winning next move for depth 3">
              <brute-force-options look-ahead="3" alpha-beta="false" quiescence="false" max-quiescent-depth="3"/>
              <expected-search-result move-id="0" inherited-value="4096" num-moves-considered="2"/>
        </test-case>


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