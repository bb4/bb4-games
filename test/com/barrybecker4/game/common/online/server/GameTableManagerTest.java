/* Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.common.online.server;

import com.barrybecker4.game.common.GameOptions;
import com.barrybecker4.game.common.online.OnlineGameTable;
import com.barrybecker4.game.common.online.TestOnlineGameTable;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.multiplayer.trivial.TrivialOptions;
import junit.framework.TestCase;

import java.awt.*;

/**
 * Table list operations used by the online server.
 */
public class GameTableManagerTest extends TestCase {

    private GameTableManager manager;

    @Override
    protected void setUp() {
        manager = new GameTableManager();
    }

    public void testAddTableAssignsUniqueSuffix() {
        GameOptions opts = new TrivialOptions(8, 0);
        Player alice = new Player("alice", Color.RED, true);
        OnlineGameTable t1 = new TestOnlineGameTable("lobby", alice, opts);
        manager.addTable(t1);
        assertEquals("lobby_0", t1.getName());

        Player bob = new Player("bob", Color.BLUE, true);
        OnlineGameTable t2 = new TestOnlineGameTable("lobby", bob, opts);
        manager.addTable(t2);
        assertEquals("lobby_1", t2.getName());
        assertEquals(2, manager.getTables().size());
    }

    public void testChangeNameUpdatesTablePlayers() {
        GameOptions opts = new TrivialOptions(8, 0);
        Player alice = new Player("alice", Color.RED, true);
        OnlineGameTable table = new TestOnlineGameTable("t", alice, opts);
        manager.addTable(table);
        manager.changeName("alice", "alice2");
        assertTrue(table.hasPlayer("alice2"));
        assertFalse(table.hasPlayer("alice"));
    }
}
