/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import org.junit.jupiter.api.Assertions.{assertFalse, assertTrue}
import org.junit.jupiter.api.Test

/** @author Barry Becker */
class CandidateMovesTest:

  /** instance under test */
  private var candidates: CandidateMoves = scala.compiletime.uninitialized

  /** The center position should be a candidate if no moves have been played yet. */
  @Test
  def testCandidatesWhenEmpty10x10Board(): Unit =
    val board = new GoMokuBoard(10, 10)

    candidates = board.getCandidateMoves

    assertTrue(candidates.isCandidateMove(6, 6), "Missing candidate move.")
    assertFalse(candidates.isCandidateMove(5, 6), "Unexpected candidate.")
    assertFalse(candidates.isCandidateMove(4, 7), "Unexpected candidate.")

  /** The center position should be a candidate if no moves have been played yet. */
  @Test
  def testCandidatesWhenEmpty2x2Board(): Unit =
    val board = new GoMokuBoard(2, 2)

    candidates = board.getCandidateMoves

    assertTrue(candidates.isCandidateMove(2, 2), "Missing candidate move.")
    assertFalse(candidates.isCandidateMove(1, 2), "Unexpected candidate.")
    assertFalse(candidates.isCandidateMove(2, 1), "Unexpected candidate.")
    assertFalse(candidates.isCandidateMove(1, 1), "Unexpected candidate.")

  /** No candidates if the board is completely full. */
  @Test
  def testCandidatesWhenNoMoves(): Unit =
    val board = new GoMokuBoard(2, 2)
    board.makeMove(TwoPlayerMove.createMove(new ByteLocation(1, 1), 0, new GamePiece(true)))
    board.makeMove(TwoPlayerMove.createMove(new ByteLocation(2, 2), 0, new GamePiece(false)))
    board.makeMove(TwoPlayerMove.createMove(new ByteLocation(1, 2), 0, new GamePiece(true)))
    board.makeMove(TwoPlayerMove.createMove(new ByteLocation(2, 1), 0, new GamePiece(false)))

    candidates = board.getCandidateMoves

    assertFalse(candidates.isCandidateMove(2, 2), "Unexpected candidate.")
    assertFalse(candidates.isCandidateMove(1, 2), "Unexpected candidate.")
    assertFalse(candidates.isCandidateMove(1, 1), "Unexpected candidate.")
    assertFalse(candidates.isCandidateMove(2, 1), "Unexpected candidate.")

  /** The 8 spaces surrounding should be candidates. */
  @Test
  def testCandidatesWhenOneCenterMove(): Unit =
    val board = new GoMokuBoard(10, 10)
    board.makeMove(TwoPlayerMove.createMove(new ByteLocation(3, 3), 0, new GamePiece(true)))

    candidates = board.getCandidateMoves

    assertTrue(candidates.isCandidateMove(4, 4), "Missing candidate move.")
    assertTrue(candidates.isCandidateMove(2, 3), "Missing candidate move.")
    assertTrue(candidates.isCandidateMove(3, 2), "Missing candidate move.")
    assertFalse(candidates.isCandidateMove(5, 6), "Unexpected candidate.")
    assertFalse(candidates.isCandidateMove(4, 7), "Unexpected candidate.")

  /** This is a typical case. */
  @Test
  def testCandidatesWhenTwoMovesPresent(): Unit =
    val board = new GoMokuBoard(10, 10)
    board.makeMove(TwoPlayerMove.createMove(new ByteLocation(3, 3), 0, new GamePiece(true)))
    board.makeMove(TwoPlayerMove.createMove(new ByteLocation(4, 3), 0, new GamePiece(false)))

    candidates = board.getCandidateMoves

    assertTrue(candidates.isCandidateMove(2, 2), "Missing candidate move.")
    assertTrue(candidates.isCandidateMove(2, 3), "Missing candidate move.")
    assertTrue(candidates.isCandidateMove(3, 2), "Missing candidate move.")
    assertTrue(candidates.isCandidateMove(5, 3), "Missing candidate move.")
    assertTrue(candidates.isCandidateMove(5, 4), "Missing candidate move.")
    assertFalse(candidates.isCandidateMove(6, 6), "Unexpected candidate.")
    assertFalse(candidates.isCandidateMove(1, 7), "Unexpected candidate.")
