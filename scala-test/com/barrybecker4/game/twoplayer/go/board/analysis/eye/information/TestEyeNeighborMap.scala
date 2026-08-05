/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information

import com.barrybecker4.common.geometry.{ByteLocation, Location}
import com.barrybecker4.game.twoplayer.go.board.GoBoardConfigurator
import com.barrybecker4.game.twoplayer.go.board.elements.eye.IGoEye
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition
import org.junit.Assert.{assertEquals, assertFalse, assertTrue, fail}
import org.junit.Test
import java.util.List
import scala.jdk.CollectionConverters._

/**
 * @author Barry Becker
 */
class TestEyeNeighborMap:

  private var nbrMap: EyeNeighborMap = scala.compiletime.uninitialized

  @Test
  def testSingleEyeSpace(): Unit =
    val eye: IGoEye =
      new StubGoEye(GoBoardConfigurator.createPositionList(Array[Location](new ByteLocation(2, 2))))
    nbrMap = new EyeNeighborMap(eye)
    assertEquals("unexpected size", 1, nbrMap.keySet().size)

  @Test
  def testTwoSpaceEye(): Unit =
    val positions: Array[Location] = Array(new ByteLocation(2, 2), new ByteLocation(2, 3))
    val eye: IGoEye = new StubGoEye(GoBoardConfigurator.createPositionList(positions))
    nbrMap = new EyeNeighborMap(eye)
    assertEquals("unexpected size", 2, nbrMap.keySet().size)

  /** Negative case */
  @Test
  def testDisjointEye(): Unit =
    val positions: Array[Location] = Array(new ByteLocation(2, 2), new ByteLocation(3, 3))
    val eye: IGoEye = new StubGoEye(GoBoardConfigurator.createPositionList(positions))
    try
      nbrMap = new EyeNeighborMap(eye)
      fail()
    catch
      case e: IllegalArgumentException => // success

  @Test
  def testThreeSpaceEye(): Unit =
    val positions: Array[Location] =
      Array(new ByteLocation(2, 2), new ByteLocation(2, 3), new ByteLocation(3, 3))
    val spaces: List[GoBoardPosition] = GoBoardConfigurator.createPositionList(positions)
    val eye: IGoEye = new StubGoEye(spaces)
    nbrMap = new EyeNeighborMap(eye)
    assertEquals("unexpected size", 3, nbrMap.keySet().size)
    verifyNumEyeNbrs(Array(1, 2, 1), spaces)

  @Test
  def testRabbittySixEye(): Unit =
    val spaces: List[GoBoardPosition] = GoBoardConfigurator.createPositionList(TestEyeNeighborMap.RABBITY_SIX)
    val eye: IGoEye = new StubGoEye(spaces)
    nbrMap = new EyeNeighborMap(eye)
    assertEquals("unexpected size", 6, nbrMap.keySet().size)
    verifyNumEyeNbrs(Array(2, 2, 1, 4, 2, 1), spaces)

  @Test
  def testRabbittySixSpecialPoints(): Unit =
    val spaces: List[GoBoardPosition] = GoBoardConfigurator.createPositionList(TestEyeNeighborMap.RABBITY_SIX)
    val eye: IGoEye = new StubGoEye(spaces)
    nbrMap = new EyeNeighborMap(eye)
    // we expect 3,3 and 4,2 to be special points.
    val specialPoints = Array(4.06f, 2.04f)
    assertTrue(nbrMap.isSpecialPoint(spaces.get(0), specialPoints))
    assertFalse(nbrMap.isSpecialPoint(spaces.get(1), specialPoints))
    assertFalse(nbrMap.isSpecialPoint(spaces.get(2), specialPoints))
    assertTrue(nbrMap.isSpecialPoint(spaces.get(3), specialPoints))
    verifyNumEyeNbrs(Array(2, 2, 1, 4, 2, 1), spaces)

  @Test
  def testBlockOfSixEye(): Unit =
    val spaces: List[GoBoardPosition] = GoBoardConfigurator.createPositionList(TestEyeNeighborMap.BLOCK_OF_SIX)
    val eye: IGoEye = new StubGoEye(spaces)
    nbrMap = new EyeNeighborMap(eye)
    assertEquals("unexpected size", 6, nbrMap.keySet().size)
    verifyNumEyeNbrs(Array(2, 3, 2, 2, 3, 2), spaces)

  private def verifyNumEyeNbrs(expectedNumNbrs: Array[Int], spaces: List[GoBoardPosition]): Unit =
    for i <- expectedNumNbrs.indices do
      assertEquals(
        "Unexpected number of neighbors for position " + i,
        expectedNumNbrs(i),
        nbrMap.getNumEyeNeighbors(spaces.get(i))
      )


object TestEyeNeighborMap:
  private val RABBITY_SIX: Array[Location] = Array(
    new ByteLocation(4, 2), new ByteLocation(3, 2), new ByteLocation(2, 3),
    new ByteLocation(3, 3), new ByteLocation(4, 3), new ByteLocation(3, 4)
  )
  private val BLOCK_OF_SIX: Array[Location] = Array(
    new ByteLocation(13, 6), new ByteLocation(12, 5), new ByteLocation(12, 4),
    new ByteLocation(13, 4), new ByteLocation(13, 5), new ByteLocation(12, 6)
  )
