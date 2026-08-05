// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.analysis

import com.barrybecker4.common.geometry.{ByteLocation, Location}
import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.twoplayer.blockade.BlockadeTestCase
import com.barrybecker4.game.twoplayer.blockade.board.{BlockadeBoard, BlockadeTstUtil}
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove
import com.barrybecker4.game.twoplayer.blockade.board.path.{Path, PathList}
import org.junit.Assert.assertEquals
import org.junit.Test

/** @author Barry Becker */
class ShortestPathFinderTest extends BlockadeTestCase:

  @Test
  def testFindShortestPathsForSimple5x7(): Unit =
    val expPaths = new PathList(Array(
      new Path(Array(
        BlockadeTstUtil.createMove(2, 2, 4, 2, 0, new GamePiece(false), null),
        BlockadeTstUtil.createMove(4, 2, 6, 2, 0, null, null)
      )),
      new Path(Array(
        BlockadeTstUtil.createMove(2, 2, 4, 2, 0, new GamePiece(false), null),
        BlockadeTstUtil.createMove(4, 2, 6, 2, 0, null, null),
        BlockadeTstUtil.createMove(6, 2, 6, 4, 0, new GamePiece(true), null)
      ))
    ))
    verifyShortestPaths("board/analysis/initial5x7", new ByteLocation(2, 2), expPaths)

  @Test
  def testFindShortestPathsWhenWAllsPresentFor5x7(): Unit =
    val expPaths = new PathList(Array(
      new Path(Array(
        BlockadeTstUtil.createMove(2, 2, 4, 2, 0, new GamePiece(false), null),
        BlockadeTstUtil.createMove(4, 2, 6, 2, 0, null, null)
      )),
      new Path(Array(
        BlockadeTstUtil.createMove(2, 2, 4, 2, 0, new GamePiece(false), null),
        BlockadeTstUtil.createMove(4, 2, 6, 2, 0, null, null),
        BlockadeTstUtil.createMove(6, 2, 6, 4, 0, new GamePiece(true), null)
      ))
    ))
    verifyShortestPaths("board/analysis/initial5x7", new ByteLocation(2, 2), expPaths)

  private def verifyShortestPaths(filename: String, loc: Location, expPaths: PathList): Unit =
    restore(filename)
    val board = controller_.getBoard.asInstanceOf[BlockadeBoard]
    val pathFinder = new ShortestPathFinder(board)
    val paths = pathFinder.findShortestPaths(board.getPosition(loc))
    assertEquals(
      "Unexpected paths. Got\n" + BlockadeTstUtil.getConstructorString(paths),
      expPaths,
      paths
    )
