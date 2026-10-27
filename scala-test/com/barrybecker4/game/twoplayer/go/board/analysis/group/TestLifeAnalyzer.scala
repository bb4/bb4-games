/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.group

import org.junit.Assert
import org.junit.Before
import org.junit.After
import org.junit.Test
import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup

class TestLifeAnalyzer extends GoTestCase {


    private val PREFIX: String = "board/analysis/group/life/"

    // test for unconditional hab
    @Test
    def testUnconditionalLife1(): Unit = {
        verifyUnconditionalLife("unconditionalLife1", true, 12, true)
    }

    @Test
    def testUnconditionalLife2(): Unit = {
        verifyUnconditionalLife("unconditionalLife2", true, 12, false)
    }

    @Test
    def testUnconditionalLife3(): Unit = {
        verifyUnconditionalLife("unconditionalLife3", true, 11, false)
    }


    @Test
    def testUnconditionalLife4(): Unit = {
        verifyUnconditionalLife("unconditionalLife4", true, 8, false)
    }

    @Test
    def testUnconditionalLife5(): Unit = {
        verifyUnconditionalLife("unconditionalLife5", true, 7, false)
    }

    @Test
    def testUnconditionalLife6(): Unit = {
        verifyUnconditionalLife("unconditionalLife6", true, 7, true)
    }

    @Test
    def testUnconditionalLife7(): Unit = {
        verifyUnconditionalLife("unconditionalLife7", false, 8, true)
    }


    @Test
    def testUnconditionalLife8(): Unit = {
        verifyUnconditionalLife("unconditionalLife8", true, 13, true)
    }

    @Test
    def testUnconditionalLife9(): Unit = {
        verifyUnconditionalLife("unconditionalLife9", false, 9, false)
    }

    @Test
    def testUnconditionalLife11(): Unit = {

        verifyUnconditionalLife("unconditionalLife11", true, 14, true)
    }

    @Test
    def testUnconditionalLife12(): Unit = {
        verifyUnconditionalLife("unconditionalLife12", true, 21, true)
    }

     @Test
    def testUnconditionalLife13(): Unit = {
        verifyUnconditionalLife("unconditionalLife13", true, 15, false)
    }

     @Test
    def testUnconditionalLife14(): Unit = {
        verifyUnconditionalLife("unconditionalLife14", true, 13, false)
    }

     @Test
    def testUnconditionalLife15(): Unit = {
        verifyUnconditionalLife("unconditionalLife15", true, 12, true)
    }

    /**
     * Use Benson's algorithm1 for detecting unconditionally alive groups.
     */
    private def verifyUnconditionalLife(file: String, forBlackGroup: Boolean, expectedSizeOfGroup: Int, expectedUnconditionalyAlive: Boolean): Unit = {
        restore(PREFIX + file)

        // find the biggest black and white groups
        var group: IGoGroup = getBiggestGroup(forBlackGroup)

        var size: Int = group.getNumStones
        Assert.assertEquals("Unexpected size of test group.", expectedSizeOfGroup, size)

        var analyzer: LifeAnalyzer = new LifeAnalyzer(group, getBoard, new GroupAnalyzerMap())
        var unconditionallyAlive: Boolean = analyzer.isUnconditionallyAlive

        if (expectedUnconditionalyAlive) {
            Assert.assertTrue("Expected this group be unconditionally alive, but its var group: not. =" + group,
                               unconditionallyAlive)
        } else {
            Assert.assertTrue("Did not expected this group be unconditionally alive, but it var group: is. =" + group,
                               !unconditionallyAlive)
        }
    }


}
