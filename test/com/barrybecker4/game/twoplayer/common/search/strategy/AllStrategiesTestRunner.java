package com.barrybecker4.game.twoplayer.common.search.strategy;

import com.barrybecker4.common.util.FileUtil;
import com.barrybecker4.game.twoplayer.common.search.TwoPlayerMoveStub;
import com.barrybecker4.game.twoplayer.common.search.strategy.testcase.SearchResult;
import com.barrybecker4.game.twoplayer.common.search.strategy.testcase.SearchTestCase;
import com.barrybecker4.game.twoplayer.common.search.strategy.testcase.SearchTestExample;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * Read the case xml files and run the tests described by them.
 * Uses this approach:
 * http://www.tutorialspoint.com/junit/junit_parameterized_test.htm
 * http://stackoverflow.com/questions/358802/junit-test-with-dynamic-number-of-tests
 *
 * @author Barry Becker
 */
@RunWith(Parameterized.class)
public class AllStrategiesTestRunner {

    /** location of xml test cases for all search strategies */
    private static final String TEST_CASE_DIR = "test/com/barrybecker4/game/twoplayer/common/search/cases";

    private static final String TEST_CASE_SUFFIX = ".xml";

    private String exampleName;
    private SearchTestCase testCase;
    private TwoPlayerMoveStub initialMove;


    @Before
    public void initialize() { }

    public AllStrategiesTestRunner(
            String exampleName, SearchTestCase testCase, TwoPlayerMoveStub initialMove) {
        this.exampleName = exampleName;
        this.testCase = testCase;
        this.initialMove = initialMove;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testCases() {

        File[] testCaseFiles = FileUtil.getFilesInDirectory(TEST_CASE_DIR);

        Collection<Object[]> testCases = new ArrayList<>();
        for (File file : testCaseFiles) {
            if (file.getPath().endsWith(TEST_CASE_SUFFIX )) {
                SearchTestExample test = new SearchTestExample(file);
                for (SearchTestCase tcase : test.getTestCases()) {
                     Object[] tcaseArg = new Object[] {test.getName(),  tcase, test.getTree(tcase.getRootPlayer1()) };
                     testCases.add(tcaseArg);
                }
            }
        }

        return testCases;
    }

    /** This test will be run once for each xml test case file in TEST_CASE_DIR */
    @Test
    public void testCaseRunner() throws Exception {

        System.out.println(exampleName + " : " + testCase.toString());

        SearchStrategy<TwoPlayerMoveStub> searchStrategy = testCase.createSearchStrategy();

        SearchResult actualResult =
            new SearchResult(initialMove, searchStrategy);

        assertEquals("\n" + exampleName + "\nWrong result found for "
                        + testCase.getName() + "\n "
                        + testCase.toString() + "\n",
                testCase.getExpectedResult(), actualResult);

        /* maybe check the transposition table too
        if (searchStrategy instanceof MemorySearchStrategy) {
            TranspositionTable table = ((MemorySearchStrategy) searchStrategy ).getTranspositionTable();
            System.out.println("table=" + table);
        }   */
    }

}
