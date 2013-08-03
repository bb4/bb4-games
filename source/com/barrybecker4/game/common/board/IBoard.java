// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.board;

/**
 * This is the interface that all game boards should implement.
 * We assume that the board is composed of a 2D array of BoardPositions.
 *
 * Providing both an interface and an abstract implementation is a pattern
 * which maximizes flexibility in a framework. The interface defines the
 * public contract. The abstract class may be package private if we don't
 * want to expose it. Or other classes may implement this interface without
 * extending the abstract base class.
 *
 * @author Barry Becker
 */
public interface IBoard {

    /**
     *  Reset the board to its initial starting state.
     */
    void reset();


    /**
     * We should be able to create a deep copy of ourselves
     * @return deep copy of the board.
     */
    IBoard copy();
}
