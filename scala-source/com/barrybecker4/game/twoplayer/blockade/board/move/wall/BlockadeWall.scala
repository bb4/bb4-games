/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.board.move.wall

import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition
import scala.collection.mutable
import scala.jdk.CollectionConverters.*

class BlockadeWall(val positions: mutable.LinkedHashSet[BlockadeBoardPosition]):
  
  private val isVertical: Boolean = 
    val it = positions.iterator
    val p1 = it.next()
    val p2 = it.next()
    val vertical = p1.getCol == p2.getCol
    assert(p1.getCol == p2.getCol || p1.getRow == p2.getRow, "impossible wall!")
    vertical

  def this(positions: java.util.Set[BlockadeBoardPosition]) =
    this(mutable.LinkedHashSet.from(positions.asScala))

  def this(p1: BlockadeBoardPosition, p2: BlockadeBoardPosition) =
    this(mutable.LinkedHashSet(p1, p2))

  override def equals(obj: Any): Boolean = obj match
    case null => false
    case wall: BlockadeWall =>
      this.isVertical == wall.isVertical &&
        this.getFirstPosition.getLocation.equals(wall.getFirstPosition.getLocation)
    case _ => false

  override def hashCode(): Int =
    positions.foldLeft(0)((acc, pos) => acc + pos.hashCode())

  def getIsVertical: Boolean = isVertical

  def getPositions: java.util.Set[BlockadeBoardPosition] =
    positions.asJava

  def getFirstPosition: BlockadeBoardPosition =
    val it = positions.iterator
    val pos1 = it.next()
    val pos2 = it.next()
    if isVertical then
      if pos1.getRow < pos2.getRow then pos1 else pos2
    else
      if pos1.getCol < pos2.getCol then pos1 else pos2

  override def toString: String =
    val buf = new StringBuilder(s"wall: ${if isVertical then "V" else "H"}")
    for pos <- positions do
      buf.append(pos.toString).append(if isVertical then "eastBlocked " else "southBlocked ")
    buf.toString()
