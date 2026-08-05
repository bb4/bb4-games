/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.group

import org.junit.Assert
import org.junit.Before
import org.junit.After
import org.junit.Test
import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone

class TestStoneInGroupAnalyzer extends GoTestCase {


    @Test
    def testDeadWhiteStoneInLiveBlackGroupIsMuchWeaker(): Unit = {

        // a black group that is mostly alive
        var group: IGoGroup = new StubGoGroup(0.6f, true, 4)
        // a white stone that is mostly dead.
        var stone: GoStone = new GoStone(false, 0.4f)
        verifyStoneWeaker(stone, group)
    }

    @Test
    def testSemiDeadWhiteStoneInMostlyLiveBlackGroupIsMuchWeaker(): Unit = {

        var group: IGoGroup = new StubGoGroup(0.5f, true, 4)
        var stone: GoStone = new GoStone(false, 0.2f)
        verifyStoneWeaker(stone, group)
    }

    @Test
    def testSemiLiveWhiteStoneInMostlyLiveBlackGroupIsNotMuchWeaker(): Unit = {

        var group: IGoGroup = new StubGoGroup(0.5f, true, 4)
        var stone: GoStone = new GoStone(false, -0.2f)
        verifyStoneNotWeaker(stone, group)
    }

    @Test
    def testDeadBlackStoneInLiveWhiteGroupIsMuchWeaker(): Unit = {

        var group: IGoGroup = new StubGoGroup(-0.6f, false, 4)
        var stone: GoStone = new GoStone(true, -0.4f)
        verifyStoneWeaker(stone, group)
    }

    @Test
    def testSemiDeadBlackStoneInMostlyLiveWhiteGroupIsMuchWeaker(): Unit = {

        var group: IGoGroup = new StubGoGroup(-0.5f, false, 4)
        var stone: GoStone = new GoStone(true, -0.2f)
        verifyStoneWeaker(stone, group)
    }

    @Test
    def testSemiLiveBlackStoneInMostlyLiveWhiteGroupIsNotMuchWeaker(): Unit = {

        var group: IGoGroup = new StubGoGroup(-0.5f, false, 4)
        var stone: GoStone = new GoStone(true, 0.2f)
        verifyStoneNotWeaker(stone, group)
    }

    private def verifyStoneWeaker(stone: GoStone, group: IGoGroup): Unit = {
        verifyStoneStrength(stone, group, true)
    }

    private def verifyStoneNotWeaker(stone: GoStone, group: IGoGroup): Unit = {
        verifyStoneStrength(stone, group, false)
    }

    private def verifyStoneStrength(stone: GoStone, group: IGoGroup, weaker: Boolean): Unit = {
        var analyzer: StoneInGroupAnalyzer = new StoneInGroupAnalyzer(group)
        Assert.assertEquals(weaker, analyzer.isStoneMuchWeakerThanGroup(stone, (group.asInstanceOf[StubGoGroup]).getAbsoluteHealth))
    }

}
