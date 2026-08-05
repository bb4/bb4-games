/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.group

import org.junit.Assert
import org.junit.Before
import org.junit.After
import org.junit.Test
import com.barrybecker4.common.format.FormatUtil
import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.E1Information
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.E2Information
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.EyeInformation
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.FalseEyeInformation
import com.barrybecker4.game.twoplayer.go.board.elements.eye.GoEyeSet
import com.barrybecker4.game.twoplayer.go.board.elements.eye.IGoEye
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup
import java.util.Arrays
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.E4Information.Eye4Type.E2222
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.E5Information.Eye5Type.E12223
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.E6Information.Eye6Type.E222233
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.EyeType.E4
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.EyeType.E5
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.EyeType.E6
import scala.jdk.CollectionConverters._

class TestAbsoluteHealthAnalyzer extends GoTestCase {


    private val PREFIX: String = "board/analysis/grouphealth/"

    // test absolute health calculation, and the number of liberties for the main black and white groups.
    // testAbsHealth1* test configurations with 1 stone in each group.
    @Test
    def testAbsHealth1(): Unit = {
        controller.reset()
        var blackEyes: Array[EyeInformation] = Array()
        var whiteEyes: Array[EyeInformation] = Array()
        var bPotential: Double = 0.344
        var wPotential: Double = 0.49
        var blackHealth: Double = 0.1
        var whiteHealth: Double = -0.1
        var AbsAbsHealthDiff: Double = 0.0
        var AbsHealthDiff: Double = 0.2
        var RelHealthDiff: Double = 0.2
        verifyHealthDifferences("groupHealth1", 4, 4, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff)
    }


    @Test
    def testAbsHealth1a(): Unit = {
        controller.reset()
        var blackEyes: Array[EyeInformation] = Array()
        var whiteEyes: Array[EyeInformation] = Array()
        var bPotential: Double = 0.3
        var wPotential: Double = 0.3
        var blackHealth: Double = 0.1
        var whiteHealth: Double = -0.1
        var AbsAbsHealthDiff: Double = 0.0
        var AbsHealthDiff: Double = 0.2
        var RelHealthDiff: Double = 0.2
        verifyHealthDifferences("groupHealth1a", 3, 3, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff)
    }
    @Test
    def testAbsHealth1b(): Unit = {
        controller.reset()
        var blackEyes: Array[EyeInformation] = Array()
        var whiteEyes: Array[EyeInformation] = Array()
        var bPotential: Double = 0.34
        var wPotential: Double = 0.34
        var blackHealth: Double = 0.1
        var whiteHealth: Double = -0.1
        var AbsAbsHealthDiff: Double = 0.0
        var AbsHealthDiff: Double = 0.2
        var RelHealthDiff: Double = 0.2
        verifyHealthDifferences("groupHealth1b", 4, 4, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff)
    }
    @Test
    def testAbsHealth1c(): Unit = {
        controller.reset()
        var blackEyes: Array[EyeInformation] = Array()
        var whiteEyes: Array[EyeInformation] = Array()
        var bPotential: Double = 0.49
        var wPotential: Double = 0.5958
        var blackHealth: Double = 0.1
        var whiteHealth: Double = -0.1
        var AbsAbsHealthDiff: Double = 0.0
        var AbsHealthDiff: Double = 0.2
        var RelHealthDiff: Double = 0.2
        verifyHealthDifferences("groupHealth1c", 4, 4, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff)
    }
    @Test
    def testAbsHealth1d(): Unit = {
        controller.reset()
        var blackEyes: Array[EyeInformation] = Array()
        var whiteEyes: Array[EyeInformation] = Array()
        var bPotential: Double = 0.24
        var wPotential: Double = 0.0
        var blackHealth: Double = 0.1
        var whiteHealth: Double = -0.02
        var AbsAbsHealthDiff: Double = 0.0
        var AbsHealthDiff: Double = 0.2
        var RelHealthDiff: Double = 0.2
        verifyHealthDifferences("groupHealth1d", 3, 2, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff)
    }

    @Test
    def testAbsHealth2(): Unit = {
        var blackEyes: Array[EyeInformation] = Array()
        var whiteEyes: Array[EyeInformation] = Array()
        var bPotential: Double = 1.18
        var wPotential: Double = 0.769
        var blackHealth: Double = 0.52
        var whiteHealth: Double = -0.348
        var AbsAbsHealthDiff: Double = 0.1
        var AbsHealthDiff: Double = 0.8
        var RelHealthDiff: Double = 0.872
        verifyHealthDifferences("groupHealth2", 19, 14, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff)
    }
    @Test
    def testAbsHealth2a(): Unit = {
        var blackEyes: Array[EyeInformation] = Array()
        var whiteEyes: Array[EyeInformation] = Array()
        var bPotential: Double = 0.42
        var wPotential: Double = 0.42
        var blackHealth: Double = 0.2
        var whiteHealth: Double = -0.2
        var AbsAbsHealthDiff: Double = 0.0
        var AbsHealthDiff: Double = 0.4
        var RelHealthDiff: Double = 0.4
        verifyHealthDifferences("groupHealth2a", 6, 6, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff)
   }
    @Test
    def testAbsHealth2b(): Unit = {
        var blackEyes: Array[EyeInformation] = Array()
        var whiteEyes: Array[EyeInformation] = Array()
        var bPotential: Double = 0.73
        var wPotential: Double = 0.64
        var blackHealth: Double = 0.2
        var whiteHealth: Double = -0.2
        var AbsAbsHealthDiff: Double = 0.0
        var AbsHealthDiff: Double = 0.4
        var RelHealthDiff: Double = 0.4
        verifyHealthDifferences("groupHealth2b", 6, 6, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff)
    }
    @Test
    def testAbsHealth2c(): Unit = {
        var blackEyes: Array[EyeInformation] = Array()
        var whiteEyes: Array[EyeInformation] = Array()
        var bPotential: Double = 0.54
        var wPotential: Double = 0.38
        var blackHealth: Double = 0.2
        var whiteHealth: Double = -0.1
        var AbsAbsHealthDiff: Double = 0.1
        var AbsHealthDiff: Double = 0.3
        var RelHealthDiff: Double = 0.3
        verifyHealthDifferences("groupHealth2c", 6, 5, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff)
   }
    @Test
    def testAbsHealth2d(): Unit = {
        var blackEyes: Array[EyeInformation] = Array()
        var whiteEyes: Array[EyeInformation] = Array()
        var bPotential: Double = 0.385
        var wPotential: Double = 0.384
        var blackHealth: Double = 0.1
        var whiteHealth: Double = -0.1
        var AbsAbsHealthDiff: Double = 0.0
        var AbsHealthDiff: Double = 0.2
        var RelHealthDiff: Double = 0.3
        verifyHealthDifferences("groupHealth2d", 5, 5, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff)
    }
    @Test
    def testAbsHealth2e(): Unit = {
        var blackEyes: Array[EyeInformation] = Array()
        var whiteEyes: Array[EyeInformation] = Array()
        var bPotential: Double = 0.516
        var wPotential: Double = 0.344
        var blackHealth: Double = 0.1
        var whiteHealth: Double = -0.05
        var AbsAbsHealthDiff: Double = 0.1
        var AbsHealthDiff: Double = 0.15
        var RelHealthDiff: Double = 0.15
        verifyHealthDifferences("groupHealth2e", 5, 4, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff)
   }
    @Test
    def testAbsHealth2f(): Unit = {
        var blackEyes: Array[EyeInformation] = Array()
        var whiteEyes: Array[EyeInformation] = Array()
        var bPotential: Double = 0.596
        var wPotential: Double = 0.385
        var blackHealth: Double = 0.1
        var whiteHealth: Double = -0.1
        var AbsAbsHealthDiff: Double = 0.0
        var AbsHealthDiff: Double = 0.2
        var RelHealthDiff: Double = 0.2
        verifyHealthDifferences("groupHealth2f", 5, 5, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff)
    }
    @Test
    def testAbsHealth2g(): Unit = {
        var blackEyes: Array[EyeInformation] = Array()
        var whiteEyes: Array[EyeInformation] = Array()
        var bPotential: Double = 0.17
        var wPotential: Double = 0.54393
        var blackHealth: Double = -0.3
        var whiteHealth: Double = -0.05
        var AbsAbsHealthDiff: Double = 0.25
        var AbsHealthDiff: Double = -0.25
        var RelHealthDiff: Double = -0.3375
        verifyHealthDifferences("groupHealth2g", 2, 4, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff)
    }

    @Test
    def testAbsHealth3(): Unit = {
        val blackEyes: Array[EyeInformation] = Array(E6.getInformation(E222233.toString), E6.getInformation(E222233.toString))
        var whiteEyes: Array[EyeInformation] = Array()
        var bPotential: Double = 1.58
        var wPotential: Double = 0.57
        var blackHealth: Double = 0.94
        var whiteHealth: Double = -0.2
        var AbsAbsHealthDiff: Double = 0.8
        var AbsHealthDiff: Double = 1.1
        var RelHealthDiff: Double = 1.1
        verifyHealthDifferences("groupHealth3", 20, 6, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff)
    }

    @Test
    def testAbsHealth3a(): Unit = {
        var blackEyes: Array[EyeInformation] = Array()
        var whiteEyes: Array[EyeInformation] = Array()
        var bPotential: Double = 0.877
        var wPotential: Double = 0.596
        var blackHealth: Double = 0.24
        var whiteHealth: Double = -0.24
        var AbsAbsHealthDiff: Double = 0.0
        var AbsHealthDiff: Double = 0.48
        var RelHealthDiff: Double = 0.48
        verifyHealthDifferences("groupHealth3a", 8, 8, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff)
    }
    @Test
    def testAbsHealth3b(): Unit = {
        var blackEyes: Array[EyeInformation] = Array()
        var whiteEyes: Array[EyeInformation] = Array()
        var bPotential: Double = 0.6661734 // 0.82
        var wPotential: Double = 0.5958436 // 0.51
        var blackHealth: Double = 0.10
        var whiteHealth: Double = -0.10
        var AbsAbsHealthDiff: Double = 0.0
        var AbsHealthDiff: Double = 0.2
        var RelHealthDiff: Double = 0.3
        verifyHealthDifferences("groupHealth3b", 5, 5, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff)
    }
    @Test
    def testAbsHealth3c(): Unit = {
        val blackEyes: Array[EyeInformation] = Array(new FalseEyeInformation())
        var whiteEyes: Array[EyeInformation] = Array()
        var bPotential: Double = 0.59584
        var wPotential: Double = 0.51602
        var blackHealth: Double = 0.02
        var whiteHealth: Double = -0.1
        var AbsAbsHealthDiff: Double = -0.08
        var AbsHealthDiff: Double = 0.12
        var RelHealthDiff: Double = 0.12
        verifyHealthDifferences("groupHealth3c", 3, 5, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff)
    }

    @Test
    def testAbsHealth4(): Unit = {
        var blackEyes: Array[EyeInformation] = Array()  // should probably have a territorial eye here
        val whiteEyes: Array[EyeInformation] = Array(new E1Information())
        var bPotential: Double = 1.9
        var wPotential: Double = 0.877
        var blackHealth: Double = 0.79
        var whiteHealth: Double = -0.405
        var AbsAbsHealthDiff: Double = 0.388
        var AbsHealthDiff: Double = 1.11
        var RelHealthDiff: Double = 1.56  //1.11
        verifyHealthDifferences("groupHealth4", 33, 12, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff)
    }
    @Test
    def testAbsHealth4a(): Unit = {
        var blackEyes: Array[EyeInformation] = Array()  // should really have a territorial eye here.
        var whiteEyes: Array[EyeInformation] = Array()
        var bPotential: Double = 1.06
        var wPotential: Double = 0.54
        var blackHealth: Double = 0.36
        var whiteHealth: Double = -0.28
        var AbsAbsHealthDiff: Double =0.083
        var AbsHealthDiff: Double = 0.64
        var RelHealthDiff: Double = 0.64
        verifyHealthDifferences("groupHealth4a", 10, 10, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff)
    }
    @Test
    def testAbsHealth4b(): Unit = {
        var blackEyes: Array[EyeInformation] = Array()  // should really have a territorial eye here.
        var whiteEyes: Array[EyeInformation] = Array()
        var bPotential: Double = 0.788
        var wPotential: Double = 0.91
        var blackHealth: Double = 0.26
        var whiteHealth: Double = -0.315
        var AbsAbsHealthDiff: Double = -0.054
        var AbsHealthDiff: Double = 0.58
        var RelHealthDiff: Double = 0.58
        verifyHealthDifferences("groupHealth4b", 9, 12, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff)
    }

    @Test
    def testAbsHealth5(): Unit = {
        val blackEyes: Array[EyeInformation] = Array(new E2Information())
        var whiteEyes: Array[EyeInformation] = Array()
        var bPotential: Double = 0.7497
        var wPotential: Double = 0.6436
        var blackHealth: Double = 0.01
        var whiteHealth: Double = -0.24
        var AbsAbsHealthDiff: Double = -0.23
        var AbsHealthDiff: Double = 0.25
        var RelHealthDiff: Double = 0.41
        verifyHealthDifferences("groupHealth5", 4, 8, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff)
    }

    @Test
    def testAbsHealth5a(): Unit = {
        var blackEyes: Array[EyeInformation] = Array()
        var whiteEyes: Array[EyeInformation] = Array()
        var bPotential: Double = 0.71
        var wPotential: Double = 1.09
        var blackHealth: Double = 0.28
        var whiteHealth: Double = -0.405
        var AbsAbsHealthDiff: Double = -0.125
        var AbsHealthDiff: Double = 0.685
        var RelHealthDiff: Double = 0.685
        verifyHealthDifferences("groupHealth5a", 10, 12, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff)
    }

    @Test
    def testAbsHealth5b(): Unit = {
        val blackEyes: Array[EyeInformation] = Array(E4.getInformation(E2222.toString))
        var whiteEyes: Array[EyeInformation] = Array()
        var bPotential: Double = 1.003
        var wPotential: Double = 1.14
        var blackHealth: Double = 0.34
        var whiteHealth: Double = -0.42
        var AbsAbsHealthDiff: Double = -0.084
        var AbsHealthDiff: Double = 0.764
        var RelHealthDiff: Double = 0.764
        verifyHealthDifferences("groupHealth5b", 9, 13, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff)
    }

    @Test
    def testAbsHealth6(): Unit = {       // seki
        val blackEyes: Array[EyeInformation] = Array(new E1Information())
        val whiteEyes: Array[EyeInformation] = Array(new E1Information())
        var bPotential: Double = 0.67
        var wPotential: Double = 0.57
        var blackHealth: Double = -0.30
        var whiteHealth: Double = 0.30
        var AbsAbsHealthDiff: Double = 0.0
        var AbsHealthDiff: Double = -0.6
        var RelHealthDiff: Double = -0.9
        verifyHealthDifferences("groupHealth6", 2, 2, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff)
    }

    @Test
    def testAbsHealth6a(): Unit = {
        val blackEyes: Array[EyeInformation] = Array(new E1Information(), new E1Information())
        var whiteEyes: Array[EyeInformation] = Array()
        var bPotential: Double = 0.9881
        var wPotential: Double = 1.397
        var blackHealth: Double = 1.0 //0.9399999976158142
        var whiteHealth: Double = -0.5
        var AbsAbsHealthDiff: Double = 0.49
        var AbsHealthDiff: Double = 1.5
        var RelHealthDiff: Double = 1.5
        verifyHealthDifferences("groupHealth6a", 8, 18, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff)
    }

     @Test
    def testAbsHealth7(): Unit = {
        val blackEyes: Array[EyeInformation] = Array(E6.getInformation(E222233.toString), E5.getInformation(E12223.toString))
        var whiteEyes: Array[EyeInformation] = Array()
        var bPotential: Double = 1.55
        var wPotential: Double = 0.69
        var blackHealth: Double = 0.94
        var whiteHealth: Double = -0.2
        var AbsAbsHealthDiff: Double = 0.74
        var AbsHealthDiff: Double = 1.14
        var RelHealthDiff: Double = 1.14
        verifyHealthDifferences("groupHealth7", 19, 6, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff)
    }

    @Test
    def testAbsHealth8(): Unit = {
        var blackEyes: Array[EyeInformation] = Array()
        var whiteEyes: Array[EyeInformation] = Array()
        var bPotential: Double = 0.54
        var wPotential: Double = 0.89
        var blackHealth: Double = 0.315
        var whiteHealth: Double = -0.3
        var AbsAbsHealthDiff: Double = 0.017
        var AbsHealthDiff: Double = 0.613
        var RelHealthDiff: Double = 0.92
        verifyHealthDifferences("groupHealth8", 12, 11, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff)
    }

    @Test
    def testAbsHealth9(): Unit = {
        var blackEyes: Array[EyeInformation] = Array()
        var whiteEyes: Array[EyeInformation] = Array()
        var bPotential: Double = 1.49
        var wPotential: Double = 0.91
        var blackHealth: Double = 0.44
        var whiteHealth: Double = -0.22
        var AbsAbsHealthDiff: Double = 0.22
        var AbsHealthDiff: Double = 0.663
        var RelHealthDiff: Double = 1.0
        verifyHealthDifferences("groupHealth9", 14, 7, blackEyes, whiteEyes,
                                                 bPotential, wPotential,
                                                 blackHealth, whiteHealth,
                                                 AbsAbsHealthDiff, AbsHealthDiff, RelHealthDiff)
    }



    private val THRESH: Double = 0.10
    private val EPS: Double = 0.01

    /**
     * Verify the black - white goup health differences for
     * the abs(absolute health), absolute health, and relative health.
     */
    private def verifyHealthDifferences(file: String, expectedNumberOfBlackLiberties: Int, expectedNumberOfWhiteLiberties: Int, blackEyes: Array[EyeInformation], whiteEyes: Array[EyeInformation], expectedBlackEyePotential: Double, expectedWhiteEyePotential: Double, expectedBlackHealth: Double, expectedWhiteHealth: Double, expectedAbsAbsHealthDifference: Double, expectedAbsHealthDifference: Double, expectedRelHealthDifference: Double): Unit = {
        restore(PREFIX + file)

        // find the biggest black and white groups
        var bg: IGoGroup = getBiggestGroup(true)
        var wg: IGoGroup = getBiggestGroup(false)

        var board: GoBoard = getBoard

        var analyzerMap: GroupAnalyzerMap = new GroupAnalyzerMap()
        var blackHealthAnalyzer: GroupAnalyzer = new GroupAnalyzer(bg, analyzerMap)
        var whiteHealthAnalyzer: GroupAnalyzer = new GroupAnalyzer(wg, analyzerMap)

        var bah: Double = blackHealthAnalyzer.calculateAbsoluteHealth(board)
        var wah: Double = whiteHealthAnalyzer.calculateAbsoluteHealth(board)

        var numBlackLiberties: Int = blackHealthAnalyzer.getNumLiberties(null)
        var numWhiteLiberties: Int = whiteHealthAnalyzer.getNumLiberties(null)

        blackHealthAnalyzer.invalidate()
        whiteHealthAnalyzer.invalidate()

         // verify that we have the expected number and type of eyes for that biggest group
        verifyEyes(blackHealthAnalyzer.getEyes(board), blackEyes, true)
        verifyEyes(whiteHealthAnalyzer.getEyes(board), whiteEyes, false)

        // find a different way to do this. In separate EyePotential test.
        var bPotential: Float = blackHealthAnalyzer.getEyePotential
        var wPotential: Float = whiteHealthAnalyzer.getEyePotential

        var brh: Double = blackHealthAnalyzer.calculateRelativeHealth(board)
        var wrh: Double = whiteHealthAnalyzer.calculateRelativeHealth(board)

        var abah: Double = Math.abs(bah)
        var awah: Double = Math.abs(wah)

        var daah: Double = abah - awah
        var dah: Double = bah - wah
        var drh: Double = brh - wrh

        // if any of the assertions are going to fail, lets print all the results nicely so they can be copied over easily
        var libertiesOK: Boolean = (expectedNumberOfBlackLiberties == numBlackLiberties) && (expectedNumberOfWhiteLiberties == numWhiteLiberties)
        var bEyePotentialOK: Boolean = approximatelyEqual(expectedBlackEyePotential, bPotential, EPS)
        var wEyePotentialOK: Boolean = approximatelyEqual(expectedWhiteEyePotential, wPotential, EPS)
        var bHealthOK: Boolean = approximatelyEqual(expectedBlackHealth, bah, EPS)
        var wHealthOK: Boolean = approximatelyEqual(expectedWhiteHealth, wah, EPS)
        var absAbsDifOK: Boolean = approximatelyEqual(daah, expectedAbsAbsHealthDifference, THRESH)
        var absDifOK: Boolean = approximatelyEqual(dah, expectedAbsHealthDifference, THRESH)
        var relDifOK: Boolean = approximatelyEqual(drh, expectedRelHealthDifference, THRESH)

        if (!(libertiesOK && bEyePotentialOK && wEyePotentialOK && bHealthOK && wHealthOK && absAbsDifOK && absDifOK && relDifOK))  {
            System.out.println( file + "     \t exp \t got ")
            System.out.println("               \t\t-----\t-----")
            System.out.println("black liberties    \t "+ expectedNumberOfBlackLiberties +"\t " + numBlackLiberties + errorMarker(libertiesOK))
            System.out.println("white liberties    \t "+ expectedNumberOfWhiteLiberties +"\t " + numWhiteLiberties + errorMarker(libertiesOK) )
            System.out.println("black eye potential\t" + expectedBlackEyePotential + "\t " + FormatUtil.formatNumber(bPotential) + errorMarker(bEyePotentialOK))
            System.out.println("white eye potential\t" + expectedWhiteEyePotential + "\t " + FormatUtil.formatNumber(wPotential) + errorMarker(wEyePotentialOK))

            System.out.println("black health      \t" + expectedBlackHealth + "\t " + FormatUtil.formatNumber(bah) + errorMarker(bHealthOK))
            System.out.println("white health      \t" + expectedWhiteHealth + "\t " + FormatUtil.formatNumber(wah) + errorMarker(wHealthOK))

            System.out.println("absAbs health diff \t" + expectedAbsAbsHealthDifference + "\t " + FormatUtil.formatNumber(daah) + errorMarker(absAbsDifOK))
            System.out.println("absolute health diff\t" + expectedAbsHealthDifference + "\t " + FormatUtil.formatNumber(dah) + errorMarker(absDifOK))
            System.out.println("relative health diff \t" + expectedRelHealthDifference + "\t " + FormatUtil.formatNumber(drh) + errorMarker(relDifOK))

            System.out.println("black eyes: "+ blackHealthAnalyzer.getEyes(board))
            System.out.println("white eyes: "+ whiteHealthAnalyzer.getEyes(board))

            System.out.println("\n")
        }

        Assert.assertEquals(file + ". Expected num liberties: black ="+ expectedNumberOfBlackLiberties + " but got "+numBlackLiberties,
                 expectedNumberOfBlackLiberties, numBlackLiberties)
        Assert.assertEquals(file + ". Expected num liberties: white ="+ expectedNumberOfWhiteLiberties + " but got "+numWhiteLiberties,
                 expectedNumberOfWhiteLiberties, numWhiteLiberties)

        Assert.assertTrue(file + ".  Unexpected black eye potential: eye = " + bPotential,  bEyePotentialOK)
        Assert.assertTrue(file + ".  Unexpected white eye potential: eye = " + wPotential,  wEyePotentialOK)

        Assert.assertTrue(file + ".  Unexpected health: black = " + bah,  bHealthOK)
        Assert.assertTrue(file + ".  Unexpected health: white = " + wah,  wHealthOK)

        Assert.assertTrue(file + ". Expected (absAbs) abs(black AbsHealth) - abs(white AbsHealth) to be about "
                + expectedAbsAbsHealthDifference + "\n but instead got ("+abah+" -"+ awah+") = "+ daah, absAbsDifOK)
        Assert.assertTrue(file + ". Expected (black AbsHealth) - (white AbsHealth) to be about "
                + expectedAbsHealthDifference + "\n but instead got ("+bah+" -"+ wah+") = "+ dah, absDifOK)
        Assert.assertTrue(file + ". Expected (rel) (black RelativeHealth) - (white RelativeHealth) to be about "
                + expectedRelHealthDifference + "\n but instead got  ("+brh+" -"+ wrh+") = "+ drh, relDifOK)
    }

private def errorMarker(OK: Boolean): String = if OK then "" else "      *Error*"

    private def verifyEyes(eyes: GoEyeSet, expectedEyes: Array[EyeInformation], black: Boolean): Unit = {
val color: String = if black then "black" else "white"
        if (expectedEyes.length != eyes.size) {
val sideColor: String = if black then "black" else "white"
            System.out.println("We expected " + sideColor +" eyes to be " + expectedEyes.mkString(", ") + ",\n")
            System.out.println("but we got "+ eyes)
        }
        Assert.assertEquals("unequal numbers of " + color + " eyes. Got " + eyes, expectedEyes.length, eyes.size)
        if (eyes.size > 0) {
            var i: Int = 0
            for (eye <- eyes.asScala) {
                var eyeInformation: EyeInformation = eye.getInformation
                Assert.assertEquals(color + "Eye " + i + " was not the type that we expected. ",
                         expectedEyes(i), eyeInformation)
                i += 1
            }
        }
    }

    private def approximatelyEqual(value: Double, expectedValue: Double, thresh: Double): Boolean = (Math.abs(value - expectedValue) < thresh)


}
