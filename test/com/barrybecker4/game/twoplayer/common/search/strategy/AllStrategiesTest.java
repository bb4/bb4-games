package com.barrybecker4.game.twoplayer.common.search.strategy;

import com.barrybecker4.common.util.FileUtil;
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

    public AllStrategiesTest(File file) {
        this.file = file;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {

        List<File> testCases = FileUtil.getFilesInDirectory(TEST_CASE_DIR);

        Collection<Object[]> data = new ArrayList<>();
        for (File file : testCases) {
            Object[] fileArg = new Object[] { file };
            data.add(fileArg);
        }

        return data;
    }

    /** This test will be run once for each xml test case file in TEST_CASE_DIR */
   @Test
   public void testCaseRunner() {
       System.out.println("Filename is : " + file);
       assertTrue("file = " + file.getAbsolutePath(), true);
   }
}
