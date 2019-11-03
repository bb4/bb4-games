/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.group;

import com.barrybecker4.game.twoplayer.go.GoTestCase;
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone;

/**
 * Mostly test that the scoring of groups works correctly.
 * @author Barry Becker
 */
public class TestStoneInGroupAnalyzer extends GoTestCase {

    public void testDeadWhiteStoneInLiveBlackGroupIsMuchWeaker() {

        // a black group that is mostly alive
        IGoGroup group = new StubGoGroup(0.6f, true, 4);
        // a white stone that is mostly dead.
        GoStone stone = new GoStone(false, 0.4f);
        verifyStoneWeaker(stone, group);
    }

    public void testSemiDeadWhiteStoneInMostlyLiveBlackGroupIsMuchWeaker() {

        IGoGroup group = new StubGoGroup(0.5f, true, 4);
        GoStone stone = new GoStone(false, 0.2f);
        verifyStoneWeaker(stone, group);
    }

    public void testSemiLiveWhiteStoneInMostlyLiveBlackGroupIsNotMuchWeaker() {

        IGoGroup group = new StubGoGroup(0.5f, true, 4);
        GoStone stone = new GoStone(false, -0.2f);
        verifyStoneNotWeaker(stone, group);
    }

    public void testDeadBlackStoneInLiveWhiteGroupIsMuchWeaker() {

        IGoGroup group = new StubGoGroup(-0.6f, false, 4);
        GoStone stone = new GoStone(true, -0.4f);
        verifyStoneWeaker(stone, group);
    }

    public void testSemiDeadBlackStoneInMostlyLiveWhiteGroupIsMuchWeaker() {

        IGoGroup group = new StubGoGroup(-0.5f, false, 4);
        GoStone stone = new GoStone(true, -0.2f);
        verifyStoneWeaker(stone, group);
    }

    public void testSemiLiveBlackStoneInMostlyLiveWhiteGroupIsNotMuchWeaker() {

        IGoGroup group = new StubGoGroup(-0.5f, false, 4);
        GoStone stone = new GoStone(true, 0.2f);
        verifyStoneNotWeaker(stone, group);
    }

    private void verifyStoneWeaker(GoStone stone, IGoGroup group) {
        verifyStoneStrength(stone, group, true);
    }

    private void verifyStoneNotWeaker(GoStone stone, IGoGroup group) {
        verifyStoneStrength(stone, group, false);
    }

    private void verifyStoneStrength(GoStone stone, IGoGroup group, boolean weaker) {
        StoneInGroupAnalyzer analyzer = new StoneInGroupAnalyzer(group);
        assertEquals(weaker, analyzer.isStoneMuchWeakerThanGroup(stone, ((StubGoGroup)group).getAbsoluteHealth()));
    }
}
