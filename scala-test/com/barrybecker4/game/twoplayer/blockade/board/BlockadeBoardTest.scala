/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.board

import com.barrybecker4.common.geometry.{ByteLocation, Location}
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.board.{BoardPosition, GamePiece}
import com.barrybecker4.game.twoplayer.blockade.BlockadeTestCase
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove
import com.barrybecker4.game.twoplayer.blockade.board.move.wall.BlockadeWall
import com.barrybecker4.game.twoplayer.blockade.board.path.{Path, PathLengths, PathList, PlayerPathLengths}
import org.junit.Assert.{assertEquals, assertTrue}
import org.junit.Test

import java.util as ju

/** @author Barry Becker */
class BlockadeBoardTest extends BlockadeTestCase:

  @Test
  def testPositionStates(): Unit =
    val p = new BlockadeBoardPosition(1, 1)
    assertTrue("no piece or walls", p.getStateIndex == 0)
    p.setPiece(new GamePiece(true))
    val wallPositions = new ju.LinkedHashSet[BlockadeBoardPosition](2)
    assertTrue("p1 piece and no walls", p.getStateIndex == 1)
    wallPositions.add(new BlockadeBoardPosition(10, 11))
    wallPositions.add(new BlockadeBoardPosition(10, 12))
    p.setEastWall(new BlockadeWall(wallPositions))
    assertTrue("p1 piece and east wall", p.getStateIndex == 3)
    wallPositions.clear()
    wallPositions.add(new BlockadeBoardPosition(10, 12))
    wallPositions.add(new BlockadeBoardPosition(11, 12))
    p.setSouthWall(new BlockadeWall(wallPositions))
    assertTrue("p1 piece and both walls", p.getStateIndex == 7)
    p.setPiece(new GamePiece(false))
    assertTrue("p2 piece and both walls", p.getStateIndex == 11)

  private val P1_NUM_MAP: ju.Map[Location, Integer] =
    val m = new ju.HashMap[Location, Integer]()
    m.put(new ByteLocation(3, 4), 9)
    m.put(new ByteLocation(3, 4), 9)
    m.put(new ByteLocation(3, 8), 9)
    m.put(new ByteLocation(4, 3), 9)
    m.put(new ByteLocation(4, 5), 9)
    m.put(new ByteLocation(4, 9), 9)
    m.put(new ByteLocation(5, 4), 9)
    m.put(new ByteLocation(5, 3), 9)
    m.put(new ByteLocation(5, 7), 6)
    m.put(new ByteLocation(5, 8), 7)
    m.put(new ByteLocation(6, 7), 6)
    m.put(new ByteLocation(6, 8), 6)
    m.put(new ByteLocation(5, 5), 9)
    m.put(new ByteLocation(7, 3), 9)
    m.put(new ByteLocation(7, 5), 9)
    m.put(new ByteLocation(7, 8), 7)
    m.put(new ByteLocation(7, 9), 9)
    m.put(new ByteLocation(8, 3), 9)
    m.put(new ByteLocation(8, 4), 7)
    m.put(new ByteLocation(8, 5), 7)
    m.put(new ByteLocation(8, 6), 6)
    m.put(new ByteLocation(8, 7), 7)
    m.put(new ByteLocation(8, 8), 7)
    m.put(new ByteLocation(8, 9), 9)
    m.put(new ByteLocation(9, 4), 6)
    m.put(new ByteLocation(9, 5), 5)
    m.put(new ByteLocation(9, 6), 6)
    m.put(new ByteLocation(9, 7), 5)
    m.put(new ByteLocation(9, 8), 6)
    m.put(new ByteLocation(10, 3), 8)
    m.put(new ByteLocation(10, 4), 6)
    m.put(new ByteLocation(10, 5), 6)
    m.put(new ByteLocation(10, 7), 7)
    m.put(new ByteLocation(10, 8), 6)
    m.put(new ByteLocation(10, 9), 9)
    m.put(new ByteLocation(10, 10), 7)
    m.put(new ByteLocation(11, 5), 7)
    m.put(new ByteLocation(11, 7), 6)
    m.put(new ByteLocation(11, 8), 6)
    m.put(new ByteLocation(12, 7), 7)
    m.put(new ByteLocation(12, 8), 7)
    m.put(new ByteLocation(13, 10), 6)
    m

  @Test
  def testPossibleMoveList(): Unit =
    restore("whitebox/moveList1")
    val board = controller_.getBoard.asInstanceOf[BlockadeBoard]
    val numRows = board.getNumRows
    val numCols = board.getNumCols
    for row <- 1 to numRows do
      for col <- 1 to numCols do
        val position = board.getPosition(row, col)
        val list1 = board.getPossibleMoveList(position, false)
        if board.isOnEdge(position) then
          if board.isInCorner(position) then
            verifyMoves(position, list1, 3, P1_NUM_MAP)
          else if row == 2 || col == 2 || row == numRows - 1 || col == numCols - 1 then
            verifyMoves(position, list1, 4, P1_NUM_MAP)
          else
            verifyMoves(position, list1, 5, P1_NUM_MAP)
        else if row == 2 || col == 2 || row == numRows - 1 || col == numCols - 1 then
          if row == col || (row == 2 && col == numCols - 1) || (col == 2 && row == numRows - 1) then
            verifyMoves(position, list1, 6, P1_NUM_MAP)
          else
            verifyMoves(position, list1, 7, P1_NUM_MAP)
        else
          verifyMoves(position, list1, 8, P1_NUM_MAP)

  private def verifyMoves(
      position: BoardPosition,
      player1Moves: ju.List[?],
      expectedNumMovesIn: Int,
      p1NumMap: ju.Map[Location, Integer]
  ): Unit =
    var expectedNumMoves = expectedNumMovesIn
    if p1NumMap.containsKey(position.getLocation) then
      expectedNumMoves = p1NumMap.get(position.getLocation)
    assertTrue(
      "Expected " + expectedNumMoves + " moves for player1, but got " + player1Moves.size + ":" + player1Moves,
      player1Moves.size == expectedNumMoves
    )

  private def createMove(r1: Int, c1: Int, r2: Int, c2: Int, piece: GamePiece): BlockadeMove =
    new BlockadeMove(new ByteLocation(r1, c1), new ByteLocation(r2, c2), 0, piece, null)

  private val moves1: Array[Array[BlockadeMove]] = Array(
      Array(
        createMove(6,4, 8,4, new GamePiece(false)),
        createMove(8,4, 10,4,  null),
        createMove(10,4,  11,5, null),
        createMove(11,5,  11,7, null),
        createMove(11,7,  11,8, null)
      ),
      Array(
        createMove(6,4,  8,4,  new GamePiece(false)),
        createMove(8,4,  10,4, null),
        createMove(10,4,  10,2, null),
        createMove(10,2,  11,3, null),
        createMove(11,3,  11,4, null)
      ),
      Array(
        createMove(8,8,  10,8,  new GamePiece(false)),
        createMove(10,8,  11,9, null),
        createMove(11,9,  11,8, null)
      ),
      Array(
        createMove(8,8,  10,8, new GamePiece(false)),
        createMove(10,8,  10,6, null),
        createMove(10,6,  12,6, null),
        createMove(12,6,  13,5, null),
        createMove(13,5,  12,4, null),
        createMove(12,4,  11,4, null)
      )
    )

  private val moves2: Array[Array[BlockadeMove]] = Array(
      Array(
        createMove(8,3,  6,3, new GamePiece(true)),
        createMove(6,3,  5,4, null),
        createMove(5,4,  4,4, null)
      ),
      Array(
        createMove(8,3,  6,3, new GamePiece(true)),
        createMove(6,3,  5,4, null),
        createMove(5,4,  4,4, null),
        createMove(4,4,  4,6, null),
        createMove(4,6,  4,8, null)
      ),
      Array(
        createMove(9,8,  8,9, new GamePiece(true)),
        createMove(8,9,  6,9, null),
        createMove(6,9,  4,9, null),
        createMove(4,9,  4,8, null)
      ),
      Array(
        createMove(9,8,  7,8, new GamePiece(true)),
        createMove(7,8,  7,6, null),
        createMove(7,6,  7,4, null),
        createMove(7,4,  5,4, null),
        createMove(5,4,  4,4, null)
      )
    )

  private val EXPECTED_P1_PATHS: Array[Path] = Array(
    new Path(moves1(0)), new Path(moves1(1)), new Path(moves1(2)), new Path(moves1(3))
  )

  private val EXPECTED_P2_PATHS: Array[Path] = Array(
    new Path(moves2(0)), new Path(moves2(1)), new Path(moves2(2)), new Path(moves2(3))
  )

  @Test
  def testFindOpponentShortestPaths(): Unit =
    restore("whitebox/shortestPaths1")
    val board = controller_.getBoard.asInstanceOf[BlockadeBoard]
    val p1Paths = board.findAllOpponentShortestPaths(true)
    val p2Paths = board.findAllOpponentShortestPaths(false)
    assertEquals("Unexpected number of paths", 4, p1Paths.size)
    assertEquals("Unexpected number of paths", 4, p2Paths.size)
    val p1PathDiff = pathListDiff(EXPECTED_P1_PATHS, p1Paths)
    val p2PathDiff = pathListDiff(EXPECTED_P2_PATHS, p2Paths)
    assertTrue("p1Path difference=\n" + p1PathDiff, p1PathDiff.length == 0)
    assertTrue("p2Path difference=\n" + p2PathDiff, p2PathDiff.length == 0)

  private def pathListDiff(expectedPaths: Array[Path], actualPaths: PathList): String =
    var pathListsDifferent = false
    val diffs = new StringBuilder("Paths compared: \n")
    for i <- expectedPaths.indices do
      val ep = expectedPaths(i)
      val ap = actualPaths.get(i)
      val diffMarker = if ep.equals(ap) then "" else "***"
      diffs.append("expected:  ").append(ep).append("actual  :  ").append(ap)
      if !ep.equals(ap) then
        diffs.append("   ").append(diffMarker).append(" \n")
        pathListsDifferent = true
    if pathListsDifferent then diffs.toString else ""

  @Test
  def testShortestPathLength(): Unit =
    restore("whitebox/noMoves2")
    val board = controller_.getBoard.asInstanceOf[BlockadeBoard]
    val pLengths = board.findPlayerPathLengths()
    GameContext.log(1, pLengths.toString)
    val expectedP1Lengths = new PathLengths(4, 6, 12)
    val expectedP2Lengths = new PathLengths(3, 3, 10)
    val actualP1Lengths = pLengths.getPathLengthsForPlayer(true)
    val actualP2Lengths = pLengths.getPathLengthsForPlayer(false)
    assertTrue("Unexpected Player1 Path lengths - " + actualP1Lengths, expectedP1Lengths.equals(actualP1Lengths))
    assertTrue("Unexpected Player2 Path lengths - " + actualP2Lengths, expectedP2Lengths.equals(actualP2Lengths))

  @Test
  def testShortestPaths2(): Unit =
    restore("whitebox/noMoves2")
    val board = controller_.getBoard.asInstanceOf[BlockadeBoard]
    val piece1 = new GamePiece(true)
    val piece2 = new GamePiece(false)
    val wall1 = new BlockadeWall(board.getPosition(8, 10), board.getPosition(9, 10))
    val wall2 = new BlockadeWall(board.getPosition(12, 6), board.getPosition(12, 7))
    val move1 = BlockadeMove.createMove(new ByteLocation(8, 11), new ByteLocation(6, 11), 1, piece2, wall2)
    val move2 = BlockadeMove.createMove(new ByteLocation(12, 6), new ByteLocation(10, 6), 1, piece1, wall1)
    controller_.makeMove(move1)
    controller_.makeMove(move2)
    val pLengths = board.findPlayerPathLengths()
    GameContext.log(1, pLengths.toString)
    val expectedP1Lengths = new PathLengths(7, 9, 12)
    val expectedP2Lengths = new PathLengths(4, 6, 10)
    val actualP1Lengths = pLengths.getPathLengthsForPlayer(true)
    val actualP2Lengths = pLengths.getPathLengthsForPlayer(false)
    assertTrue("Unexpected Player1 Path lengths - " + actualP1Lengths, expectedP1Lengths.equals(actualP1Lengths))
    assertTrue("Unexpected Player2 Path lengths - " + actualP2Lengths, expectedP2Lengths.equals(actualP2Lengths))

  private val EXPECTED_PATHS_LENGTHS: Array[Int] = Array(0, 11, 12)

  @Test
  def testFindShortestPaths(): Unit =
    restore("whitebox/shortestPathsCheck")
    val board = controller_.getBoard.asInstanceOf[BlockadeBoard]
    val pos1 = board.getPosition(2, 2)
    val pos2 = board.getPosition(5, 2)
    val pLengths1 = board.findShortestPaths(pos1)
    val pLengths2 = board.findShortestPaths(pos2)
    val size = EXPECTED_PATHS_LENGTHS.length
    val lengths = new Array[Int](size)
    assertTrue("Unexpected number of pLengths1=" + pLengths1.size, pLengths1.size == 1)
    assertTrue("Unexpected number of pLengths2=" + pLengths2.size, pLengths2.size == 2)
    lengths(0) = pLengths1.get(0).getLength
    lengths(1) = pLengths2.get(0).getLength
    lengths(2) = pLengths2.get(1).getLength
    for i <- 0 until size do
      assertTrue(
        "Expected len " + EXPECTED_PATHS_LENGTHS(i) + " but got  " + lengths(i),
        lengths(i) == EXPECTED_PATHS_LENGTHS(i)
      )
