/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.transposition

import com.barrybecker4.common.geometry.{ByteLocation, Location}
import com.barrybecker4.game.common.board.{BoardPosition, GamePiece}
import com.barrybecker4.game.twoplayer.common.{TwoPlayerBoard, TwoPlayerMove}
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import com.barrybecker4.game.twoplayer.tictactoe.TicTacToeBoard
import org.junit.Assert.{assertEquals, assertFalse}
import org.junit.{Before, Test}

/**
 * Verify expected hash key are generated based on board state.
 * @author Barry Becker
 */
object ZobristHashTest:
  private val CENTER_X_HASH = new HashKey(428667830982598836L)
  private val CORNER_O_HASH = new HashKey(-6688467811848818630L)

class ZobristHashTest:

  private var hash: ZobristHash = scala.compiletime.uninitialized
  private var board: TwoPlayerBoard[TwoPlayerMove] = scala.compiletime.uninitialized

  @Before
  def setUp(): Unit =
    board = new TicTacToeBoard()
    createZHash(board)

  @Test
  def testEmptyBoardHash(): Unit =
    assertEquals("Unexpected hashkey for empty board", new HashKey(), hash.getKey)

  @Test
  def testCenterXHash(): Unit =
    applyMoveToHash(2, 2, true)
    assertEquals("Unexpected hashkey for board with center X", ZobristHashTest.CENTER_X_HASH, hash.getKey)

  @Test
  def testCenterXBoard(): Unit =
    val m = TwoPlayerMove.createMove(new ByteLocation(2, 2), 0, new GamePiece(true))
    board.makeMove(m)
    hash = createZHash(board)
    assertEquals("Unexpected hashkey for board with center X", ZobristHashTest.CENTER_X_HASH, hash.getKey)

  @Test
  def testCornerOHash(): Unit =
    applyMoveToHash(1, 1, false)
    assertEquals("Unexpected hashkey for board with corner O", ZobristHashTest.CORNER_O_HASH, hash.getKey)

  @Test
  def testCornerOBoard(): Unit =
    val m = TwoPlayerMove.createMove(new ByteLocation(1, 1), 0, new GamePiece(false))
    board.makeMove(m)
    hash = createZHash(board)
    assertEquals("Unexpected hashkey for board with corner O", ZobristHashTest.CORNER_O_HASH, hash.getKey)

  @Test
  def testHashAfterUndo(): Unit =
    applyMoveToHash(2, 2, true)
    applyMoveToHash(2, 2, true)
    assertEquals("Unexpected hashkey for entry board after undo", new HashKey(), hash.getKey)

  @Test
  def testHashAfterTwoMoves(): Unit =
    applyMoveToHash(2, 2, true)
    applyMoveToHash(1, 1, false)
    assertEquals("Unexpected hashkey for board after 2 moves", new HashKey(-6422371760107745138L), hash.getKey)

  @Test
  def testHashAfterTwoMovesThenUndoFirst(): Unit =
    applyMoveToHash(2, 2, true)
    applyMoveToHash(1, 1, false)
    applyMoveToHash(2, 2, true)
    assertEquals(
      "Unexpected hashkey for board after 2 moves then an undo",
      ZobristHashTest.CORNER_O_HASH,
      hash.getKey
    )

  @Test
  def testHashAfterTwoMovesThenUndoSecond(): Unit =
    applyMoveToHash(2, 2, true)
    applyMoveToHash(1, 1, false)
    applyMoveToHash(1, 1, false)
    assertEquals(
      "Unexpected hashkey for board after 2 moves then an undo",
      ZobristHashTest.CENTER_X_HASH,
      hash.getKey
    )

  @Test
  def testHashAfterTwoMovesThenUndoMoveO(): Unit =
    board.makeMove(TwoPlayerMove.createMove(new ByteLocation(1, 1), 0, new GamePiece(false)))
    board.makeMove(TwoPlayerMove.createMove(new ByteLocation(2, 2), 0, new GamePiece(true)))
    board.undoMove()
    hash = createZHash(board)
    assertEquals(
      "Unexpected hashkey for board after 2 moves then an undo move",
      ZobristHashTest.CORNER_O_HASH,
      hash.getKey
    )

  @Test
  def testHashAfterTwoMovesThenUndoMoveX(): Unit =
    board.makeMove(TwoPlayerMove.createMove(new ByteLocation(2, 2), 0, new GamePiece(true)))
    board.makeMove(TwoPlayerMove.createMove(new ByteLocation(1, 1), 0, new GamePiece(false)))
    board.undoMove()
    hash = createZHash(board)
    assertEquals(
      "Unexpected hashkey for board after 2 moves then an undo move",
      ZobristHashTest.CENTER_X_HASH,
      hash.getKey
    )

  @Test
  def testHashAfterTwoMovesThenTwoUndos(): Unit =
    board.makeMove(TwoPlayerMove.createMove(new ByteLocation(2, 2), 0, new GamePiece(true)))
    board.makeMove(TwoPlayerMove.createMove(new ByteLocation(1, 1), 0, new GamePiece(false)))
    board.undoMove()
    board.undoMove()
    hash = createZHash(board)
    assertEquals("Unexpected hashkey for board after 2 moves then two undos", new HashKey(), hash.getKey)

  @Test
  def testHashesForDifferentBoardsStatesUnequal(): Unit =
    board.makeMove(TwoPlayerMove.createMove(new ByteLocation(2, 2), 0, new GamePiece(true)))
    board.makeMove(TwoPlayerMove.createMove(new ByteLocation(1, 1), 0, new GamePiece(false)))
    hash = createZHash(board)
    val hash1 = hash.getKey
    board = new TicTacToeBoard()
    board.makeMove(TwoPlayerMove.createMove(new ByteLocation(2, 2), 0, new GamePiece(true)))
    board.makeMove(TwoPlayerMove.createMove(new ByteLocation(3, 1), 0, new GamePiece(false)))
    hash = createZHash(board)
    assertFalse("Hash keys for different moves unexpectedly equal. both=" + hash1, hash1.equals(hash.getKey))

  @Test
  def testHashesForDifferentBoardsStatesUnequalUsingUndo(): Unit =
    board.makeMove(TwoPlayerMove.createMove(new ByteLocation(2, 2), 0, new GamePiece(true)))
    board.makeMove(TwoPlayerMove.createMove(new ByteLocation(1, 1), 0, new GamePiece(false)))
    hash = createZHash(board)
    val hash1 = hash.getKey
    board.undoMove()
    board.undoMove()
    board.makeMove(TwoPlayerMove.createMove(new ByteLocation(2, 2), 0, new GamePiece(true)))
    board.makeMove(TwoPlayerMove.createMove(new ByteLocation(3, 1), 0, new GamePiece(false)))
    hash = createZHash(board)
    assertFalse("Hash keys for different moves unexpectedly equal. both=" + hash1, hash1.equals(hash.getKey))

  @Test
  def testGoHash(): Unit =
    val goBoard = new GoBoard(5, 0)
    goBoard.makeMove(new GoMove(new ByteLocation(3, 3), 0, new GoStone(true)))
    goBoard.makeMove(new GoMove(new ByteLocation(1, 3), 0, new GoStone(false)))
    hash = createZHash(goBoard)
    assertEquals("Unexpected hashkey for GoBoard", new HashKey(-5179128285022783026L), hash.getKey)

  private def applyMoveToHash(row: Int, col: Int, player1: Boolean): Unit =
    val p = new GamePiece(player1)
    val loc: Location = new ByteLocation(row, col)
    val stateIndex = board.getStateIndex(new BoardPosition(row, col, p))
    hash.applyMove(loc, stateIndex)

  private def createZHash(b: TwoPlayerBoard[?]): ZobristHash =
    hash = new ZobristHash(b, 0, true)
    hash
