/* Copyright by Barry G. Becker, 2004-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.move

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.common.{GameContext, MoveList}
import com.barrybecker4.game.common.board.{Board, CaptureList}
import com.barrybecker4.game.twoplayer.common.{BestMoveFinder, TwoPlayerMove}
import com.barrybecker4.game.twoplayer.go.board.{GoBoard, GoProfiler, GoSearchable}
import com.barrybecker4.game.twoplayer.go.board.analysis.CandidateMoveAnalyzer
import com.barrybecker4.game.twoplayer.go.board.analysis.territory.TerminalScoreCalculator
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoStone}
import com.barrybecker4.game.twoplayer.go.board.elements.string.IGoString
import com.barrybecker4.optimization.parameter.ParameterArray
import scala.jdk.CollectionConverters._

/**
 * Responsible for determining a set of reasonable next moves.
 */
final class GoMoveGenerator(searchable_ : GoSearchable) {

  /**
   * @return all reasonably good next moves with statically evaluated scores.
   */
  def generateEvaluatedMoves(lastMove: GoMove, weights: ParameterArray): MoveList[GoMove] = {
    val prof = GoProfiler.getInstance
    prof.startGenerateMoves()

    val moveList = generatePossibleMoves(lastMove)
    dropMovesThatDoNotForceAReply(moveList)

    moveList.asScala.foreach { move =>
      setMoveValue(weights, move)
    }
    val finder = new BestMoveFinder[GoMove](searchable_.getSearchOptions.getBestMovesSearchOptions)
    val bestMoves = finder.getBestMoves(moveList)
    addBackAtariMoves(moveList, bestMoves)

    addPassingMoveIfNeeded(lastMove, moveList, bestMoves, weights)

    prof.stopGenerateMoves()
    bestMoves
  }

  /**
   * @return all possible reasonable next moves. We try to limit to reasonable moves as best we can, but that
   * is difficult without static evaluation. At least no illegal moves will be returned.
   */
  private def generatePossibleMoves(lastMove: GoMove): MoveList[GoMove] = {
    val board = searchable_.getBoard
    val moveList = new MoveList[GoMove]()
    val nCols = board.getNumCols
    val nRows = board.getNumRows

    val candidateMoves = new CandidateMoveAnalyzer(board)

    val player1 = (lastMove == null) || !lastMove.isPlayer1
    val lastMoveValue = if (lastMove == null) 0 else lastMove.getValue

    for (i <- 1 to nCols) {
      for (j <- 1 to nRows) {
        // if its a candidate move and not an immediate take-back (which would break the rule of ko)
        if (candidateMoves.isCandidateMove(j, i) && !GoMoveGenerator.isTakeBack(j, i, lastMove, board)) {
          val m = new GoMove(new ByteLocation(j, i), lastMoveValue, new GoStone(player1))

          if (m.isSuicidal(board)) {
            GameContext.log(3, s"The move was a suicide (can't add it to the list): $m")
          } else {
            moveList.add(m)
          }
        }
      }
    }
    moveList
  }

  /**
   * Make the generated move, determine its value, set it into the move, and undo the move on the board.
   */
  private def setMoveValue(weights: ParameterArray, m: GoMove): Unit = {
    val prof = GoProfiler.getInstance
    prof.stopGenerateMoves()
    m.setUrgent(UrgentMoveGenerator.isUrgentCandidate(m, searchable_.getBoard))
    searchable_.makeInternalMove(m)

    m.setValue(searchable_.worth(m, weights))

    // now revert the board
    searchable_.undoInternalMove(m)
    prof.startGenerateMoves()
  }

  /**
   * The prune can drop an atari escape or a capture because the static score prefers
   * a quieter point. Put those points back. Only strings already in atari contribute,
   * one point each, so the list passed to quiescence stays small.
   */
  private def addBackAtariMoves(evaluated: MoveList[GoMove], kept: MoveList[GoMove]): Unit = {
    val liberties = atariLiberties(searchable_.getBoard)
    if (liberties.isEmpty) return
    val already = kept.asScala.iterator
      .filter(!_.isPassingMove)
      .map(move => (move.getToRow.toInt, move.getToCol.toInt))
      .toSet
    evaluated.asScala.foreach { move =>
      val point = (move.getToRow.toInt, move.getToCol.toInt)
      if (move.isUrgent && liberties.contains(point) && !already.contains(point)) {
        kept.add(move)
      }
    }
  }

  private def atariLiberties(board: GoBoard): Set[(Int, Int)] = {
    val points = scala.collection.mutable.Set.empty[(Int, Int)]
    val seen = scala.collection.mutable.Set.empty[IGoString]
    for (r <- 1 to board.getNumRows; c <- 1 to board.getNumCols) {
      val pos = board.getPosition(r, c).asInstanceOf[GoBoardPosition]
      if (pos.isOccupied && pos.getString != null && seen.add(pos.getString)) {
        val liberties = pos.getString.getLiberties(board)
        if (liberties.size == 1) {
          val liberty = liberties.iterator.next()
          points.add((liberty.getRow, liberty.getCol))
        }
      }
    }
    points.toSet
  }

  /**
   * Pass once the game is past the opening, and only when every stone move gives up
   * at least a point. A first-line penalty is a fraction of a point, so it must not
   * make passing look better than playing an empty edge point. The pass is scored
   * from the position itself. Copying the previous move's value is wrong at the root,
   * where a human move is stored as zero.
   */
  private def addPassingMoveIfNeeded(
    lastMove: TwoPlayerMove,
    evaluated: MoveList[GoMove],
    kept: MoveList[GoMove],
    weights: ParameterArray
  ): Unit = {
    val b = searchable_.getBoard
    if (searchable_.getNumMoves() <= (b.getNumCols + b.getNumRows)) return

    val player1 = (lastMove == null) || !lastMove.isPlayer1
    val current = searchable_.worth(lastMove.asInstanceOf[GoMove], weights)
    val losesAPoint = evaluated.isEmpty || evaluated.asScala.forall { move =>
      if (player1) move.getValue <= current - GoMoveGenerator.OnePoint
      else move.getValue >= current + GoMoveGenerator.OnePoint
    }
    // At the root, room in a shared region is not settled life, so the computer
    // plays a stone. Deeper in the tree a pass is allowed: without it every
    // unsettled endgame is read to the full depth. A group that cannot force
    // a reply has already been removed, and then the only move is to pass.
    val noStoneMove = kept.asScala.forall(_.isPassingMove)
    val unsettledAtRoot = !noStoneMove && !searchable_.isInsideSearchTree &&
      new TerminalScoreCalculator(b).hasUnsettledGroup
    if (noStoneMove || (losesAPoint && !unsettledAtRoot)) {
      kept.add(kept.size, GoMove.createPassMove(current, player1))
    }
  }

  /**
   * Late in the game, at the move the computer will play, drop a point that
   * black or white can ignore. A dead group is filled only at the point that
   * makes it live if the opponent passes. Capturing or atari on a group that
   * is still alive also has to be answered. Deeper plies still see every
   * candidate, so a fight that needs two moves is not cut off inside the tree.
   */
  private def dropMovesThatDoNotForceAReply(moves: MoveList[GoMove]): Unit = {
    val board = searchable_.getBoard
    if (searchable_.isInsideSearchTree) return
    if (searchable_.getNumMoves() <= board.getNumRows + board.getNumCols) return

    val scored = new TerminalScoreCalculator(board)
    val dead = scored.deadPoints()
    val before = scored.blackAdvantage()
    val it = moves.iterator()
    while (it.hasNext) {
      val move = it.next()
      if (!earnsAReply(move, dead, board, before)) {
        it.remove()
      }
    }
  }

  private def earnsAReply(move: GoMove, dead: Set[(Int, Int)], board: GoBoard, before: Int): Boolean = {
    if (threatensLivingEnemy(move, dead, board) || touchesNonDeadFriend(move, dead, board)) {
      return true
    }
    searchable_.makeInternalMove(move)
    val after =
      try new TerminalScoreCalculator(board).blackAdvantage()
      finally searchable_.undoInternalMove(move)
    if (move.isPlayer1) after >= before + GoMoveGenerator.OnePointOfTerritory
    else before >= after + GoMoveGenerator.OnePointOfTerritory
  }

  /** Atari or capture of a string terminal scoring has not already given up. */
  private def threatensLivingEnemy(move: GoMove, dead: Set[(Int, Int)], board: GoBoard): Boolean = {
    val seen = scala.collection.mutable.Set.empty[IGoString]
    orthNeighbors(move, board).exists { pos =>
      val enemy = pos.isOccupied && pos.getPiece.isOwnedByPlayer1 != move.isPlayer1
      enemy && !dead.contains((pos.getRow.toInt, pos.getCol.toInt)) && pos.getString != null && seen.add(pos.getString) && {
        val libs = pos.getString.getNumLiberties(board)
        libs == 1 || libs == 2
      }
    }
  }

  /** A stone of ours that can still live. Playing next to it is still a fight. */
  private def touchesNonDeadFriend(move: GoMove, dead: Set[(Int, Int)], board: GoBoard): Boolean = {
    orthNeighbors(move, board).exists { pos =>
      pos.isOccupied && pos.getPiece.isOwnedByPlayer1 == move.isPlayer1 &&
        !dead.contains((pos.getRow.toInt, pos.getCol.toInt))
    }
  }

  private def orthNeighbors(move: GoMove, board: GoBoard): List[GoBoardPosition] = {
    val row = move.getToRow.toInt
    val col = move.getToCol.toInt
    val points = List((row - 1, col), (row + 1, col), (row, col - 1), (row, col + 1))
    points.collect {
      case (r, c) if r >= 1 && c >= 1 && r <= board.getNumRows && c <= board.getNumCols =>
        board.getPosition(r, c).asInstanceOf[GoBoardPosition]
    }
  }
}

object GoMoveGenerator {
  /** Worth is stored in tenths of a point. */
  private val OnePoint = 10

  /** Terminal territory is whole points, not tenths. */
  private val OnePointOfTerritory = 1

  /**
   * It is a take-back move if the proposed move position (row,col) would immediately replace the last captured piece
   * and capture the stone that did the capturing.
   * @return true of this is an immediate take-back (not allowed in go - see "rule of ko")
   */
  def isTakeBack(row: Int, col: Int, lastMove: GoMove, board: GoBoard): Boolean = {
    if (lastMove == null) return false

    val captures = lastMove.getCaptures
    if (captures != null && captures.size == 1) {
      val capture = captures.getFirst.asInstanceOf[GoBoardPosition]
      if (capture.getRow == row && capture.getCol == col) {
        val lastStone = board.getPosition(lastMove.getToRow, lastMove.getToCol).asInstanceOf[GoBoardPosition]
        if (lastStone.getNumLiberties(board) == 1 && lastStone.getString.getMembers.size == 1) {
          GameContext.log(2, "it is a takeback ")
          return true
        }
      }
    }
    false
  }
}
