// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.go.board.analysis.group.eye

import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.{E1Information, EyeStatus, EyeType, TerritorialEyeInformation}
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzerMap
import com.barrybecker4.game.twoplayer.go.board.elements.eye.{GoEyeSet, IGoEye}
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup
import org.junit.Assert.{assertEquals, assertTrue}
import org.junit.Test
import scala.jdk.CollectionConverters._

/**
 * Verify that we come up with reasonable eye potential values (likelihood of making eyes in the group).
 * @author Barry Becker
 */
class TestEyeSpaceAnalyzer extends GoTestCase:

  private val PREFIX = "board/analysis/group/eye/eyespace/"

  @Test
  def testEyeSpace_SingleIsolatedStone(): Unit =
    restoreGame("single_isolated_stone")
    verifyBlackEyes(createEyeSet())
    verifyWhiteEyes(createEyeSet())

  @Test
  def testEyeSpace_SingleEye(): Unit =
    restoreGame("single_eye")
    val blackEye: IGoEye = new StubGoEye(true, EyeStatus.NAKADE, new E1Information(), 0, 0, false, 1)
    val whiteEye: IGoEye = new StubGoEye(false, EyeStatus.NAKADE, new E1Information(), 0, 1, false, 1)
    verifyBlackEyes(createEyeSet(blackEye))
    verifyWhiteEyes(createEyeSet(whiteEye))

  /**
   * For black eye we have:
   *   C
   *   CCE
   * and for white we have
   *   ECC
   * where C's are counted as being edges as well as corner.
   */
  @Test
  def testEyeSpace_EyesInCorner(): Unit =
    restoreGame("eyes_in_corner")
    val blackEye: IGoEye = new StubGoEye(true, EyeStatus.ALIVE, EyeType.E6.getInformation("E222233"), 3, 4, false, 6)
    val whiteEye: IGoEye = new StubGoEye(false, EyeStatus.UNSETTLED, EyeType.E3.getInformation("E112"), 2, 3, false, 3)
    verifyBlackEyes(createEyeSet(blackEye))
    verifyWhiteEyes(createEyeSet(whiteEye))

  @Test
  def testEyeSpace_EyesInCornerComplex(): Unit =
    restoreGame("eyes_in_corner_complex")
    val blackEye: IGoEye = new StubGoEye(true, EyeStatus.ALIVE, EyeType.E7.getInformation("E1122222"), 3, 6, false, 7)
    val whiteEye: IGoEye = new StubGoEye(false, EyeStatus.ALIVE, new TerritorialEyeInformation(), 3, 5, false, 8)
    verifyBlackEyes(createEyeSet(blackEye))
    verifyWhiteEyes(createEyeSet(whiteEye))

  @Test
  def testEyeSpace_TwoEyesUnconditional(): Unit =
    restoreGame("two_unconditional_eyes")
    val blackEye1: IGoEye = new StubGoEye(true, EyeStatus.NAKADE, EyeType.E1.getInformation("E1"), 0, 0, false, 1)
    val blackEye2: IGoEye = new StubGoEye(true, EyeStatus.NAKADE, EyeType.E1.getInformation("E1"), 1, 1, false, 1)
    val whiteEye1: IGoEye = new StubGoEye(false, EyeStatus.NAKADE, EyeType.E1.getInformation("E1"), 0, 0, false, 1)
    val whiteEye2: IGoEye = new StubGoEye(false, EyeStatus.NAKADE, EyeType.E1.getInformation("E1"), 0, 0, false, 1)
    verifyBlackEyes(createEyeSet(blackEye1, blackEye2))
    verifyWhiteEyes(createEyeSet(whiteEye1, whiteEye2))

  @Test
  def testEyeSpace_TunnelButNoEye(): Unit =
    restoreGame("tunnels_no_eyes")
    verifyBlackEyes(createEyeSet())
    verifyWhiteEyes(createEyeSet())

  @Test
  def testEyeSpace_tunnelButNoEye2(): Unit =
    restoreGame("tunnels_no_eyes2")
    verifyBlackEyes(createEyeSet())
    verifyWhiteEyes(createEyeSet())

  @Test
  def testEyeSpace_tunnelButNoEye3(): Unit =
    restoreGame("tunnels_no_eyes2")
    verifyBlackEyes(createEyeSet())
    verifyWhiteEyes(createEyeSet())

  @Test
  def testEyeSpace_tunnelsWithEyes(): Unit =
    restoreGame("tunnels_with_eyes")
    // This eye (if it is one) does not get noticed because it is a really odd edge case.
    val whiteEye1: IGoEye = new StubGoEye(false, EyeStatus.ALIVE, new TerritorialEyeInformation(), 3, 5, false, 9)
    val whiteEye2: IGoEye = new StubGoEye(false, EyeStatus.ALIVE, EyeType.E5.getInformation("E11222"), 0, 1, false, 5)
    verifyBlackEyes(createEyeSet())
    verifyWhiteEyes(createEyeSet(whiteEye1, whiteEye2))

  @Test
  def testEyeSpace_tunnelsWithEyes2(): Unit =
    restoreGame("tunnels_with_eyes2")
    val blackEye: IGoEye = new StubGoEye(true, EyeStatus.ALIVE, new TerritorialEyeInformation(), 4, 14, false, 14)
    val whiteEye1: IGoEye = new StubGoEye(false, EyeStatus.ALIVE, EyeType.E7.getInformation("E1122222"), 3, 5, false, 7)
    val whiteEye2: IGoEye = new StubGoEye(false, EyeStatus.NAKADE, new E1Information(), 0, 0, false, 1)
    val whiteEye3: IGoEye = new StubGoEye(false, EyeStatus.ALIVE, EyeType.E5.getInformation("E11222"), 0, 1, false, 5)
    verifyBlackEyes(createEyeSet(blackEye))
    verifyWhiteEyes(createEyeSet(whiteEye1, whiteEye2, whiteEye3))

  private def restoreGame(file: String): Unit =
    restore(PREFIX + file)

  private def verifyBlackEyes(expectedEyes: GoEyeSet): Unit =
    verifyEyes(true, expectedEyes)

  private def verifyWhiteEyes(expectedEyes: GoEyeSet): Unit =
    verifyEyes(false, expectedEyes)

  private def createEyeSet(eyes: IGoEye*): GoEyeSet =
    val eyeList = new GoEyeSet()
    eyeList.addAll(java.util.Arrays.asList(eyes*))
    eyeList

  /** Use EyeSpaceAnalyzer to find eyes and match against the expected set of eyes. */
  private def verifyEyes(forBlackGroup: Boolean, expectedEyes: GoEyeSet): Unit =
    val group: IGoGroup = getBiggestGroup(forBlackGroup)
    val analyzer = new EyeSpaceAnalyzer(group, new GroupAnalyzerMap())
    analyzer.setBoard(getBoard)
    val eyes = analyzer.determineEyes()
    val matched = compareEyeSets(expectedEyes, eyes)
    if !matched then
      System.err.println("we expected: \n" + expectedEyes)
      System.err.println("but got: \n" + eyes)
    assertTrue("The actual eyes did not match the expected.", matched)

  /** @return true if both sets of eyes are the same */
  private def compareEyeSets(expEyes: GoEyeSet, actEyes: GoEyeSet): Boolean =
    assertEquals("Unexpected number of eyes in group.", expEyes.size, actEyes.size)
    var allFound = true
    for actEye <- actEyes.asScala do
      var found = false
      for eye <- expEyes.asScala do
        val expEye = eye.asInstanceOf[StubGoEye]
        if expEye.isMatch(actEye) then found = true
      if !found then allFound = false
    allFound
