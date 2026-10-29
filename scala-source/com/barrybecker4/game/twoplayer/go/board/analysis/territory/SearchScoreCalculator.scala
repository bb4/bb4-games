/* Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.territory

import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.EyeStatus
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzerMap
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoStone}
import com.barrybecker4.game.twoplayer.go.options.GoOptions

import scala.collection.mutable
import scala.jdk.CollectionConverters._

/**
 * Japanese point estimate for search.
 *
 * Empty points in a region touched only by living stones of one color are that color's
 * territory. A group is living when Benson marked it, or when it has an eye whose shape
 * status is ALIVE. Health above 0.8 is not enough: eye potential reaches that with no
 * real eye. A string is dead when it is not living and its owner-signed health is below
 * -0.2 (atari, or two liberties and no eye). Anything else is unsettled: it blocks
 * territory and is not a prisoner. Dead stones are removed for the region walk, so the
 * point they sat on can be territory, and each dead stone is also one point.
 * Komi is subtracted from black's score.
 *
 * Phase 5's ladder reader will also mark captured strings dead.
 */
class SearchScoreCalculator(
  board: GoBoard,
  komi: Float = GoOptions.DEFAULT_KOMI,
  analyzerMap: GroupAnalyzerMap = null
) {

  /** Owner-signed health below this is a group the health function already calls lost. */
  private val DeadBelow = -0.2f
  private var groupsWithRealEye: Set[IGoGroup] = Set.empty
  private val rows = board.getNumRows
  private val cols = board.getNumCols

  /** Black-positive points, before the sub-point shape tie-break. */
  def points(): Double = {
    groupsWithRealEye = findGroupsWithAliveEye()
    val dead = deadPositions()
    var blackTerritory = 0
    var whiteTerritory = 0
    for (region <- findRegions(dead)) {
      regionOwner(region, dead) match {
        case Some(true) => blackTerritory += region.size
        case Some(false) => whiteTerritory += region.size
        case None =>
      }
    }
    val prisoners = board.getNumCaptures(false) - board.getNumCaptures(true)
    val deadWhite = dead.count { case (r, c) => isWhiteStone(r, c) }
    val deadBlack = dead.size - deadWhite
    blackTerritory - whiteTerritory + prisoners + deadWhite - deadBlack - komi
  }

  private def deadPositions(): Set[(Int, Int)] = {
    val dead = mutable.Set.empty[(Int, Int)]
    for (r <- 1 to rows; c <- 1 to cols) {
      val pos = position(r, c)
      if (pos.isOccupied && life(pos) == Life.Dead) {
        dead += r -> c
      }
    }
    dead.toSet
  }

  private def life(pos: GoBoardPosition): Life = {
    val stone = pos.getPiece.asInstanceOf[GoStone]
    val signed = if (stone.isOwnedByPlayer1) stone.getHealth else -stone.getHealth
    val benson = pos.getString != null && pos.getString.isUnconditionallyAlive
    val realEye = pos.getGroup != null && groupsWithRealEye.contains(pos.getGroup)
    if (benson || realEye) Life.Alive
    else if (signed < DeadBelow) Life.Dead
    else Life.Unsettled
  }

  /** Groups that already have an eye shape the eye table calls unconditionally alive. */
  private def findGroupsWithAliveEye(): Set[IGoGroup] = {
    if (analyzerMap == null) {
      return Set.empty
    }
    val alive = mutable.Set.empty[IGoGroup]
    val seen = mutable.Set.empty[IGoGroup]
    for (r <- 1 to rows; c <- 1 to cols) {
      val pos = position(r, c)
      val group = if (pos.isOccupied) pos.getGroup else null
      if (group != null && seen.add(group)) {
        val eyes = analyzerMap.getAnalyzer(group).getEyes(board)
        if (eyes != null && eyes.asScala.exists(_.getStatus == EyeStatus.ALIVE)) {
          alive.add(group)
        }
      }
    }
    alive.toSet
  }

  private def findRegions(dead: Set[(Int, Int)]): List[List[(Int, Int)]] = {
    val visited = Array.ofDim[Boolean](rows + 1, cols + 1)
    val regions = mutable.ListBuffer.empty[List[(Int, Int)]]
    for (r <- 1 to rows; c <- 1 to cols) {
      if (!visited(r)(c) && isEmptyForScoring(r, c, dead)) {
        val region = mutable.ListBuffer.empty[(Int, Int)]
        val stack = mutable.Stack((r, c))
        visited(r)(c) = true
        while (stack.nonEmpty) {
          val (cr, cc) = stack.pop()
          region += cr -> cc
          for ((nr, nc) <- orthNeighbors(cr, cc)) {
            if (!visited(nr)(nc) && isEmptyForScoring(nr, nc, dead)) {
              visited(nr)(nc) = true
              stack.push((nr, nc))
            }
          }
        }
        regions += region.toList
      }
    }
    regions.toList
  }

  /** Some(true) black, Some(false) white, None when unsettled or both colors touch it. */
  private def regionOwner(region: List[(Int, Int)], dead: Set[(Int, Int)]): Option[Boolean] = {
    var livingBlack = false
    var livingWhite = false
    var unsettled = false
    for ((r, c) <- region; (nr, nc) <- orthNeighbors(r, c)) {
      if (!dead.contains((nr, nc))) {
        val pos = position(nr, nc)
        if (pos.isOccupied) {
          life(pos) match {
            case Life.Alive =>
              if (pos.getPiece.isOwnedByPlayer1) livingBlack = true else livingWhite = true
            case Life.Unsettled => unsettled = true
            case Life.Dead =>
          }
        }
      }
    }
    if (unsettled || livingBlack == livingWhite) None
    else Some(livingBlack)
  }

  private def isEmptyForScoring(r: Int, c: Int, dead: Set[(Int, Int)]): Boolean =
    !position(r, c).isOccupied || dead.contains((r, c))

  private def isWhiteStone(r: Int, c: Int): Boolean = {
    val pos = position(r, c)
    pos.isOccupied && !pos.getPiece.isOwnedByPlayer1
  }

  private def position(r: Int, c: Int): GoBoardPosition =
    board.getPosition(r, c).asInstanceOf[GoBoardPosition]

  private def orthNeighbors(r: Int, c: Int): List[(Int, Int)] = {
    val buf = mutable.ListBuffer.empty[(Int, Int)]
    if (r > 1) buf += r - 1 -> c
    if (r < rows) buf += r + 1 -> c
    if (c > 1) buf += r -> (c - 1)
    if (c < cols) buf += r -> (c + 1)
    buf.toList
  }

  private enum Life:
    case Alive, Dead, Unsettled
}
