// Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.mancala.move;

import com.barrybecker4.common.geometry.ByteLocation;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Barry Becker
 */
public class MancalaMoveTest {

    /** instance under test. */
    private MancalaMove move;


    @Test
    public void testSerializeSimpleMove() {

        move = new MancalaMove(true, new ByteLocation(1, 5), (byte)3, 0);

        assertEquals("Unexpected serialized form.",
                "Player 1 val:0 inhrtd:0 piece: X((row=1, column=5)) \n" +
                "stones moved = 3",
                move.toString());
    }

    @Test
    public void testSerializeCompoundMove() {

        move = new MancalaMove(true, new ByteLocation(1, 4), (byte)3, 0);
        MancalaMove followUp = new MancalaMove(true, new ByteLocation(1, 6), (byte)3, 0);
        MancalaMove followUp2 = new MancalaMove(true, new ByteLocation(1, 2), (byte)4, 0);
        followUp.setFollowUpMove(followUp2);
        move.setFollowUpMove(followUp);

        assertEquals("Unexpected serialized form.",
                "Player 1 val:0 inhrtd:0 piece: X((row=1, column=4)) \n" +
                "stones moved = 3\n" +
                "[\n" +
                "   Player 1 val:0 inhrtd:0 piece: X((row=1, column=6)) \n" +
                "   stones moved = 3\n" +
                "   [\n" +
                "      Player 1 val:0 inhrtd:0 piece: X((row=1, column=2)) \n" +
                "      stones moved = 4\n" +
                "   ]\n" +
                "]",
                move.toString());
    }

}