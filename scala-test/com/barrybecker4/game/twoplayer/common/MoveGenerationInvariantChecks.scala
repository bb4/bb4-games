/* Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common

import com.barrybecker4.game.common.{GameContext, Move, MoveList}
import com.barrybecker4.game.twoplayer.blockade.board.Homes
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard
import com.barrybecker4.game.twoplayer.chess.{ChessBoard, ChessMove}
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.move.{GoMove, GoMoveGenerator}
import scala.jdk.CollectionConverters.*

/**
 * Shared invariant checks for move generation across seeded random playouts.
 */
object MoveGenerationInvariantChecks {

  def moveSet[M <: Move](moves: MoveList[M]): Set[M] =
    moves.asScala.toSet

  def assertDeterministicGeneration[M <: Move](
    first: MoveList[M],
    second: MoveList[M],
    moveIdentity: M => String,
    context: String,
    includeInCheck: M => Boolean = (_: M) => true
  ): Unit = {
    val a = first.asScala.filter(includeInCheck).map(moveIdentity).toSet
    val b = second.asScala.filter(includeInCheck).map(moveIdentity).toSet
    assert(
      a == b,
      s"$context: generateMoves differed on second call (sizes ${a.size} vs ${b.size})"
    )
  }

  def assertMakeUndoRestoresHash[M <: TwoPlayerMove](
    searchable: TwoPlayerSearchable[M, ?],
    move: M,
    context: String
  ): Unit = {
    val hashBefore = searchable.getHashKey().getKey
    val played = move.copy().asInstanceOf[M]
    searchable.makeInternalMove(played)
    searchable.undoInternalMove(played)
    assert(
      searchable.getHashKey().getKey == hashBefore,
      s"$context: getHashKey changed after make/undo of $move (before=$hashBefore, after=${searchable.getHashKey().getKey})"
    )
  }

  def checkCommonInvariants[M <: TwoPlayerMove](
    searchable: TwoPlayerSearchable[M, ?],
    moves: MoveList[M],
    lastMove: M,
    weights: com.barrybecker4.optimization.parameter.ParameterArray,
    moveIdentity: M => String,
    context: String,
    includeInDeterministicCheck: M => Boolean = (_: M) => true,
    checkDeterministic: Boolean = true
  ): Unit = {
    if (checkDeterministic) {
      val again = searchable.generateMoves(lastMove, weights)
      assertDeterministicGeneration(
        moves,
        again,
        moveIdentity,
        context,
        includeInDeterministicCheck
      )
    }
    moves.asScala.foreach { m =>
      assertMakeUndoRestoresHash(searchable, m, context)
    }
  }

  def checkChessInvariants(
    searchable: TwoPlayerSearchable[ChessMove, ChessBoard],
    moves: MoveList[ChessMove],
    context: String
  ): Unit = {
    val board = searchable.getBoard
    moves.asScala.foreach { move =>
      assert(
        !board.causesSelfCheck(move),
        s"$context: generated move leaves king in check: $move"
      )
    }
    if (moves.isEmpty && searchable.getNumMoves() > 0) {
      assert(
        searchable.done(null.asInstanceOf[ChessMove], recordWin = false),
        s"$context: no generated moves but game is not over"
      )
    }
  }

  def checkGoInvariants(
    board: GoBoard,
    moves: MoveList[GoMove],
    lastMove: GoMove,
    context: String
  ): Unit = {
    moves.asScala.foreach { move =>
      if (!move.isPassingMove && !move.isResignationMove) {
        assert(
          !move.isSuicidal(board),
          s"$context: generated suicidal move: $move"
        )
        assert(
          !GoMoveGenerator.isTakeBack(move.getToRow, move.getToCol, lastMove, board),
          s"$context: generated immediate ko take-back: $move"
        )
      }
    }
  }

  def checkBlockadeInvariants(
    searchable: TwoPlayerSearchable[BlockadeMove, BlockadeBoard],
    moves: MoveList[BlockadeMove],
    context: String
  ): Unit = {
    val expectedPaths = Homes.NUM_HOMES * Homes.NUM_HOMES
    val board = searchable.getBoard
    moves.asScala.foreach { move =>
      if (move.getWall != null) {
        val hashBefore = searchable.getHashKey().getKey
        val played = move.copy().asInstanceOf[BlockadeMove]
        searchable.makeInternalMove(played)
        try {
          val paths1 = board.findAllOpponentShortestPaths(player1 = true)
          val paths2 = board.findAllOpponentShortestPaths(player1 = false)
          assert(
            paths1.size() >= expectedPaths && paths2.size() >= expectedPaths,
            s"$context: wall move blocked paths (p1=${paths1.size()}, p2=${paths2.size()}): $move"
          )
        } finally {
          searchable.undoInternalMove(played)
          assert(
            searchable.getHashKey().getKey == hashBefore,
            s"$context: hash not restored after blockade path check for $move"
          )
        }
      }
    }
  }

  /** @return true if the playout should stop (game over or no moves). */
  def playRandomMove[M <: TwoPlayerMove](
    searchable: TwoPlayerSearchable[M, ?],
    moves: MoveList[M],
    context: String
  ): Boolean = {
    if (moves.isEmpty) {
      searchable.getNumMoves() == 0 || searchable.done(null.asInstanceOf[M], recordWin = false)
    } else {
      val next = moves.getRandomMove.copy().asInstanceOf[M]
      searchable.makeInternalMove(next)
      searchable.done(next, recordWin = false)
    }
  }

  def player1ToMove(lastMove: TwoPlayerMove): Boolean =
    lastMove == null || !lastMove.isPlayer1

  def runSeededPlayout[M <: TwoPlayerMove](
    searchable: TwoPlayerSearchable[M, ?],
    weightsForLastMove: M => com.barrybecker4.optimization.parameter.ParameterArray,
    maxSteps: Int,
    seed: Int,
    onPosition: (M, String) => Unit
  ): Unit = {
    GameContext.setRandomSeed(seed)
    var lastMove: M = null.asInstanceOf[M]
    var step = 0
    while (step < maxSteps) {
      val context = s"seed=$seed step=$step"
      onPosition(lastMove, context)
      val weights = weightsForLastMove(lastMove)
      val moves = searchable.generateMoves(lastMove, weights)
      if (playRandomMove(searchable, moves, context)) {
        return
      }
      lastMove = searchable.getMoveList.getLastMove
      step += 1
    }
  }
}
