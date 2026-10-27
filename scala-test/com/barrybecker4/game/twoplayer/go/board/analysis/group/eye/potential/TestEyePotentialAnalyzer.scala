/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.group.eye.potential

import org.junit.Assert
import org.junit.Before
import org.junit.After
import org.junit.Test
import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzerMap
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup

class TestEyePotentialAnalyzer extends GoTestCase {


    private val PREFIX: String = "board/analysis/group/eye/potential/"

    private val TOLERANCE: Double = 0.001


    @Test
    def testEyePotential_SingleIsolatedStone(): Unit = {

        restoreGame("single_isolated_stone")
        verifyBlackEyePotential(1, 0.344f)
        verifyWhiteEyePotential(1, 0.344f)
    }

    @Test
    def testEyePotential_SingleStoneNearEdge(): Unit = {

        restoreGame("single_stone_near_edge")
        verifyBlackEyePotential(1, 0.544f)
        verifyWhiteEyePotential(1, 0.596f)
    }

    @Test
    def testEyePotential_SingleStoneNearEnemy(): Unit = {

        restoreGame("single_stone_near_enemy")
        verifyBlackEyePotential(1, 0.4865f)    // why bigger?
        verifyWhiteEyePotential(1, 0.344f)
    }

    @Test
    def testEyePotential_SingleStoneAdjacentToEnemyBlackNearEdge(): Unit = {

        restoreGame("single_stone_adj_enemy_black_edge")
        verifyBlackEyePotential(1, 0.544f)
        verifyWhiteEyePotential(1, 0.344f)
    }

    @Test
    def testEyePotential_SingleStoneAdjacentToEnemyWhiteNearEdge(): Unit = {

        restoreGame("single_stone_adj_enemy_white_edge")
        verifyBlackEyePotential(1, 0.344f)
        verifyWhiteEyePotential(1, 0.544f)
    }

    @Test
    def testEyePotential_IsolatedClumpy(): Unit = {

        restoreGame("isolated_clumpy")
        verifyBlackEyePotential(7, 1.31f)
        verifyWhiteEyePotential(6, 0.544f)
    }

    @Test
    def testEyePotential_IsolatedClump(): Unit = {

        restoreGame("isolated_clump")
        verifyBlackEyePotential(4, 0.4865f)
        verifyWhiteEyePotential(4, 0.4865f)
    }

    @Test
    def testEyePotential_ClumpNearEdge(): Unit = {

        restoreGame("clump_near_edge")
        verifyBlackEyePotential(4, 0.688f)
        verifyWhiteEyePotential(4, 0.68802f)
    }

    @Test
    def testEyePotential_ClumpNearEnemy(): Unit = {

        restoreGame("clump_near_enemy")
        verifyBlackEyePotential(4, 0.644f)
        verifyWhiteEyePotential(4, 0.421f)
    }

    @Test
    def testEyePotential_SingleSpace(): Unit = {

        restoreGame("single_space_eye")
        verifyBlackEyePotential(7, 1.24f)
        verifyWhiteEyePotential(6, 0.9421f)
    }

    @Test
    def testEyePotential_ThreeSpaces(): Unit = {
        restoreGame("three_space_eye")
        verifyBlackEyePotential(9, 1.354f)
        verifyWhiteEyePotential(9, 1.276f)
    }

    @Test
    def testEyePotential_FourSpacesVertical(): Unit = {
        restoreGame("four_space_vertical_eye")
        verifyBlackEyePotential(12, 1.252f)
        verifyWhiteEyePotential(12, 1.228f)
    }

    @Test
    def testEyePotential_FourSpacesHorizontal(): Unit = {
        restoreGame("four_space_horizontal_eye")
        verifyBlackEyePotential(12, 1.1665963f)
        verifyWhiteEyePotential(11, 1.287f)
    }

    @Test
    def testEyePotential_BentFourSpaces(): Unit = {
        restoreGame("bent_four_space_eye")
        verifyBlackEyePotential(14, 1.64f)
        verifyWhiteEyePotential(13, 1.31f)
    }

    @Test
    def testEyePotential_FourSpacesOneInside(): Unit = {
        restoreGame("four_space_eye_one_inside")
        verifyBlackEyePotential(14, 1.5f)
        verifyWhiteEyePotential(13, 1.154f) // was 1.31
    }

    @Test
    def testEyePotential_FourSpacesTwoInside(): Unit = {
        restoreGame("four_space_eye_two_inside")
        verifyBlackEyePotential(14, 1.45f)    // was 1.64
        verifyWhiteEyePotential(13, 1.06f)    // was 1.31
    }

    @Test
    def testEyePotential_FourSpacesThreeInside(): Unit = {
        restoreGame("four_space_eye_three_inside")
        verifyBlackEyePotential(14, 1.397f)    // was 1.64
        verifyWhiteEyePotential(13, 0.988f)    // was 1.31
    }

    @Test
    def testEyePotential_ThreeOneSpaceJumps(): Unit = {
        restoreGame("three_one_space_jumps")
        verifyBlackEyePotential(1, 0.644f)
        verifyWhiteEyePotential(1, 0.769f)
    }

    @Test
    def testEyePotential_CShape(): Unit = {
        restoreGame("isolated_c_space")
        verifyBlackEyePotential(7, 0.842f)
        verifyWhiteEyePotential(7, 0.843f)
    }

    @Test
    def testEyePotential_SymmetricSmallC(): Unit = {
        restoreGame("symmetric_small_c")
        verifyBlackEyePotential(5, 0.75f)
        verifyWhiteEyePotential(5, 0.666f)
    }

    @Test
    def testEyePotential_SymmetricLargeC(): Unit = {
        restoreGame("symmetric_large_c")
        verifyBlackEyePotential(9, 1.074f)
        verifyWhiteEyePotential(9, 1.074f)
    }

    private def restoreGame(file: String): Unit = {
        restore(PREFIX + file)
    }

    private def verifyBlackEyePotential(expectedSizeOfGroup: Int, expectedPotential: Float): Unit = {
        verifyEyePotential(true, expectedSizeOfGroup, expectedPotential)
    }

    private def verifyWhiteEyePotential(expectedSizeOfGroup: Int, expectedPotential: Float): Unit = {
        verifyEyePotential(false, expectedSizeOfGroup, expectedPotential)
    }


    /**
     * Use EyePotentialAnalyzer to find potential of making eyes.
     */
    private def verifyEyePotential(forBlackGroup: Boolean, expectedSizeOfGroup: Int, expectedPotential: Float): Unit = {

        var group: IGoGroup = getBiggestGroup(forBlackGroup)

        var size: Int = group.getNumStones
        Assert.assertEquals("Unexpected size of test group.", expectedSizeOfGroup, size)

        var analyzer: EyePotentialAnalyzer = new EyePotentialAnalyzer(group, new GroupAnalyzerMap())
        analyzer.setBoard(getBoard)
        var eyePotential: Float = analyzer.calculateEyePotential()

        Assert.assertEquals("Unexpected group eye potential", expectedPotential, eyePotential, TOLERANCE)
    }


}
