// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.move.wall

import com.barrybecker4.game.twoplayer.blockade.BlockadeTestCase
import com.barrybecker4.game.twoplayer.blockade.board.{BlockadeBoard, BlockadeBoardPosition}
import org.junit.Assert.assertEquals
import org.junit.{Before, Test}

/** @author Barry Becker */
class DiagonalWallCheckerTest extends BlockadeTestCase:

  private var checker: DiagonalWallChecker = scala.compiletime.uninitialized
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
    board = new BlockadeBoard(7, 5)
    checker = new DiagonalWallChecker(board)
    topLeft = board.getPosition(2, 3)
    topRight = board.getPosition(2, 4)
    bottomLeft = board.getPosition(3, 3)
    topTopLeft = board.getPosition(1, 3)
    topLeftLeft = board.getPosition(2, 2)
    topRightRight = board.getPosition(2, 5)
    bottomBottomLeft = board.getPosition(4, 3)

  @Test
  def testCheckWhenNoWallsOnBoard(): Unit =
    val expWalls = new BlockadeWallList()
    expWalls.add(new BlockadeWall(topLeft, topRight))
    expWalls.add(new BlockadeWall(topLeft, bottomLeft))
    verifyWalls(expWalls)

  @Test
  def testCheckWhenLeftWallOnBoard(): Unit =
    board.addWall(new BlockadeWall(topLeftLeft, topLeft))
    val expWalls = new BlockadeWallList()
    expWalls.add(new BlockadeWall(topLeft, bottomLeft))
    expWalls.add(new BlockadeWall(topRight, topRightRight))
    verifyWalls(expWalls)

  @Test
  def testCheckWhenRightWallOnBoard(): Unit =
    board.addWall(new BlockadeWall(topRight, topRightRight))
    val expWalls = new BlockadeWallList()
    expWalls.add(new BlockadeWall(topLeft, bottomLeft))
    expWalls.add(new BlockadeWall(topLeftLeft, topRight))
    verifyWalls(expWalls)

  @Test
  def testCheckWhenTopWallOnBoard(): Unit =
    board.addWall(new BlockadeWall(topTopLeft, topLeft))
    val expWalls = new BlockadeWallList()
    expWalls.add(new BlockadeWall(topLeft, topRight))
    expWalls.add(new BlockadeWall(bottomLeft, bottomBottomLeft))
    verifyWalls(expWalls)

  @Test
  def testCheckWhenBottomWallOnBoard(): Unit =
    board.addWall(new BlockadeWall(bottomLeft, bottomBottomLeft))
    val expWalls = new BlockadeWallList()
    expWalls.add(new BlockadeWall(topLeft, topRight))
    expWalls.add(new BlockadeWall(topTopLeft, topLeft))
    verifyWalls(expWalls)

  @Test
  def testCheckWhenLeftAndBottomWalls(): Unit =
    board.addWall(new BlockadeWall(topLeftLeft, topLeft))
    board.addWall(new BlockadeWall(bottomLeft, bottomBottomLeft))
    val expWalls = new BlockadeWallList()
    expWalls.add(new BlockadeWall(topRight, topRightRight))
    expWalls.add(new BlockadeWall(topTopLeft, topLeft))
    verifyWalls(expWalls)

  @Test
  def testCheckWhenLeftAndTopWalls(): Unit =
    board.addWall(new BlockadeWall(topLeftLeft, topLeft))
    board.addWall(new BlockadeWall(topTopLeft, topLeft))
    val expWalls = new BlockadeWallList()
    expWalls.add(new BlockadeWall(topRight, topRightRight))
    expWalls.add(new BlockadeWall(bottomLeft, bottomBottomLeft))
    verifyWalls(expWalls)

  @Test
  def testCheckWhenRightAndTopWalls(): Unit =
    board.addWall(new BlockadeWall(topTopLeft, topLeft))
    board.addWall(new BlockadeWall(topRight, topRightRight))
    val expWalls = new BlockadeWallList()
    expWalls.add(new BlockadeWall(topLeftLeft, topLeft))
    expWalls.add(new BlockadeWall(bottomLeft, bottomBottomLeft))
    verifyWalls(expWalls)

  @Test
  def testCheckWhenRightAndBottomWalls(): Unit =
    board.addWall(new BlockadeWall(topRight, topRightRight))
    board.addWall(new BlockadeWall(bottomLeft, bottomBottomLeft))
    val expWalls = new BlockadeWallList()
    expWalls.add(new BlockadeWall(topTopLeft, topLeft))
    expWalls.add(new BlockadeWall(topLeftLeft, topLeft))
    verifyWalls(expWalls)

  private def verifyWalls(expWalls: BlockadeWallList): Unit =
    val walls = checker.checkWalls(topLeft, topRight, bottomLeft)
    assertEquals("Unexpected walls", expWalls, walls)
