/* Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.common.online;

import com.barrybecker4.game.common.GameOptions;
import com.barrybecker4.game.common.player.Player;

/**
 * Exposes {@link OnlineGameTable} constructor for tests.
 */
public final class TestOnlineGameTable extends OnlineGameTable {

    public TestOnlineGameTable(String name, Player initialPlayer, GameOptions options) {
        super(name, initialPlayer, options);
    }
}
