/* Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.territory

import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoStone}
import com.barrybecker4.game.twoplayer.go.board.elements.string.IGoString

import scala.collection.mutable
import scala.jdk.CollectionConverters._

/**
 * End-of-game life-and-death and territory without mid-game relative-health heuristics.
 *
 * Empty regions (treating already-dead stones as empty) whose living neighbors are all one color
 * are that color's territory. Regions touching both colors are dame (or seki) and score 0.
 *
 * A string is dead when it cannot make two eyes: fewer than two real eyes, no eye
 * of three or more points, and not a mutual seki (both sides share exactly the same
 * liberties). Sharing one dame point with a stronger group is not seki. A dame region
 * of seven or more points is room to live for a group that already has an eye, or at
 * least three liberties. A smaller shared pocket is not room to live.
 *
 * The weakest dead string (fewest eyes, then fewest liberties) is removed first
 * and the board is re-evaluated, so a group that only looked dead because it was
 * surrounded by a deader group can live.
 */
class TerminalScoreCalculator(board: GoBoard) {

  private val rows = board.getNumRows
  private val cols = board.getNumCols

  /** Ownership of a scoring region: Some(true)=black, Some(false)=white, None=dame/seki. */
  private type Owner = Option[Boolean]

  def calculate(): TerminalScore = {
    clearLifeFlags()

    val dead = mutable.Set.empty[(Int, Int)]
    var changed = true
    var guard = 0
    while (changed && guard < rows * cols) {
      guard += 1
      changed = false
      val regions = findRegions(dead)
      val ownership = regions.map(r => r -> regionOwner(r, dead)).toMap
      val victims = livingStrings(dead).filter(s => isDeadString(s, dead, regions, ownership)).toList
      if (victims.nonEmpty) {
        // Fewer eyes, then fewer liberties. Removing every dead string in one pass
        // would also remove a group that lives once the weaker group is gone.
        val victim = victims.minBy { s =>
          val libs = scoringLiberties(s, dead)
          val eyes = eyeRegionsOf(s.isOwnedByPlayer1, libs, dead, regions, ownership)
          val eyeScore = if (eyes.exists(countsAsTwoEyes)) 2 else eyes.size
          val pos = s.getMembers.iterator().next()
          // Fewer eyes, then fewer liberties. A one-eyed group lives once a
          // no-eye neighbor with the same liberty count is removed.
          (eyeScore, libs.size, pos.getRow, pos.getCol)
        }
        victim.getMembers.asScala.foreach { pos =>
          dead += ((pos.getRow, pos.getCol))
        }
        changed = true
      }
    }

    applyDeadMarks(dead)
    val regions = findRegions(dead)
    val territory = countTerritory(regions, dead)
    TerminalScore(
      blackTerritory = territory._1,
      whiteTerritory = territory._2,
      deadBlack = dead.count { case (r, c) => isBlackStone(r, c) },
      deadWhite = dead.count { case (r, c) => isWhiteStone(r, c) }
    )
  }

  private def clearLifeFlags(): Unit = {
    for (r <- 1 to rows; c <- 1 to cols) {
      val pos = board.getPosition(r, c).asInstanceOf[GoBoardPosition]
      if (pos.isOccupied) {
        pos.getPiece.asInstanceOf[GoStone].setDead(false)
        val str = pos.getString
        if (str != null) {
          str.setUnconditionallyAlive(false)
        }
      }
    }
  }

  private def applyDeadMarks(dead: mutable.Set[(Int, Int)]): Unit = {
    for ((r, c) <- dead) {
      val pos = board.getPosition(r, c).asInstanceOf[GoBoardPosition]
      if (pos.isOccupied) {
        pos.getPiece.asInstanceOf[GoStone].setDead(true)
      }
    }
  }

  private def isBlackStone(r: Int, c: Int): Boolean = {
    val pos = board.getPosition(r, c).asInstanceOf[GoBoardPosition]
    pos.isOccupied && pos.getPiece.isOwnedByPlayer1
  }

  private def isWhiteStone(r: Int, c: Int): Boolean = {
    val pos = board.getPosition(r, c).asInstanceOf[GoBoardPosition]
    pos.isOccupied && !pos.getPiece.isOwnedByPlayer1
  }

  private def isScoringEmpty(r: Int, c: Int, dead: mutable.Set[(Int, Int)]): Boolean = {
    val pos = board.getPosition(r, c).asInstanceOf[GoBoardPosition]
    pos.isUnoccupied || dead.contains((r, c))
  }

  private def livingStrings(dead: mutable.Set[(Int, Int)]): Iterable[IGoString] = {
    val seen = mutable.Set.empty[IGoString]
    val result = mutable.ListBuffer.empty[IGoString]
    for (r <- 1 to rows; c <- 1 to cols) {
      if (!dead.contains((r, c))) {
        val pos = board.getPosition(r, c).asInstanceOf[GoBoardPosition]
        if (pos.isOccupied) {
          val str = pos.getString
          if (str != null && seen.add(str)) {
            result += str
          }
        }
      }
    }
    result
  }

  private def findRegions(dead: mutable.Set[(Int, Int)]): List[List[(Int, Int)]] = {
    val visited = Array.ofDim[Boolean](rows + 1, cols + 1)
    val regions = mutable.ListBuffer.empty[List[(Int, Int)]]

    for (r <- 1 to rows; c <- 1 to cols) {
      if (!visited(r)(c) && isScoringEmpty(r, c, dead)) {
        val region = mutable.ListBuffer.empty[(Int, Int)]
        val stack = mutable.Stack((r, c))
        visited(r)(c) = true
        while (stack.nonEmpty) {
          val (cr, cc) = stack.pop()
          region += ((cr, cc))
          for ((nr, nc) <- orthNeighbors(cr, cc)) {
            if (!visited(nr)(nc) && isScoringEmpty(nr, nc, dead)) {
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

  private def regionOwner(region: List[(Int, Int)], dead: mutable.Set[(Int, Int)]): Owner = {
    var touchesBlack = false
    var touchesWhite = false
    for ((r, c) <- region; (nr, nc) <- orthNeighbors(r, c)) {
      if (!dead.contains((nr, nc))) {
        val pos = board.getPosition(nr, nc).asInstanceOf[GoBoardPosition]
        if (pos.isOccupied) {
          if (pos.getPiece.isOwnedByPlayer1) touchesBlack = true
          else touchesWhite = true
        }
      }
    }
    if (touchesBlack && !touchesWhite) Some(true)
    else if (touchesWhite && !touchesBlack) Some(false)
    else None
  }

  private def isDeadString(
    string: IGoString,
    dead: mutable.Set[(Int, Int)],
    regions: List[List[(Int, Int)]],
    ownership: Map[List[(Int, Int)], Owner]
  ): Boolean = {
    val colorBlack = string.isOwnedByPlayer1
    val libs = scoringLiberties(string, dead)
    if (libs.isEmpty) {
      return true
    }
    if (hasTwoEyes(colorBlack, libs, dead, regions, ownership)) {
      return false
    }
    if (inMutualSeki(libs, colorBlack, dead, regions, ownership)) {
      return false
    }
    // A shared region of seven or more points is room to live for an eyed group or one with three liberties.
    if (hasOpenDame(colorBlack, libs, dead, regions, ownership)) {
      return false
    }
    true
  }

  /**
   * Dame with at least this many real empty points (not dead stones) is room to live.
   * Points under stones already judged dead do not count: a last stone beside a
   * pile of dead friendly stones is still dead.
   */
  private val OpenDameSize = 7

  /** Below this many liberties, a group with no eye cannot use a shared region. */
  private val OpenDameMinLiberties = 3

  private def hasOpenDame(
    colorBlack: Boolean,
    libs: Set[(Int, Int)],
    dead: mutable.Set[(Int, Int)],
    regions: List[List[(Int, Int)]],
    ownership: Map[List[(Int, Int)], Owner]
  ): Boolean = {
    val canUseTheSpace = libs.size >= OpenDameMinLiberties ||
      eyeRegionsOf(colorBlack, libs, dead, regions, ownership).nonEmpty
    canUseTheSpace && regions.exists { region =>
      ownership(region).isEmpty &&
        region.exists(libs.contains) &&
        region.count(pt => !dead.contains(pt)) >= OpenDameSize
    }
  }

  /** Two real eyes, or one eye shape that lives no matter who plays first. */
  private def hasTwoEyes(
    colorBlack: Boolean,
    libs: Set[(Int, Int)],
    dead: mutable.Set[(Int, Int)],
    regions: List[List[(Int, Int)]],
    ownership: Map[List[(Int, Int)], Owner]
  ): Boolean = {
    val eyeRegions = eyeRegionsOf(colorBlack, libs, dead, regions, ownership)
    eyeRegions.size >= 2 || eyeRegions.exists(countsAsTwoEyes)
  }

  private def eyeRegionsOf(
    colorBlack: Boolean,
    libs: Set[(Int, Int)],
    dead: mutable.Set[(Int, Int)],
    regions: List[List[(Int, Int)]],
    ownership: Map[List[(Int, Int)], Owner]
  ): List[List[(Int, Int)]] = {
    val adjRegions = regions.filter(region => region.exists(libs.contains))
    adjRegions.filter { region =>
      ownership(region).contains(colorBlack) && !isFalseEyeRegion(region, colorBlack, dead)
    }
  }

  /**
   * Mutual life: both strings have no two eyes and neither has a liberty the other does not.
   * A dame point touched by a group that also has eyes of its own is not seki.
   */
  private def inMutualSeki(
    libs: Set[(Int, Int)],
    colorBlack: Boolean,
    dead: mutable.Set[(Int, Int)],
    regions: List[List[(Int, Int)]],
    ownership: Map[List[(Int, Int)], Owner]
  ): Boolean = {
    if (libs.size < 2) {
      return false
    }
    livingStrings(dead).exists { other =>
      other.isOwnedByPlayer1 != colorBlack && {
        val otherLibs = scoringLiberties(other, dead)
        otherLibs == libs && !hasTwoEyes(!colorBlack, otherLibs, dead, regions, ownership)
      }
    }
  }

  /** One exclusive region of three or more empty points can be made into two eyes. */
  private def countsAsTwoEyes(region: List[(Int, Int)]): Boolean =
    region.size >= 3

  /** A one-point eye cut by opponent diagonals is not a real eye. */
  private def isFalseEyeRegion(
    region: List[(Int, Int)],
    ownerBlack: Boolean,
    dead: mutable.Set[(Int, Int)]
  ): Boolean = {
    region.size == 1 && isFalseEyePoint(region.head, ownerBlack, dead)
  }

  private def isFalseEyePoint(
    pt: (Int, Int),
    ownerBlack: Boolean,
    dead: mutable.Set[(Int, Int)]
  ): Boolean = {
    val (r, c) = pt
    val friends = orthNeighbors(r, c).count { case (nr, nc) =>
      friendlyStone(nr, nc, ownerBlack, dead)
    }
    // A corner point with both adjacent stones friendly is a real eye.
    // An edge or center point needs three friendly neighbors before a diagonal cut falsifies it.
    if (friends < 3) {
      return false
    }
    val cuts = List((-1, -1), (-1, 1), (1, -1), (1, 1)).count { case (dr, dc) =>
      opponentCutsDiagonal(r, c, dr, dc, ownerBlack, dead)
    }
    val onEdge = r == 1 || c == 1 || r == rows || c == cols
    (onEdge && cuts >= 1) || cuts >= 2
  }

  private def opponentCutsDiagonal(
    r: Int,
    c: Int,
    dr: Int,
    dc: Int,
    ownerBlack: Boolean,
    dead: mutable.Set[(Int, Int)]
  ): Boolean = {
    val rr = r + dr
    val cc = c + dc
    inBounds(rr, cc) &&
      enemyStone(rr, cc, ownerBlack, dead) &&
      friendlyStone(r + dr, c, ownerBlack, dead) &&
      friendlyStone(r, c + dc, ownerBlack, dead)
  }

  private def friendlyStone(r: Int, c: Int, ownerBlack: Boolean, dead: mutable.Set[(Int, Int)]): Boolean = {
    inBounds(r, c) && !dead.contains((r, c)) && occupiedBy(r, c, ownerBlack)
  }

  private def enemyStone(r: Int, c: Int, ownerBlack: Boolean, dead: mutable.Set[(Int, Int)]): Boolean = {
    inBounds(r, c) && !dead.contains((r, c)) && occupiedBy(r, c, !ownerBlack)
  }

  private def occupiedBy(r: Int, c: Int, black: Boolean): Boolean = {
    val pos = board.getPosition(r, c).asInstanceOf[GoBoardPosition]
    pos.isOccupied && pos.getPiece.isOwnedByPlayer1 == black
  }

  private def inBounds(r: Int, c: Int): Boolean = r >= 1 && r <= rows && c >= 1 && c <= cols

  private def scoringLiberties(string: IGoString, dead: mutable.Set[(Int, Int)]): Set[(Int, Int)] = {
    val libs = mutable.Set.empty[(Int, Int)]
    for (stone <- string.getMembers.asScala; (nr, nc) <- orthNeighbors(stone.getRow, stone.getCol)) {
      if (isScoringEmpty(nr, nc, dead)) {
        libs += ((nr, nc))
      }
    }
    libs.toSet
  }

  private def countTerritory(
    regions: List[List[(Int, Int)]],
    dead: mutable.Set[(Int, Int)]
  ): (Int, Int) = {
    var black = 0
    var white = 0
    for (region <- regions) {
      regionOwner(region, dead) match {
        case Some(true) => black += region.size
        case Some(false) => white += region.size
        case None =>
      }
    }
    (black, white)
  }

  private def orthNeighbors(r: Int, c: Int): List[(Int, Int)] = {
    val buf = mutable.ListBuffer.empty[(Int, Int)]
    if (r > 1) buf += ((r - 1, c))
    if (r < rows) buf += ((r + 1, c))
    if (c > 1) buf += ((r, c - 1))
    if (c < cols) buf += ((r, c + 1))
    buf.toList
  }
}
