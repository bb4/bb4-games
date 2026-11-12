/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.update

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import com.barrybecker4.game.twoplayer.go.GoTestCase

class TestDeadStones extends GoTestCase {


    /** instance under test */
    private var deadStones: DeadStones = new DeadStones()

    @Test
    def testNoDeadStones(): Unit = {

        Assertions.assertEquals(0, deadStones.getNumberOnBoard(true), "Unexpected dead stones")
        Assertions.assertEquals(0, deadStones.getNumberOnBoard(false), "Unexpected dead stones")
    }

    @Test
    def testIncBlackDeadStones(): Unit = {

        deadStones.increment(true)
        Assertions.assertEquals(1, deadStones.getNumberOnBoard(true), "Unexpected dead stones")

    }

    @Test
    def testIncBlackAndWhiteDeadStones(): Unit = {

        deadStones.increment(false)
        deadStones.increment(true)
        deadStones.increment(false)

        Assertions.assertEquals(1, deadStones.getNumberOnBoard(true), "Unexpected black dead stones")
        Assertions.assertEquals(2, deadStones.getNumberOnBoard(false), "Unexpected white dead stones")
    }

}
