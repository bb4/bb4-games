/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import java.util.ArrayList
import java.util.List
import scala.jdk.CollectionConverters._

class TestCandidateMoveAnalyzer extends GoTestCase {


    /** we can just reuse one of the other file sets */
    private val PREFIX: String = "scoring/"


    @Test
    def testCandidateMoves1(): Unit = {
        verifyCandidateMoves("problem_score1", 93, null)
    }

    @Test
    def testCandidateMoves2(): Unit = {
        verifyCandidateMoves("problem_score2", 89, null)
    }

    @Test
    def testCandidateMoves3(): Unit = {
        var expCandidates: List[Location] = new java.util.ArrayList[Location]()
        expCandidates.add(new ByteLocation(2, 5))
        expCandidates.add(new ByteLocation(1, 4))
        expCandidates.add(new ByteLocation(4, 3))
        expCandidates.add(new ByteLocation(5, 2))

        verifyCandidateMoves("problem_score55a", 10/*4*/, expCandidates)
    }

    /** XXXX sometimes passes sometimes fails. odd  */
    @Test
    def testCandidateMoves4(): Unit = {

        System.out.println("------------cm4 --------------------------")
        var expCandidates: List[Location] = new java.util.ArrayList[Location]()
        expCandidates.add(new ByteLocation(1, 1))
        expCandidates.add(new ByteLocation(1, 5))
        expCandidates.add(new ByteLocation(2, 1))
        expCandidates.add(new ByteLocation(2, 3))
        expCandidates.add(new ByteLocation(2, 4))
        expCandidates.add(new ByteLocation(3, 2))
        expCandidates.add(new ByteLocation(4, 1))
        expCandidates.add(new ByteLocation(5, 1))
        expCandidates.add(new ByteLocation(5, 4))
        expCandidates.add(new ByteLocation(5, 5))

        verifyCandidateMoves("problem_score55b", 10, expCandidates)
    }


    /**
     * Verify candidate move generation.
     */
    private def verifyCandidateMoves(file: String, expNumCandidates: Int, expCandidates: List[Location]): Unit = {
        restore(PREFIX + file)

        var board: GoBoard = getBoard
        var cma: CandidateMoveAnalyzer = new CandidateMoveAnalyzer(board)

        var actNumCandidates: Int = cma.getNumCandidates
        Assertions.assertEquals(expNumCandidates, actNumCandidates, "Unexpected number of candidate moves for case  "+ file)

        if (expCandidates != null) {
            System.out.println("var candidates: actual ="+ actNumCandidates)
            for (loc <- expCandidates.asScala) {
                Assertions.assertTrue(cma.isCandidateMove(loc.row, loc.col), "Invalid candidate position:" + loc)
            }
        }
    }


}
