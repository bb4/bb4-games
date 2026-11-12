package com.barrybecker4.game.twoplayer.common.search.strategy

import com.barrybecker4.common.util.FileUtil
import com.barrybecker4.game.twoplayer.common.search.TwoPlayerMoveStub
import com.barrybecker4.game.twoplayer.common.search.strategy.testcase.{SearchResult, SearchTestCase, SearchTestExample}
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

import java.util
import java.util.stream.Stream

import scala.annotation.static
import scala.jdk.CollectionConverters.*
import org.junit.jupiter.params.provider.Arguments

/**
 * Read the case xml files and run the tests described by them (JUnit Jupiter parameterized).
 */
class AllStrategiesTestRunner:

  /** Run once for each xml test case file in TEST_CASE_DIR */
  @ParameterizedTest(name = "{0} : {1}")
  @MethodSource(Array("testCases"))
  def testCaseRunner(
      exampleName: String,
      testCase: SearchTestCase,
      initialMove: TwoPlayerMoveStub
  ): Unit =
    System.out.println(exampleName + " : " + testCase.toString)
    val searchStrategy = testCase.createSearchStrategy()
    val actualResult = SearchResult(initialMove, searchStrategy)
    assertEquals(
      testCase.getExpectedResult,
      actualResult,
      "\n" + exampleName + "\nWrong result found for " +
        testCase.getName + "\n " + testCase.toString + "\n",
    )

object AllStrategiesTestRunner:

  /** location of xml test cases for all search strategies */
  private val TEST_CASE_DIR = "test/com/barrybecker4/game/twoplayer/common/search/cases"

  private val TEST_CASE_SUFFIX = ".xml"

  @static
  def testCases(): Stream[Arguments] =
    val testCaseFiles = FileUtil.getFilesInDirectory(TEST_CASE_DIR)
    val cases = new util.ArrayList[Arguments]()
    for file <- testCaseFiles do
      if file.getPath.endsWith(TEST_CASE_SUFFIX) then
        val test = new SearchTestExample(file)
        for tcase <- test.getTestCases.asScala do
          cases.add(Arguments.of(test.getName, tcase, test.getTree(tcase.getRootPlayer1)))
    cases.stream()
