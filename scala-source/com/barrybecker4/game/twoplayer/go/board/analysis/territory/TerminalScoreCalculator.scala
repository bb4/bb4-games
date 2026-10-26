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
 * A string is dead when it is enclosed by the opponent and cannot make two eyes:
 * fewer than two exclusive-eye regions, no single eye large enough to become two eyes,
 * and no dame liberties. Iteration continues until the set of dead stones is stable.
 */
class TerminalScoreCalculator(board: GoBoard) {

  private val rows = board.getNumRows
  private val cols = board.getNumCols

  /** Minimum empty points in one exclusive region to count as able to make two eyes. */
  private val BigEyeSize = 3

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
      for (string <- livingStrings(dead)) {
        if (isDeadString(string, dead, regions, ownership)) {
          string.getMembers.asScala.foreach { pos =>
            dead += ((pos.getRow, pos.getCol))
          }
          changed = true
        }
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
    val opponent = !colorBlack
    val libs = scoringLiberties(string, dead)
    if (libs.isEmpty) {
      return true
    }

    val adjRegions = regions.filter(region => region.exists(libs.contains))
    val eyeRegions = adjRegions.filter(r => ownership(r).contains(colorBlack))
    val hasBigEye = eyeRegions.exists(_.size >= BigEyeSize)
    if (eyeRegions.size >= 2 || hasBigEye) {
      return false
    }

    // Seki: a dame liberty that an under-eyed opponent string also needs.
    val inSeki = adjRegions.exists { region =>
      ownership(region).isEmpty && opponentDependsOnDame(region, opponent, dead, regions, ownership)
    }
    if (inSeki) {
      return false
    }

    // Surrounded: every liberty is own small eye, opponent territory, or non-seki dame touching opponent.
    val surrounded = libs.forall { pt =>
      adjRegions.find(_.contains(pt)) match {
        case None => true
        case Some(region) =>
          ownership(region).contains(colorBlack) ||
            ownership(region).contains(opponent) ||
            ownership(region).isEmpty
      }
    }
    surrounded && eyeRegions.size < 2
  }

  /**
   * True if some living opponent string with fewer than two eyes also has a liberty in this dame region.
   */
  private def opponentDependsOnDame(
    region: List[(Int, Int)],
    opponentIsBlack: Boolean,
    dead: mutable.Set[(Int, Int)],
    regions: List[List[(Int, Int)]],
    ownership: Map[List[(Int, Int)], Owner]
  ): Boolean = {
    val regionSet = region.toSet
    livingStrings(dead).exists { other =>
      other.isOwnedByPlayer1 == opponentIsBlack && {
        val otherLibs = scoringLiberties(other, dead)
        otherLibs.exists(regionSet.contains) && {
          val otherAdj = regions.filter(r => r.exists(otherLibs.contains))
          val otherEyes = otherAdj.filter(r => ownership(r).contains(opponentIsBlack))
          otherEyes.size < 2 && !otherEyes.exists(_.size >= BigEyeSize)
        }
      }
    }
  }

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
