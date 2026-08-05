/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.ui.rendering

import com.barrybecker4.ui.util.ColorMap
import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.board.BoardPosition
import com.barrybecker4.game.twoplayer.go.board.{BoardValidator, GoBoard}
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzer
import com.barrybecker4.game.twoplayer.go.board.analysis.neighbor.NeighborAnalyzer
import com.barrybecker4.game.twoplayer.go.board.elements.eye.{GoEyeSet, IGoEye}
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionList, GoBoardPositionSet}
import com.barrybecker4.ui.util.GUIUtil
import java.awt.{Color, Font, Graphics2D}
import java.awt.geom.{Area, Ellipse2D, GeneralPath, Point2D}
import scala.collection.mutable
import scala.jdk.CollectionConverters._

/**
 *  Static Utility methods for rendering a GoGroup.
 *  @author Barry Becker
 */
final class GoGroupRenderer(
  private val board_ : GoBoard,
  private val colormap_ : ColorMap,
  private val cellSize_ : Float,
  private val margin_ : Int,
  private val g2_ : Graphics2D
) {

  def drawGroupDecoration(groupAnalyzer: GroupAnalyzer): Unit = {
    val group = groupAnalyzer.getGroup
    var cachedRegion = GoGroupRenderer.hmRegionCache_.get(group)

    if (!groupAnalyzer.isValid || cachedRegion.isEmpty || cellSize_ != cachedRegion.get.cellSize) {
      var h = groupAnalyzer.getRelativeHealth(board_, true)
      if (!group.isOwnedByPlayer1) {
        h = -h
      }

      val region = new GoGroupRenderer.GroupRegion()
      region.borderArea = calcGroupBorder(group.getStones)
      region.borderColor = colormap_.getColorForValue(h)
      region.cellSize = cellSize_

      GoGroupRenderer.hmRegionCache_.put(group, region)
      cachedRegion = Some(region)
    }

    fillInRegion(cachedRegion.get)

    if (!groupAnalyzer.getEyes(board_).isEmpty) {
      drawEyes(groupAnalyzer.getEyes(board_))
    }
  }

  private def calcGroupBorder(groupStones: GoBoardPositionSet): Area = {
    if (groupStones == null || groupStones.isEmpty) {
      return null
    }

    val firstStone = groupStones.iterator.next

    if (groupStones.size == 1) {
      return createSingleStoneBorder(firstStone.getLocation)
    }

    createMultiStoneBorder(firstStone.getLocation)
  }

  private def createSingleStoneBorder(firstStoneLoc: Location): Area = {
    val offset = GoGroupRenderer.BORDER_OFFSET + 0.5f
    val x = margin_ + (firstStoneLoc.col - offset) * cellSize_
    val y = margin_ + (firstStoneLoc.row - offset) * cellSize_
    new Area(new Ellipse2D.Float(x, y, cellSize_, cellSize_))
  }

  private def createMultiStoneBorder(firstStoneLoc: Location): Area = {
    val boardCopy = board_.copy()

    val q = mutable.ArrayBuffer[BoardPosition]()
    val qset = new GoBoardPositionSet()
    val visitedSet = new GoBoardPositionList()

    val firstStone = boardCopy.getPosition(firstStoneLoc).asInstanceOf[GoBoardPosition]
    q += firstStone
    qset.add(firstStone)
    val area = new Area()
    val nbrAnalyzer = new NeighborAnalyzer(boardCopy)

    while (q.nonEmpty) {
      val stone = q.remove(0).asInstanceOf[GoBoardPosition]
      qset.remove(stone)
      stone.setVisited(true)
      visitedSet.add(stone)
      val nbrs = nbrAnalyzer.findGroupNeighbors(stone, true)
      for (nbrStone <- nbrs.asScala) {
        area.add(new Area(getBorderBetween(stone, nbrStone)))
        if (!nbrStone.isVisited && !qset.contains(nbrStone)) {
          q += nbrStone
          qset.add(nbrStone)
        }
      }
    }
    visitedSet.unvisitPositions()
    if (GameContext.getDebugMode > 1) {
      new BoardValidator(boardCopy).confirmAllUnvisited()
    }
    area
  }

  private def getBorderBetween(s1: GoBoardPosition, s2: GoBoardPosition): GeneralPath = {
    val dist = s1.getDistanceFrom(s2)
    var border: GeneralPath = null

    if (dist == 1.0 || dist == 2.0) {
      border = getLinearNbrBorder(s1, s2)
    } else if ((dist - Math.sqrt(2.0)) < 0.001) {
      border = getDiagonalNbrBorder(s1, s2)
    } else if ((dist - Math.sqrt(5.0)) < 0.001) {
      border = getKogeimaNbrBorder(s1, s2)
    } else {
      assert(false, s"error! dist=$dist")
    }
    assert(border != null, s"the border was null. dist=$dist")

    border
  }

  private def getLinearNbrBorder(s1: GoBoardPosition, s2: GoBoardPosition): GeneralPath = {
    val border = new GeneralPath()
    val celld2 = cellSize_ / 2.0f

    if (s1.getRow == s2.getRow) {
      val (leftStone, rightStone) = if (s1.getCol < s2.getCol) (s1, s2) else (s2, s1)
      val xleft = margin_ + (leftStone.getCol.toFloat - GoGroupRenderer.BORDER_OFFSET) * cellSize_
      val yleft = margin_ + (leftStone.getRow.toFloat - GoGroupRenderer.BORDER_OFFSET) * cellSize_
      val xright = margin_ + (rightStone.getCol.toFloat - GoGroupRenderer.BORDER_OFFSET) * cellSize_
      val yright = margin_ + (rightStone.getRow.toFloat - GoGroupRenderer.BORDER_OFFSET) * cellSize_
      border.moveTo(xleft, yleft + celld2)
      border.quadTo(xleft - celld2, yleft + celld2, xleft - celld2, yleft)
      border.quadTo(xleft - celld2, yleft - celld2, xleft, yleft - celld2)
      border.lineTo(xright, yright - celld2)
      border.quadTo(xright + celld2, yright - celld2, xright + celld2, yright)
      border.quadTo(xright + celld2, yright + celld2, xright, yright + celld2)
      border.closePath()
    } else {
      val (topStone, bottomStone) = if (s1.getRow < s2.getRow) (s1, s2) else (s2, s1)
      val xtop = margin_ + (topStone.getCol.toFloat - GoGroupRenderer.BORDER_OFFSET) * cellSize_
      val ytop = margin_ + (topStone.getRow.toFloat - GoGroupRenderer.BORDER_OFFSET) * cellSize_
      val xbottom = margin_ + (bottomStone.getCol.toFloat - GoGroupRenderer.BORDER_OFFSET) * cellSize_
      val ybottom = margin_ + (bottomStone.getRow.toFloat - GoGroupRenderer.BORDER_OFFSET) * cellSize_
      border.moveTo(xtop - celld2, ytop)
      border.quadTo(xtop - celld2, ytop - celld2, xtop, ytop - celld2)
      border.quadTo(xtop + celld2, ytop - celld2, xtop + celld2, ytop)
      border.lineTo(xbottom + celld2, ybottom)
      border.quadTo(xbottom + celld2, ybottom + celld2, xbottom, ybottom + celld2)
      border.quadTo(xbottom - celld2, ybottom + celld2, xbottom - celld2, ybottom)
      border.closePath()
    }
    border
  }

  private def getDiagonalNbrBorder(s1: GoBoardPosition, s2: GoBoardPosition): GeneralPath = {
    val border = new GeneralPath()
    val celld2 = cellSize_ / 2.0f

    var ulStone: GoBoardPosition = null
    var lrStone: GoBoardPosition = null
    var llStone: GoBoardPosition = null
    var urStone: GoBoardPosition = null

    if (s1.getRow < s2.getRow) {
      if (s1.getCol < s2.getCol) {
        ulStone = s1
        lrStone = s2
      } else {
        llStone = s2
        urStone = s1
      }
    } else {
      if (s1.getCol < s2.getCol) {
        llStone = s1
        urStone = s2
      } else {
        lrStone = s1
        ulStone = s2
      }
    }

    if (urStone == null) {
      val ulx = margin_ + (ulStone.getCol.toFloat - GoGroupRenderer.BORDER_OFFSET) * cellSize_
      val uly = margin_ + (ulStone.getRow.toFloat - GoGroupRenderer.BORDER_OFFSET) * cellSize_
      val lrx = margin_ + (lrStone.getCol.toFloat - GoGroupRenderer.BORDER_OFFSET) * cellSize_
      val lry = margin_ + (lrStone.getRow.toFloat - GoGroupRenderer.BORDER_OFFSET) * cellSize_
      border.moveTo(ulx, uly + celld2)
      border.quadTo(ulx - celld2, uly + celld2, ulx - celld2, uly)
      border.quadTo(ulx - celld2, uly - celld2, ulx, uly - celld2)
      border.quadTo(ulx + celld2, uly - celld2, ulx + celld2, uly)
      border.quadTo(ulx + celld2, uly + celld2, lrx, lry - celld2)
      border.quadTo(lrx + celld2, lry - celld2, lrx + celld2, lry)
      border.quadTo(lrx + celld2, lry + celld2, lrx, lry + celld2)
      border.quadTo(lrx - celld2, lry + celld2, lrx - celld2, lry)
      border.quadTo(ulx + celld2, lry - celld2, ulx, uly + celld2)
      border.closePath()
    } else {
      val llx = margin_ + (llStone.getCol.toFloat - GoGroupRenderer.BORDER_OFFSET) * cellSize_
      val lly = margin_ + (llStone.getRow.toFloat - GoGroupRenderer.BORDER_OFFSET) * cellSize_
      val urx = margin_ + (urStone.getCol.toFloat - GoGroupRenderer.BORDER_OFFSET) * cellSize_
      val ury = margin_ + (urStone.getRow.toFloat - GoGroupRenderer.BORDER_OFFSET) * cellSize_
      border.moveTo(llx + celld2, lly)
      border.quadTo(llx + celld2, lly + celld2, llx, lly + celld2)
      border.quadTo(llx - celld2, lly + celld2, llx - celld2, lly)
      border.quadTo(llx - celld2, lly - celld2, llx, lly - celld2)
      border.quadTo(llx + celld2, lly - celld2, urx - celld2, ury)
      border.quadTo(urx - celld2, ury - celld2, urx, ury - celld2)
      border.quadTo(urx + celld2, ury - celld2, urx + celld2, ury)
      border.quadTo(urx + celld2, ury + celld2, urx, ury + celld2)
      border.quadTo(urx - celld2, ury + celld2, llx + celld2, lly)
      border.closePath()
    }

    border
  }

  private def getKogeimaNbrBorder(s1Stone: GoBoardPosition, s2Stone: GoBoardPosition): GeneralPath = {
    val border = new GeneralPath()
    val celld2 = cellSize_ / 2.0f

    val p = new Point2D.Float(celld2 * (s2Stone.getCol - s1Stone.getCol),
      celld2 * (s2Stone.getRow - s1Stone.getRow))
    val s1x = margin_ + (s1Stone.getCol.toFloat - GoGroupRenderer.BORDER_OFFSET) * cellSize_
    val s1y = margin_ + (s1Stone.getRow.toFloat - GoGroupRenderer.BORDER_OFFSET) * cellSize_
    val s2x = margin_ + (s2Stone.getCol.toFloat - GoGroupRenderer.BORDER_OFFSET) * cellSize_
    val s2y = margin_ + (s2Stone.getRow.toFloat - GoGroupRenderer.BORDER_OFFSET) * cellSize_

    if (Math.abs(p.x) > Math.abs(p.y)) {
      p.x /= 2.0f
      p.y = -p.y
      border.moveTo(s1x, s1y + p.y)
      border.quadTo(s1x - p.x, s1y + p.y, s1x - p.x, s1y)
      border.quadTo(s1x - p.x, s1y - p.y, s1x, s1y - p.y)
      border.lineTo(s2x, s2y + p.y)
      border.quadTo(s2x + p.x, s2y + p.y, s2x + p.x, s2y)
      border.quadTo(s2x + p.x, s2y - p.y, s2x, s2y - p.y)
      border.quadTo(s2x - p.x, s2y - p.y, s2x - p.x, s2y)
      border.quadTo(s2x - p.x, s2y + p.y, s2x - 2.0f * p.x, s2y + p.y)
      border.quadTo(s1x + p.x, s1y - p.y, s1x + p.x, s1y)
      border.quadTo(s1x + p.x, s1y + p.y, s1x, s1y + p.y)
      border.closePath()
    } else {
      p.y /= 2.0f
      border.moveTo(s1x - p.x, s1y)
      border.quadTo(s1x - p.x, s1y - p.y, s1x, s1y - p.y)
      border.quadTo(s1x + p.x, s1y - p.y, s1x + p.x, s1y)
      border.lineTo(s1x + p.x, s1y + 2.0f * p.y)
      border.quadTo(s2x - p.x, s2y - p.y, s2x, s2y - p.y)
      border.quadTo(s2x + p.x, s2y - p.y, s2x + p.x, s2y)
      border.quadTo(s2x + p.x, s2y + p.y, s2x, s2y + p.y)
      border.quadTo(s2x - p.x, s2y + p.y, s2x - p.x, s2y)
      border.lineTo(s2x - p.x, s2y - 2.0f * p.y)
      border.quadTo(s1x + p.x, s1y + p.y, s1x, s1y + p.y)
      border.quadTo(s1x - p.x, s1y + p.y, s1x - p.x, s1y)
      border.closePath()
    }
    border
  }

  private def drawEyes(eyes: GoEyeSet): Unit = {
    if (!eyes.isEmpty) {
      val font = new Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.PLAIN, (1.6 * Math.sqrt(cellSize_) - 1).toInt)
      g2_.setFont(font)
      g2_.setColor(GoGroupRenderer.EYE_TEXT_COLOR)

      for (eye <- eyes.asScala) {
        val eyeName = eye.getEyeTypeName
        for (eyeSpace <- eye.getMembers.asScala) {
          val x = margin_ + (eyeSpace.getCol.toFloat - GoGroupRenderer.BORDER_OFFSET - 0.5f) * cellSize_
          val y = margin_ + (eyeSpace.getRow.toFloat - GoGroupRenderer.BORDER_OFFSET + 0.1f) * cellSize_
          g2_.drawString(eyeName, x, y)
        }
      }
    }
  }

  private def fillInRegion(region: GoGroupRenderer.GroupRegion): Unit = {
    if (region != null && region.borderArea != null) {
      g2_.setColor(region.borderColor)
      g2_.fill(region.borderArea)
      g2_.setColor(Color.black)
      g2_.draw(region.borderArea)
    }
  }
}

object GoGroupRenderer {
  private val BORDER_OFFSET = 0.5f
  private val EYE_TEXT_COLOR = new Color(30, 10, 10)
  private val hmRegionCache_ = mutable.HashMap[IGoGroup, GroupRegion]()

  private class GroupRegion {
    var borderArea: Area = _
    var borderColor: Color = _
    var cellSize: Float = _
  }
}
