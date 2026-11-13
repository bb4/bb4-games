/* Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.analysis.group

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup

class TestAbsoluteHealthCalculator extends GoTestCase {


    private val PREFIX: String = "board/analysis/grouphealth/"

    private var absHealthCalculator: AbsoluteHealthCalculator = scala.compiletime.uninitialized

    @BeforeEach
    override def setUp(): Unit = {
        super.setUp()
        var group: IGoGroup = new StubGoGroup(0.0f, true, 10)
                var analyzerMap: GroupAnalyzerMap = new GroupAnalyzerMap()

                absHealthCalculator = new AbsoluteHealthCalculator(group, analyzerMap)
    }

    @Test
    def testDefaultEyePotential(): Unit = {
        Assertions.assertEquals(0.0f, absHealthCalculator.getEyePotential, 0.0f, "Unexpected eye potential")
    }

    @Test
    def testIsValid(): Unit = {
            Assertions.assertFalse(absHealthCalculator.isValid, "Unexpected isValidValue")
    }


}
