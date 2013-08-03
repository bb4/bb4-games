// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.online;

/**
 * @author Barry Becker
 */
public interface OnlinePlayer {

    /**
     * @return either the player itself, or it a surrogate player, then the player that ht surrogate contains.
     */
    OnlinePlayer getActualPlayer();

    /** @return true if this is a surrogate player */
    boolean isSurrogate();
}
