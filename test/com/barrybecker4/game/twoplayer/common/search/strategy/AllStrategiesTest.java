package com.barrybecker4.game.twoplayer.common.search.strategy;

import com.barrybecker4.common.util.FileUtil;
import com.barrybecker4.game.twoplayer.common.search.strategy.testcase.SearchTestExample;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
public class AllStrategiesTest {

    /** location of xml test cases for all search strategies */
    private static final String TEST_CASE_DIR = "test/com/barrybecker4/game/twoplayer/common/search/cases";

    /** the test case file */
    private File file;

    @Before
    public void initialize() {
      //primeNumberChecker = new PrimeNumberChecker();
    }

    public AllStrategiesTest(File file) {
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
    public void testCaseRunner() {
        SearchTestExample test = new SearchTestExample(file);


        System.out.println("Filename is : " + file);
        assertTrue("file = " + file.getAbsolutePath(), false);
    }
}
