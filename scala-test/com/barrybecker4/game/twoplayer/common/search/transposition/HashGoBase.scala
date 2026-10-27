/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.transposition

import com.barrybecker4.game.common.board.{BoardPosition, GamePiece}
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import org.junit.Assert.{assertEquals, assertTrue}
import org.junit.Before

import scala.jdk.CollectionConverters.*

/**
 * Various uniqueness tests for hash keys.
 * @author Barry Becker
 */
abstract class HashGoBase:

  protected var hash: ZobristHash = scala.compiletime.uninitialized
  protected var board: GoBoard = scala.compiletime.uninitialized

  @Before
  def setUp(): Unit =
    board = createBoard()
    hash = createZobristHash()

  protected def createBoard(): GoBoard

  /**
   * In actual play I found two boards that generated the same hash key. Verify that this does not happen again.
   */
  def verifyGoBoardSame(moves1: Array[GoMove], moves2: Array[GoMove]): Unit =
    compareGoBoards(moves1, moves2, assertEqual = true)

  /**
   * In actual play I found two boards that generated the same hash key. Verify that this does not happen again.
   */
  def verifyGoBoardsDistinct(moves1: Array[GoMove], moves2: Array[GoMove]): Unit =
    compareGoBoards(moves1, moves2, assertEqual = false)

  /**
   * In actual play I found two boards that generated the same hash key. Verify that this does not happen again.
   * @param assertEqual if true, then verify equal, else verify not equal.
   */
  def compareGoBoards(moves1: Array[GoMove], moves2: Array[GoMove], assertEqual: Boolean): Unit =
    applyMoves(moves1)
    val key1 = hash.getKey.copy()

    // apply them again to reset
    applyMoves(moves1)

    board.reset()

    applyMoves(moves2)
    val key2 = hash.getKey

    if assertEqual then
      assertEquals("Keys not equal", key1, key2)
    else
      assertTrue("Keys unexpectedly equal. \nkey1=" + key1 + "\nkey2=" + key2, !key1.equals(key2))

  protected def black(): GoStone = new GoStone(true)

  protected def white(): GoStone = new GoStone(false)

  protected def applyMoves(moves: Array[GoMove]): Unit =
    for move <- moves do
      board.makeMove(move)
      applyMoveToHash(board.getPosition(move.getToLocation))
      if move.getNumCaptures > 0 then
        for capture <- move.getCaptures.asScala do
          val oldPiece = capture.getPiece
          capture.setPiece(if move.isPlayer1 then white() else black())
          applyMoveToHash(capture)
          capture.setPiece(oldPiece)
    System.out.println()

  private def applyMoveToHash(position: BoardPosition): Unit =
    val stateIndex = board.getStateIndex(position)
    System.out.print("stateIndex=" + stateIndex + " for " + position + "   ")
    hash.applyMove(position.getLocation, stateIndex)
    System.out.println(" hash after apply=" + hash.getKey.getKey)

  private def createZobristHash(): ZobristHash =
    new ZobristHash(board, 0, false)
