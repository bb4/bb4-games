/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.update

import org.junit.Assert
import org.junit.Before
import org.junit.After
import org.junit.Test
import com.barrybecker4.game.twoplayer.go.GoTestCase

class TestDeadStones extends GoTestCase {


    /** instance under test */
    private var deadStones: DeadStones = new DeadStones()

    @Test
    def testNoDeadStones(): Unit = {

        Assert.assertEquals("Unexpected dead stones", 0, deadStones.getNumberOnBoard(true))
        Assert.assertEquals("Unexpected dead stones", 0, deadStones.getNumberOnBoard(false))
    }

    @Test
    def testIncBlackDeadStones(): Unit = {

        deadStones.increment(true)
        Assert.assertEquals("Unexpected dead stones", 1, deadStones.getNumberOnBoard(true))

    }

    @Test
    def testIncBlackAndWhiteDeadStones(): Unit = {

        deadStones.increment(false)
        deadStones.increment(true)
        deadStones.increment(false)

        Assert.assertEquals("Unexpected black dead stones", 1, deadStones.getNumberOnBoard(true))
        Assert.assertEquals("Unexpected white dead stones", 2, deadStones.getNumberOnBoard(false))
    }

}
