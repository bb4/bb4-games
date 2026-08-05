/* Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy

import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.JUnitCore
import org.junit.runner.notification.Failure

import scala.jdk.CollectionConverters.*

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
class AllStrategiesTest:

  @Test
  def runAllTestCases(): Unit =
    val result = JUnitCore.runClasses(classOf[AllStrategiesTestRunner])
    if !result.wasSuccessful() then
      System.out.println(" *** FAILURES (" + result.getFailureCount + ") ***\n")
      for failure <- result.getFailures.asScala do
        System.out.println("msg: " + failure.getException.getMessage)
    assertTrue("There were failures.", result.wasSuccessful())
