package com.barrybecker4.game.twoplayer.common.search.strategy

import com.barrybecker4.common.util.FileUtil
import com.barrybecker4.game.twoplayer.common.search.TwoPlayerMoveStub
import com.barrybecker4.game.twoplayer.common.search.strategy.testcase.{SearchResult, SearchTestCase, SearchTestExample}
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import java.util

import scala.annotation.static
import scala.jdk.CollectionConverters.*

/**
 * Read the case xml files and run the tests described by them.
 * Uses this approach:
 * http://www.tutorialspoint.com/junit/junit_parameterized_test.htm
 * http://stackoverflow.com/questions/358802/junit-test-with-dynamic-number-of-tests
 *
 * @author Barry Becker
 */
@RunWith(classOf[Parameterized])
class AllStrategiesTestRunner(
    exampleName: String,
    testCase: SearchTestCase,
    initialMove: TwoPlayerMoveStub
):

  /** This test will be run once for each xml test case file in TEST_CASE_DIR */
  @Test
  def testCaseRunner(): Unit =
    System.out.println(exampleName + " : " + testCase.toString)
    val searchStrategy = testCase.createSearchStrategy()
    val actualResult = SearchResult(initialMove, searchStrategy)
    assertEquals(
      "\n" + exampleName + "\nWrong result found for " +
        testCase.getName + "\n " + testCase.toString + "\n",
      testCase.getExpectedResult,
      actualResult
    )

object AllStrategiesTestRunner:

  /** location of xml test cases for all search strategies */
  private val TEST_CASE_DIR = "test/com/barrybecker4/game/twoplayer/common/search/cases"

  private val TEST_CASE_SUFFIX = ".xml"

  @Parameterized.Parameters
  @static
  def testCases(): util.Collection[Array[AnyRef]] =
    val testCaseFiles = FileUtil.getFilesInDirectory(TEST_CASE_DIR)
    val cases = new util.ArrayList[Array[AnyRef]]()
    for file <- testCaseFiles do
      if file.getPath.endsWith(TEST_CASE_SUFFIX) then
        val test = new SearchTestExample(file)
        for tcase <- test.getTestCases.asScala do
          cases.add(Array[AnyRef](test.getName, tcase, test.getTree(tcase.getRootPlayer1)))
    cases
