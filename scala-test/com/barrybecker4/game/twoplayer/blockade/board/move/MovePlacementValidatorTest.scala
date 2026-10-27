// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.move

import com.barrybecker4.game.twoplayer.blockade.BlockadeTestCase
import com.barrybecker4.game.twoplayer.blockade.board.{BlockadeBoard, BlockadeBoardPosition, BlockadeTstUtil}
import com.barrybecker4.game.twoplayer.blockade.board.move.wall.BlockadeWall
import org.junit.Assert.{assertFalse, assertTrue}
import org.junit.{Before, Test}

/** @author Barry Becker */
class MovePlacementValidatorTest extends BlockadeTestCase:

  private var validator: MovePlacementValidator = scala.compiletime.uninitialized
  private var board: BlockadeBoard = scala.compiletime.uninitialized

  private var topLeft: BlockadeBoardPosition = scala.compiletime.uninitialized
  private var topRight: BlockadeBoardPosition = scala.compiletime.uninitialized
  private var bottomLeft: BlockadeBoardPosition = scala.compiletime.uninitialized
  private var topLeftLeft: BlockadeBoardPosition = scala.compiletime.uninitialized
  private var topRightRight: BlockadeBoardPosition = scala.compiletime.uninitialized
  private var topTopLeft: BlockadeBoardPosition = scala.compiletime.uninitialized
  private var bottomBottomLeft: BlockadeBoardPosition = scala.compiletime.uninitialized

  @Before
  override def setUp(): Unit =
    super.setUp()
    board = new BlockadeBoard(7, 6)
    validator = new MovePlacementValidator(board)
    topLeft = board.getPosition(3, 3)
    topRight = board.getPosition(3, 4)
    bottomLeft = board.getPosition(4, 3)
    topTopLeft = board.getPosition(2, 3)
    topLeftLeft = board.getPosition(3, 2)
    topRightRight = board.getPosition(3, 5)
    bottomBottomLeft = board.getPosition(5, 3)

  @Test def testSouthEastMoveNotBlockedWhenNopWall(): Unit =
    verifyNotBlocked(BlockadeTstUtil.createMove(3, 3, 4, 4))

  @Test def testSouthEastMoveNotBlockedWhenBottomWall(): Unit =
    board.addWall(new BlockadeWall(bottomLeft, bottomBottomLeft))
    verifyNotBlocked(BlockadeTstUtil.createMove(3, 3, 4, 4))

  @Test def testSouthEastMoveNotBlockedWhenLeftWall(): Unit =
    board.addWall(new BlockadeWall(topLeftLeft, topLeft))
    verifyNotBlocked(BlockadeTstUtil.createMove(3, 3, 4, 4))

  @Test def testSouthEastMoveNotBlockedWhenRightWall(): Unit =
    board.addWall(new BlockadeWall(topRight, topRightRight))
    verifyNotBlocked(BlockadeTstUtil.createMove(3, 3, 4, 4))

  @Test def testSouthEastMoveNotBlockedWhenTopWall(): Unit =
    board.addWall(new BlockadeWall(topTopLeft, topLeft))
    verifyNotBlocked(BlockadeTstUtil.createMove(3, 4, 4, 3))

  @Test def testSouthEastMoveBlockedWhenVertWall(): Unit =
    board.addWall(new BlockadeWall(topLeft, bottomLeft))
    verifyBlocked(BlockadeTstUtil.createMove(3, 3, 4, 4))

  @Test def testSouthEastMoveBlockedWhenHorzWall(): Unit =
    board.addWall(new BlockadeWall(topLeft, topRight))
    verifyBlocked(BlockadeTstUtil.createMove(3, 3, 4, 4))

  @Test def testSouthWestMoveNotBlockedWhenBottomWall(): Unit =
    board.addWall(new BlockadeWall(bottomLeft, bottomBottomLeft))
    verifyNotBlocked(BlockadeTstUtil.createMove(3, 4, 4, 3))

  @Test def testSouthWestMoveNotBlockedWhenLeftWall(): Unit =
    board.addWall(new BlockadeWall(topLeftLeft, topLeft))
    verifyNotBlocked(BlockadeTstUtil.createMove(3, 4, 4, 3))

  @Test def testSouthWestMoveNotBlockedWhenRightWall(): Unit =
    board.addWall(new BlockadeWall(topRight, topRightRight))
    verifyNotBlocked(BlockadeTstUtil.createMove(3, 4, 4, 3))

  @Test def testSouthWestMoveNotBlockedWhenTopWall(): Unit =
    board.addWall(new BlockadeWall(topTopLeft, topLeft))
    verifyNotBlocked(BlockadeTstUtil.createMove(3, 4, 4, 3))

  @Test def testSouthWestMoveBlockedWhenVertWall(): Unit =
    board.addWall(new BlockadeWall(topLeft, bottomLeft))
    verifyBlocked(BlockadeTstUtil.createMove(3, 4, 4, 3))

  @Test def testSouthWestMoveBlockedWhenHorzWall(): Unit =
    board.addWall(new BlockadeWall(topLeft, topRight))
    verifyBlocked(BlockadeTstUtil.createMove(3, 4, 4, 3))

  @Test def testNorthWestMoveNotBlockedWhenBottomWall(): Unit =
    board.addWall(new BlockadeWall(bottomLeft, bottomBottomLeft))
    verifyNotBlocked(BlockadeTstUtil.createMove(4, 4, 3, 3))

  @Test def testNorthWestMoveNotBlockedWhenLeftWall(): Unit =
    board.addWall(new BlockadeWall(topLeftLeft, topLeft))
    verifyNotBlocked(BlockadeTstUtil.createMove(4, 4, 3, 3))

  @Test def testNorthWestMoveNotBlockedWhenRightWall(): Unit =
    board.addWall(new BlockadeWall(topRight, topRightRight))
    verifyNotBlocked(BlockadeTstUtil.createMove(4, 4, 3, 3))

  @Test def testNorthWestMoveNotBlockedWhenTopWall(): Unit =
    board.addWall(new BlockadeWall(topTopLeft, topLeft))
    verifyNotBlocked(BlockadeTstUtil.createMove(4, 4, 3, 3))

  @Test def testNorthWestMoveBlockedWhenVertWall(): Unit =
    board.addWall(new BlockadeWall(topLeft, bottomLeft))
    verifyBlocked(BlockadeTstUtil.createMove(4, 4, 3, 3))

  @Test def testNorthWestMoveBlockedWhenHorzWall(): Unit =
    board.addWall(new BlockadeWall(topLeft, topRight))
    verifyBlocked(BlockadeTstUtil.createMove(4, 4, 3, 3))

  @Test def testNorthEastMoveNotBlockedWhenBottomWall(): Unit =
    board.addWall(new BlockadeWall(bottomLeft, bottomBottomLeft))
    verifyNotBlocked(BlockadeTstUtil.createMove(4, 3, 3, 4))

  @Test def testNorthEastMoveNotBlockedWhenLeftWall(): Unit =
    board.addWall(new BlockadeWall(topLeftLeft, topLeft))
    verifyNotBlocked(BlockadeTstUtil.createMove(4, 3, 3, 4))

  @Test def testNorthEastMoveNotBlockedWhenRightWall(): Unit =
    board.addWall(new BlockadeWall(topRight, topRightRight))
    verifyNotBlocked(BlockadeTstUtil.createMove(4, 3, 3, 4))

  @Test def testNorthEastMoveNotBlockedWhenTopWall(): Unit =
    board.addWall(new BlockadeWall(topTopLeft, topLeft))
    verifyNotBlocked(BlockadeTstUtil.createMove(4, 3, 3, 4))

  @Test def testNorthEastMoveBlockedWhenVertWall(): Unit =
    board.addWall(new BlockadeWall(topLeft, bottomLeft))
    verifyBlocked(BlockadeTstUtil.createMove(4, 3, 3, 4))

  @Test def testNorthEastMoveBlockedWhenHorzWall(): Unit =
    board.addWall(new BlockadeWall(topLeft, topRight))
    verifyBlocked(BlockadeTstUtil.createMove(4, 3, 3, 4))

  private def verifyNotBlocked(move: BlockadeMove): Unit =
    assertFalse("Move " + move + " unexpectedly blocked", validator.isMoveBlocked(move))

  private def verifyBlocked(move: BlockadeMove): Unit =
    assertTrue("Move " + move + " unexpectedly not blocked", validator.isMoveBlocked(move))
