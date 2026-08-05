/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.board.path

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.twoplayer.blockade.BlockadeTestCase
import com.barrybecker4.game.twoplayer.blockade.board.{BlockadeBoard, BlockadeBoardPosition, BlockadeTstUtil}
import com.barrybecker4.game.twoplayer.blockade.board.move.wall.BlockadeWall
import org.junit.Assert.{assertFalse, assertTrue}
import org.junit.{Before, Test}

/** @author Barry Becker */
class PathTest extends BlockadeTestCase:

  private var path: Path = scala.compiletime.uninitialized
  private var board: BlockadeBoard = scala.compiletime.uninitialized

  @Before
  override def setUp(): Unit =
    super.setUp()
    board = new BlockadeBoard(7, 5)

  @Test
  def testUnblockedOneStepPath(): Unit =
    path = BlockadeTstUtil.createPath(new ByteLocation(2, 2), new ByteLocation(4, 2))
    assertNotBlocked(path)

  @Test
  def testUnblockedTwoStepPath(): Unit =
    path = BlockadeTstUtil.createPath(new ByteLocation(2, 2), new ByteLocation(4, 2), new ByteLocation(5, 3))
    assertNotBlocked(path)

  @Test
  def testBlockedOneStepPathHorzWall(): Unit =
    path = BlockadeTstUtil.createPath(new ByteLocation(2, 2), new ByteLocation(4, 2))
    board.addWall(new BlockadeWall(new BlockadeBoardPosition(2, 2), new BlockadeBoardPosition(2, 3)))
    assertBlocked(path)

  @Test
  def testBlockedTwoStepPathHorzWall(): Unit =
    path = BlockadeTstUtil.createPath(new ByteLocation(2, 2), new ByteLocation(4, 2), new ByteLocation(5, 3))
    board.addWall(new BlockadeWall(new BlockadeBoardPosition(3, 2), new BlockadeBoardPosition(3, 3)))
    assertBlocked(path)

  @Test
  def testBlockedTwoStepPathVertWallUpper(): Unit =
    path = BlockadeTstUtil.createPath(new ByteLocation(2, 2), new ByteLocation(4, 2), new ByteLocation(5, 3))
    board.addWall(new BlockadeWall(new BlockadeBoardPosition(4, 2), new BlockadeBoardPosition(4, 3)))
    assertBlocked(path)

  /** The wall does not block the path in this case */
  @Test
  def testNotBlockedTwoStepPathVertWallLower(): Unit =
    path = BlockadeTstUtil.createPath(new ByteLocation(2, 2), new ByteLocation(4, 2), new ByteLocation(5, 3))
    board.addWall(new BlockadeWall(new BlockadeBoardPosition(5, 2), new BlockadeBoardPosition(6, 2)))
    assertNotBlocked(path)

  @Test
  def testNotBlockedTwoStepPathHorzWallLower(): Unit =
    path = BlockadeTstUtil.createPath(new ByteLocation(2, 2), new ByteLocation(4, 2), new ByteLocation(5, 3))
    board.addWall(new BlockadeWall(new BlockadeBoardPosition(5, 2), new BlockadeBoardPosition(5, 3)))
    assertNotBlocked(path)

  @Test
  def testBlockedTwoStepPathHorzWallLower(): Unit =
    path = BlockadeTstUtil.createPath(new ByteLocation(2, 2), new ByteLocation(4, 2), new ByteLocation(5, 3))
    board.addWall(new BlockadeWall(new BlockadeBoardPosition(4, 2), new BlockadeBoardPosition(4, 3)))
    assertBlocked(path)

  private def assertBlocked(p: Path): Unit =
    assertTrue("Path unexpectedly not blocked", p.isBlocked(board))

  private def assertNotBlocked(p: Path): Unit =
    assertFalse("Path unexpectedly blocked", p.isBlocked(board))
