/* Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.common.online.server;

import com.barrybecker4.game.common.GameOptions;
import com.barrybecker4.game.common.online.GameCommand;
import com.barrybecker4.game.common.online.OnlineGameTable;
import com.barrybecker4.game.common.online.TestOnlineGameTable;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.multiplayer.trivial.TrivialController;
import com.barrybecker4.game.multiplayer.trivial.TrivialOptions;
import junit.framework.TestCase;

import java.awt.*;
import java.util.List;

/**
 * Command dispatch and response ordering for the online game server.
 */
public class ServerCommandProcessorTest extends TestCase {

    public void testEnterRoomPrependsUpdateTables() {
        TrivialController controller = new TrivialController();
        GameTableManager tables = new GameTableManager();
        ServerCommandProcessor proc = new ServerCommandProcessor(controller, tables);

        List<GameCommand> out = proc.processCommand(
                new GameCommand(GameCommand.Name.ENTER_ROOM, ""));

        assertEquals(1, out.size());
        assertEquals(GameCommand.Name.UPDATE_TABLES, out.getFirst().getName());
    }

    public void testChatMessageDoesNotPrependUpdateTables() {
        TrivialController controller = new TrivialController();
        GameTableManager tables = new GameTableManager();
        ServerCommandProcessor proc = new ServerCommandProcessor(controller, tables);

        GameCommand chat = new GameCommand(GameCommand.Name.CHAT_MESSAGE, "hi");
        List<GameCommand> out = proc.processCommand(chat);

        assertEquals(1, out.size());
        assertSame(chat, out.getFirst());
    }

    public void testAddTableReturnsTablesUpdate() {
        TrivialController controller = new TrivialController();
        GameTableManager tables = new GameTableManager();
        ServerCommandProcessor proc = new ServerCommandProcessor(controller, tables);

        GameOptions opts = new TrivialOptions(8, 0);
        Player owner = new Player("owner", Color.GREEN, true);
        OnlineGameTable table = new TestOnlineGameTable("alpha", owner, opts);

        List<GameCommand> out = proc.processCommand(new GameCommand(GameCommand.Name.ADD_TABLE, table));

        assertEquals(1, out.size());
        assertEquals(GameCommand.Name.UPDATE_TABLES, out.getFirst().getName());
        assertEquals(1, tables.getTables().size());
    }
}
