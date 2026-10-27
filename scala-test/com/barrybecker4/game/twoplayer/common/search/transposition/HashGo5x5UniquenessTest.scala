/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.transposition

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import org.junit.Test

/**
 * Various uniqueness tests for hash keys.
 * @author Barry Becker
 */
class HashGo5x5UniquenessTest extends HashGoBase:

  override def createBoard(): GoBoard = new GoBoard(5, 0)

  /**
   *     12345
   *  01|X XO |     <--- Test if the key is the same wit/without the 1,1 position.
   *  02|  XO |
   *  03| XXXO|
   *  04| O O |
   *  05|    O|
   */
  @Test
  def testBoard5x5Uniqueness(): Unit =
    val moves1 = Array(
      new GoMove(new ByteLocation(1, 3), 0, black()),
      new GoMove(new ByteLocation(1, 4), 0, white()),
      new GoMove(new ByteLocation(2, 3), 0, black()),
      new GoMove(new ByteLocation(2, 4), 0, white()),
      new GoMove(new ByteLocation(3, 2), 0, black()),
      new GoMove(new ByteLocation(3, 3), 0, black()),
      new GoMove(new ByteLocation(3, 4), 0, black()),
      new GoMove(new ByteLocation(3, 5), 0, white()),
      new GoMove(new ByteLocation(4, 2), 0, white()),
      new GoMove(new ByteLocation(4, 4), 0, white()),
      new GoMove(new ByteLocation(5, 5), 0, white()),
      new GoMove(new ByteLocation(1, 1), 0, black()) // only one that is different
    )
    val moves2 = Array(
      new GoMove(new ByteLocation(1, 3), 0, black()),
      new GoMove(new ByteLocation(1, 4), 0, white()),
      new GoMove(new ByteLocation(2, 3), 0, black()),
      new GoMove(new ByteLocation(2, 4), 0, white()),
      new GoMove(new ByteLocation(3, 2), 0, black()),
      new GoMove(new ByteLocation(3, 3), 0, black()),
      new GoMove(new ByteLocation(3, 4), 0, black()),
      new GoMove(new ByteLocation(3, 5), 0, white()),
      new GoMove(new ByteLocation(4, 2), 0, white()),
      new GoMove(new ByteLocation(4, 4), 0, white()),
      new GoMove(new ByteLocation(5, 5), 0, white())
    )
    verifyGoBoardsDistinct(moves1, moves2)

  /** The order in which the moves are placed should not matter */
  @Test
  def testBoard5x5UniquenessReordered(): Unit =
    val moves1 = Array(
      new GoMove(new ByteLocation(1, 1), 0, black()),
      new GoMove(new ByteLocation(1, 4), 0, white()),
      new GoMove(new ByteLocation(1, 3), 0, black()),
      new GoMove(new ByteLocation(3, 5), 0, white()),
      new GoMove(new ByteLocation(2, 3), 0, black()),
      new GoMove(new ByteLocation(2, 4), 0, white()),
      new GoMove(new ByteLocation(3, 2), 0, black()),
      new GoMove(new ByteLocation(4, 2), 0, white()),
      new GoMove(new ByteLocation(3, 3), 0, black()),
      new GoMove(new ByteLocation(4, 4), 0, white()),
      new GoMove(new ByteLocation(3, 4), 0, black()),
      new GoMove(new ByteLocation(5, 5), 0, white())
    )
    val moves2 = Array(
      new GoMove(new ByteLocation(1, 4), 0, white()),
      new GoMove(new ByteLocation(2, 3), 0, black()),
      new GoMove(new ByteLocation(2, 4), 0, white()),
      new GoMove(new ByteLocation(3, 4), 0, black()),
      new GoMove(new ByteLocation(3, 5), 0, white()),
      new GoMove(new ByteLocation(3, 3), 0, black()),
      new GoMove(new ByteLocation(4, 4), 0, white()),
      new GoMove(new ByteLocation(3, 2), 0, black()),
      new GoMove(new ByteLocation(5, 5), 0, white()),
      new GoMove(new ByteLocation(1, 3), 0, black()),
      new GoMove(new ByteLocation(4, 2), 0, white())
    )
    verifyGoBoardsDistinct(moves1, moves2)

  /**
   *       12345
   *      -------
   *    01|  OX |        < note snapback here
   *    02|  XOO|
   *    03| OXXX|
   *    04| X   |
   *    05| O   |
   */
  @Test
  def testBoard5x5Snapback(): Unit =
    val moves1 = Array(
      new GoMove(new ByteLocation(1, 3), 0, white()),
      new GoMove(new ByteLocation(1, 4), 0, black()),
      new GoMove(new ByteLocation(2, 3), 0, black()),
      new GoMove(new ByteLocation(2, 4), 0, white()),
      new GoMove(new ByteLocation(2, 5), 0, white()),
      new GoMove(new ByteLocation(3, 2), 0, white()),
      new GoMove(new ByteLocation(3, 3), 0, black()),
      new GoMove(new ByteLocation(3, 4), 0, black()),
      new GoMove(new ByteLocation(3, 5), 0, black()),
      new GoMove(new ByteLocation(4, 2), 0, black()),
      new GoMove(new ByteLocation(5, 2), 0, white())
    )
    // now after snapback, we get back to the same position
    // I think when we remove in the capture, we are not updating the hashkey.
    // We need to apply the moves when removing and again when restoring.
    val moves2 = Array(
      new GoMove(new ByteLocation(1, 3), 0, white()),
      new GoMove(new ByteLocation(1, 4), 0, black()),
      new GoMove(new ByteLocation(2, 3), 0, black()),
      new GoMove(new ByteLocation(2, 4), 0, white()),
      new GoMove(new ByteLocation(2, 5), 0, white()),
      new GoMove(new ByteLocation(3, 2), 0, white()),
      new GoMove(new ByteLocation(3, 3), 0, black()),
      new GoMove(new ByteLocation(3, 4), 0, black()),
      new GoMove(new ByteLocation(3, 5), 0, black()),
      new GoMove(new ByteLocation(4, 2), 0, black()),
      new GoMove(new ByteLocation(5, 2), 0, white()),
      new GoMove(new ByteLocation(1, 5), 0, white()),
      new GoMove(new ByteLocation(1, 4), 0, black()),
      new GoMove(new ByteLocation(2, 4), 0, white()),
      new GoMove(new ByteLocation(2, 5), 0, white())
    )
    verifyGoBoardSame(moves1, moves2)
