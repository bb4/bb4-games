// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.analysis

import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.twoplayer.blockade.BlockadeTestCase
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeTstUtil
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove
import com.barrybecker4.game.twoplayer.blockade.board.path.Path
import com.barrybecker4.game.twoplayer.blockade.board.path.PathList
import org.junit.Assert.assertEquals
import org.junit.Test


/**
 * @author Barry Becker
 */
class BoardAnalyzerTest extends BlockadeTestCase {


    @Test
  def testFindAllOpponentShortestPathsForSimple5x7Player1(): Unit = {

        val expPaths = new PathList(Array(
            new Path(Array(
                BlockadeTstUtil.createMove(2,2,  4,2, 0, new GamePiece(false), null),
                BlockadeTstUtil.createMove(4,2,  6,2, 0, null, null),
            )),
            new Path(Array(
                BlockadeTstUtil.createMove(2,2,  4,2, 0, new GamePiece(false), null),
                BlockadeTstUtil.createMove(4,2,  6,2, 0, null, null),
                BlockadeTstUtil.createMove(6,2,  6,4, 0, new GamePiece(true), null),
            )),
            new Path(Array(
                BlockadeTstUtil.createMove(2,4,  4,4, 0, new GamePiece(false), null),
                BlockadeTstUtil.createMove(4,4,  6,4, 0, null, null),
            )),
            new Path(Array(
                BlockadeTstUtil.createMove(2,4,  4,4, 0, new GamePiece(false), null),
                BlockadeTstUtil.createMove(4,4,  6,4, 0, null, null),
                BlockadeTstUtil.createMove(6,4,  6,2, 0, new GamePiece(true), null),
            )),
        ))

        verifyOpponentShortestPaths("board/analysis/initial5x7", true, expPaths)
    }

    @Test
  def testFindAllOpponentShortestPathsForSimple5x7Player2(): Unit = {

        val expPaths = new PathList(Array(
            new Path(Array(
                BlockadeTstUtil.createMove(6,2,  4,2, 0, new GamePiece(true), null),
                BlockadeTstUtil.createMove(4,2,  2,2, 0, null, null),
            )),
            new Path(Array(
                BlockadeTstUtil.createMove(6,2,  5,3, 0, new GamePiece(true), null),
                BlockadeTstUtil.createMove(5,3,  4,4, 0, null, null),
                BlockadeTstUtil.createMove(4,4,  2,4, 0, null, null),
            )),
            new Path(Array(
                BlockadeTstUtil.createMove(6,4,  4,4, 0, new GamePiece(true), null),
                BlockadeTstUtil.createMove(4,4,  2,4, 0, null, null),
            )),
            new Path(Array(
                BlockadeTstUtil.createMove(6,4,  4,4, 0, new GamePiece(true), null),
                BlockadeTstUtil.createMove(4,4,  4,2, 0, null, null),
                BlockadeTstUtil.createMove(4,2,  2,2, 0, null, null),
            )),
        ))

        verifyOpponentShortestPaths("board/analysis/initial5x7", false, expPaths)
    }

    @Test
  def testFindAllOpponentShortestPathsFor5x7Player1AfterFirstMove(): Unit = {

        val expPaths = new PathList(Array(
            new Path(Array(
                BlockadeTstUtil.createMove(2,4,  3,4, 0, new GamePiece(false), null),
                BlockadeTstUtil.createMove(3,4,  3,2, 0, null, null),
                BlockadeTstUtil.createMove(3,2,  5,2, 0, null, null),
                BlockadeTstUtil.createMove(5,2,  6,2, 0, null, null),
            )),
            new Path(Array(
                BlockadeTstUtil.createMove(2,4,  3,5, 0, new GamePiece(false), null),
                BlockadeTstUtil.createMove(3,5,  5,5, 0, null, null),
                BlockadeTstUtil.createMove(5,5,  5,3, 0, null, null),
                BlockadeTstUtil.createMove(5,3,  6,4, 0, null, null),
            )),
            new Path(Array(
                BlockadeTstUtil.createMove(4,2,  6,2, 0, new GamePiece(false), null),
            )),
            new Path(Array(
                BlockadeTstUtil.createMove(4,2,  6,2, 0, new GamePiece(false), null),
                BlockadeTstUtil.createMove(6,2,  6,4, 0, new GamePiece(true), null),
            )),
        ))

        verifyOpponentShortestPaths("board/analysis/afterFirstMove5x7", true, expPaths)
    }

    @Test
  def testFindAllOpponentShortestPathsFor5x7Player2AfterFirstMove(): Unit = {

        val expPaths = new PathList(Array(
            new Path(Array(
                BlockadeTstUtil.createMove(4,4,  3,5, 0, new GamePiece(true), null),
                BlockadeTstUtil.createMove(3,5,  2,4, 0, null, null),
            )),
            new Path(Array(
                BlockadeTstUtil.createMove(4,4,  4,3, 0, new GamePiece(true), null),
                BlockadeTstUtil.createMove(4,3,  3,2, 0, null, null),
                BlockadeTstUtil.createMove(3,2,  2,2, 0, null, null),
            )),
            new Path(Array(
                BlockadeTstUtil.createMove(6,2,  5,2, 0, new GamePiece(true), null),
                BlockadeTstUtil.createMove(5,2,  3,2, 0, null, null),
                BlockadeTstUtil.createMove(3,2,  2,2, 0, null, null),
            )),
            new Path(Array(
                BlockadeTstUtil.createMove(6,2,  5,2, 0, new GamePiece(true), null),
                BlockadeTstUtil.createMove(5,2,  3,2, 0, null, null),
                BlockadeTstUtil.createMove(3,2,  2,2, 0, null, null),
                BlockadeTstUtil.createMove(2,2,  2,4, 0, null, null),
            )),
        ))

        verifyOpponentShortestPaths("board/analysis/afterFirstMove5x7", false, expPaths)
    }

    @Test
  def testFindAllOpponentShortestPathsFor11x14Endgame(): Unit = {

        val expPaths = new PathList(Array(
            new Path(Array(
                BlockadeTstUtil.createMove(5,8,  4,8, 0, new GamePiece(true), null),
            )),
            new Path(Array(
                BlockadeTstUtil.createMove(5,8,  4,8, 0, new GamePiece(true), null),
                BlockadeTstUtil.createMove(4,8,  3,7, 0, null, null),
                BlockadeTstUtil.createMove(3,7,  3,5, 0, null, null),
                BlockadeTstUtil.createMove(3,5,  3,3, 0, null, null),
                BlockadeTstUtil.createMove(3,3,  4,4, 0, null, null),
            )),
            new Path(Array(
                BlockadeTstUtil.createMove(8,3,  9,2, 0, new GamePiece(true), null),
                BlockadeTstUtil.createMove(9,2,  10,3, 0, null, null),
                BlockadeTstUtil.createMove(10,3,  10,5, 0, null, null),
                BlockadeTstUtil.createMove(10,5,  10,7, 0, null, null),
                BlockadeTstUtil.createMove(10,7,  9,8, 0, null, null),
                BlockadeTstUtil.createMove(9,8,  8,9, 0, null, null),
                BlockadeTstUtil.createMove(8,9,  9,10, 0, null, null),
                BlockadeTstUtil.createMove(9,10,  11,10, 0, null, null),
                BlockadeTstUtil.createMove(11,10,  13,10, 0, null, null),
                BlockadeTstUtil.createMove(13,10,  13,8, 0, null, null),
                BlockadeTstUtil.createMove(13,8,  13,6, 0, null, null),
                BlockadeTstUtil.createMove(13,6,  13,4, 0, null, null),
                BlockadeTstUtil.createMove(13,4,  11,4, 0, null, null),
                BlockadeTstUtil.createMove(11,4,  12,3, 0, null, null),
                BlockadeTstUtil.createMove(12,3,  13,2, 0, null, null),
                BlockadeTstUtil.createMove(13,2,  14,3, 0, null, null),
                BlockadeTstUtil.createMove(14,3,  14,5, 0, null, null),
                BlockadeTstUtil.createMove(14,5,  14,7, 0, null, null),
                BlockadeTstUtil.createMove(14,7,  14,9, 0, null, null),
                BlockadeTstUtil.createMove(14,9,  14,11, 0, null, null),
                BlockadeTstUtil.createMove(14,11,  12,11, 0, null, null),
                BlockadeTstUtil.createMove(12,11,  10,11, 0, null, null),
                BlockadeTstUtil.createMove(10,11,  8,11, 0, null, null),
                BlockadeTstUtil.createMove(8,11,  6,11, 0, null, null),
                BlockadeTstUtil.createMove(6,11,  4,11, 0, null, null),
                BlockadeTstUtil.createMove(4,11,  3,10, 0, null, null),
                BlockadeTstUtil.createMove(3,10,  2,9, 0, null, null),
                BlockadeTstUtil.createMove(2,9,  2,11, 0, null, null),
                BlockadeTstUtil.createMove(2,11,  1,10, 0, null, null),
                BlockadeTstUtil.createMove(1,10,  1,8, 0, null, null),
                BlockadeTstUtil.createMove(1,8,  2,7, 0, null, null),
                BlockadeTstUtil.createMove(2,7,  2,5, 0, null, null),
                BlockadeTstUtil.createMove(2,5,  2,3, 0, null, null),
                BlockadeTstUtil.createMove(2,3,  3,2, 0, null, null),
                BlockadeTstUtil.createMove(3,2,  4,1, 0, null, null),
                BlockadeTstUtil.createMove(4,1,  5,2, 0, null, null),
                BlockadeTstUtil.createMove(5,2,  7,2, 0, null, null),
                BlockadeTstUtil.createMove(7,2,  7,4, 0, null, null),
                BlockadeTstUtil.createMove(7,4,  6,5, 0, null, null),
                BlockadeTstUtil.createMove(6,5,  5,4, 0, null, null),
                BlockadeTstUtil.createMove(5,4,  5,6, 0, null, null),
                BlockadeTstUtil.createMove(5,6,  5,7, 0, null, null),
                BlockadeTstUtil.createMove(5,7,  6,8, 0, null, null),
                BlockadeTstUtil.createMove(6,8,  4,8, 0, null, null),
            )),
            new Path(Array(
                BlockadeTstUtil.createMove(8,3,  9,2, 0, new GamePiece(true), null),
                BlockadeTstUtil.createMove(9,2,  10,3, 0, null, null),
                BlockadeTstUtil.createMove(10,3,  10,5, 0, null, null),
                BlockadeTstUtil.createMove(10,5,  10,7, 0, null, null),
                BlockadeTstUtil.createMove(10,7,  9,8, 0, null, null),
                BlockadeTstUtil.createMove(9,8,  8,9, 0, null, null),
                BlockadeTstUtil.createMove(8,9,  9,10, 0, null, null),
                BlockadeTstUtil.createMove(9,10,  11,10, 0, null, null),
                BlockadeTstUtil.createMove(11,10,  13,10, 0, null, null),
                BlockadeTstUtil.createMove(13,10,  13,8, 0, null, null),
                BlockadeTstUtil.createMove(13,8,  13,6, 0, null, null),
                BlockadeTstUtil.createMove(13,6,  13,4, 0, null, null),
                BlockadeTstUtil.createMove(13,4,  11,4, 0, null, null),
                BlockadeTstUtil.createMove(11,4,  12,3, 0, null, null),
                BlockadeTstUtil.createMove(12,3,  13,2, 0, null, null),
                BlockadeTstUtil.createMove(13,2,  14,3, 0, null, null),
                BlockadeTstUtil.createMove(14,3,  14,5, 0, null, null),
                BlockadeTstUtil.createMove(14,5,  14,7, 0, null, null),
                BlockadeTstUtil.createMove(14,7,  14,9, 0, null, null),
                BlockadeTstUtil.createMove(14,9,  14,11, 0, null, null),
                BlockadeTstUtil.createMove(14,11,  12,11, 0, null, null),
                BlockadeTstUtil.createMove(12,11,  10,11, 0, null, null),
                BlockadeTstUtil.createMove(10,11,  8,11, 0, null, null),
                BlockadeTstUtil.createMove(8,11,  6,11, 0, null, null),
                BlockadeTstUtil.createMove(6,11,  4,11, 0, null, null),
                BlockadeTstUtil.createMove(4,11,  3,10, 0, null, null),
                BlockadeTstUtil.createMove(3,10,  2,9, 0, null, null),
                BlockadeTstUtil.createMove(2,9,  2,11, 0, null, null),
                BlockadeTstUtil.createMove(2,11,  1,10, 0, null, null),
                BlockadeTstUtil.createMove(1,10,  1,8, 0, null, null),
                BlockadeTstUtil.createMove(1,8,  2,7, 0, null, null),
                BlockadeTstUtil.createMove(2,7,  2,5, 0, null, null),
                BlockadeTstUtil.createMove(2,5,  2,3, 0, null, null),
                BlockadeTstUtil.createMove(2,3,  3,2, 0, null, null),
                BlockadeTstUtil.createMove(3,2,  4,1, 0, null, null),
                BlockadeTstUtil.createMove(4,1,  5,2, 0, null, null),
                BlockadeTstUtil.createMove(5,2,  7,2, 0, null, null),
                BlockadeTstUtil.createMove(7,2,  7,4, 0, null, null),
                BlockadeTstUtil.createMove(7,4,  6,5, 0, null, null),
                BlockadeTstUtil.createMove(6,5,  5,4, 0, null, null),
                BlockadeTstUtil.createMove(5,4,  5,6, 0, null, null),
                BlockadeTstUtil.createMove(5,6,  5,7, 0, null, null),
                BlockadeTstUtil.createMove(5,7,  6,8, 0, null, null),
                BlockadeTstUtil.createMove(6,8,  4,8, 0, null, null),
                BlockadeTstUtil.createMove(4,8,  3,7, 0, null, null),
                BlockadeTstUtil.createMove(3,7,  3,5, 0, null, null),
                BlockadeTstUtil.createMove(3,5,  3,3, 0, null, null),
                BlockadeTstUtil.createMove(3,3,  4,4, 0, null, null),
            )),
        ))

        verifyOpponentShortestPaths("whitebox/endGame", false, expPaths)
    }


    /**
     * @param filename name of the test file to restore
     * @param player1  player to check shortest paths for
     * @param expPaths the expected shortest paths to opponent home bases for the specified player.
     */
    private def verifyOpponentShortestPaths(filename: String, player1: Boolean, expPaths: PathList): Unit = {

        restore(filename)

        val board = controller_.getBoard.asInstanceOf[BlockadeBoard]
        val analyzer = new BoardAnalyzer(board)

        val paths = analyzer.findAllOpponentShortestPaths(player1)

        //assertEquals("Unexpected number of shortest paths", 4, paths.size())
        assertEquals("Unexpected paths. Got\n" + BlockadeTstUtil.getConstructorString(paths),
                expPaths, paths)
    }

}
