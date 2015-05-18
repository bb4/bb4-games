package com.barrybecker4.game.twoplayer.common.search.strategy;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class AllStrategiesTestRunner {

   public static void main(String[] args) {
      Result result = JUnitCore.runClasses(AllStrategiesTest.class);
      for (Failure failure : result.getFailures()) {
         System.out.println(failure.toString());
      }
      System.out.println(result.wasSuccessful());
   }
}