/* Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.analysis.group

import org.junit.Assert
import org.junit.Before
import org.junit.After
import org.junit.Test
import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup

class TestAbsoluteHealthCalculator extends GoTestCase {


    private val PREFIX: String = "board/analysis/grouphealth/"

    private var absHealthCalculator: AbsoluteHealthCalculator = scala.compiletime.uninitialized

    @Before
    override def setUp(): Unit = {
        super.setUp()
        var group: IGoGroup = new StubGoGroup(0.0f, true, 10)
                var analyzerMap: GroupAnalyzerMap = new GroupAnalyzerMap()

                absHealthCalculator = new AbsoluteHealthCalculator(group, analyzerMap)
    }

    @Test
    def testDefaultEyePotential(): Unit = {
        Assert.assertEquals("Unexpected eye potential",
                0.0f, absHealthCalculator.getEyePotential, 0.0f)
    }

    @Test
    def testIsValid(): Unit = {
            Assert.assertFalse("Unexpected isValidValue", absHealthCalculator.isValid)
    }


}
