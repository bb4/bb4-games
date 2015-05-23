package com.barrybecker4.game.twoplayer.common.search.strategy;

import com.barrybecker4.common.util.FileUtil;
import com.barrybecker4.game.twoplayer.common.search.TwoPlayerMoveStub;
import com.barrybecker4.game.twoplayer.common.search.examples.GameTreeExample;
import com.barrybecker4.game.twoplayer.common.search.strategy.testcase.SearchResult;
import com.barrybecker4.game.twoplayer.common.search.strategy.testcase.SearchTestCase;
import com.barrybecker4.game.twoplayer.common.search.strategy.testcase.SearchTestExample;
import com.barrybecker4.game.twoplayer.common.search.transposition.TranspositionTable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.barrybecker4.game.twoplayer.common.search.strategy.EvaluationPerspective.CURRENT_PLAYER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    /** the test case file */
    private File file;

    @Before
    public void initialize() {
      //primeNumberChecker = new PrimeNumberChecker();
    }

    public AllStrategiesTestRunner(File file) {
        this.file = file;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testCases() {

        List<File> testCaseFiles = FileUtil.getFilesInDirectory(TEST_CASE_DIR);

        Collection<Object[]> testCases = new ArrayList<>();
        for (File file : testCaseFiles) {
            Object[] fileArg = new Object[] { file };
            testCases.add(fileArg);
        }

        return testCases;
    }

    /** This test will be run once for each xml test case file in TEST_CASE_DIR */
    @Test
    public void testCaseRunner() throws Exception {
        SearchTestExample test = new SearchTestExample(file);

        System.out.println("Filename is : " + file);

        for (SearchTestCase testCase : test.getTestCases()) {
            System.out.println("testCase = "+ testCase.toString());

            SearchStrategy<TwoPlayerMoveStub> searchStrategy = testCase.createSearchStrategy();

            SearchResult actualResult =
                new SearchResult(test.getInitialMove(), searchStrategy);

            assertEquals("Wrong result found for " +test.getName() + " " + testCase.toString(),
                    testCase.getExpectedResult(), actualResult);

            /* maybe check the transposition table too
            if (searchStrategy instanceof MemorySearchStrategy) {
                TranspositionTable table = ((MemorySearchStrategy) searchStrategy ).getTranspositionTable();
                System.out.println("table=" + table);
            }   */
        }
    }

}
