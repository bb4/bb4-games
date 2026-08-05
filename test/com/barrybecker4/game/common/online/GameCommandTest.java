/* Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.common.online;

import junit.framework.TestCase;

/**
 * Tests for online protocol command objects.
 */
public class GameCommandTest extends TestCase {

    public void testRequireNonNullArgument() {
        try {
            new GameCommand(GameCommand.Name.ENTER_ROOM, null);
            fail("expected NullPointerException");
        } catch (NullPointerException e) {
            assertTrue(e.getMessage().contains("argument"));
        }
    }

    public void testRequireNonNullName() {
        try {
            new GameCommand(null, "");
            fail("expected NullPointerException");
        } catch (NullPointerException e) {
            assertTrue(e.getMessage().contains("name"));
        }
    }

    public void testToStringAndChangeNameDelimiter() {
        GameCommand cmd = new GameCommand(GameCommand.Name.CHAT_MESSAGE, "hello");
        assertTrue(cmd.toString().contains("CHAT_MESSAGE"));
        assertTrue(cmd.toString().contains("hello"));

        String payload = "old" + GameCommand.CHANGE_TO() + "new";
        GameCommand rename = new GameCommand(GameCommand.Name.CHANGE_NAME, payload);
        String[] parts = ((String) rename.getArgument()).split(GameCommand.CHANGE_TO());
        assertEquals(2, parts.length);
        assertEquals("old", parts[0]);
        assertEquals("new", parts[1]);
    }
}
