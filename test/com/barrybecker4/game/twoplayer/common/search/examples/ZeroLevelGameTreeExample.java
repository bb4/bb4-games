/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.examples;

/**
 * A simple game tree for testing search strategies.
 * It looks something like this
 *                 ____   [6]  _____
 *
 * @author Barry Becker
 */
public class ZeroLevelGameTreeExample extends AbstractGameTreeExample  {


    public ZeroLevelGameTreeExample(boolean player1PlaysNext, EvaluationPerspective persp) {

        super(persp);

        initialMove = moveCreator.createMove(6, !player1PlaysNext, null);
    }
}
