/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.group;

/**
 * Always returns a fixed value depending on boolean passed to constructor.
 *
 * @author Barry Becker
 */
public final class StubLifeAnalyzer extends LifeAnalyzer {

    boolean isUnconditionallyAlive;

    /**
     * Constructor.
     * @param isUnconditionallyAlive always returns this value.
     */
    public StubLifeAnalyzer(boolean isUnconditionallyAlive) {
        this.isUnconditionallyAlive = isUnconditionallyAlive;
    }

    /**
     * Use Benson's algorithm (1977) to determine if a set of strings and eyes within a group
     * is unconditionally alive.
     *
     * @return true if unconditionally alive
     */
    @Override
    public boolean isUnconditionallyAlive() {
        return isUnconditionallyAlive;
    }

}
