/* Copyright by Barry G. Becker, 2007-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.board.path

import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove
import com.barrybecker4.game.twoplayer.blockade.board.move.MovePlacementValidator
import com.barrybecker4.game.twoplayer.blockade.board.move.wall.BlockadeWall
import javax.swing.tree.DefaultMutableTreeNode
import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters.*

class Path private (private val moves: ListBuffer[BlockadeMove]):

  def this() =
    this(ListBuffer.empty)

  def this(node: DefaultMutableTreeNode) =
    this()
    addPathElements(node)

  def this(moves: Array[BlockadeMove]) =
    this()
    for m <- moves do
      add(m)

  def add(move: BlockadeMove): Unit =
    moves.addOne(move)

  def iterator: java.util.Iterator[BlockadeMove] =
    moves.iterator.asJava

  def get(index: Int): BlockadeMove =
    moves(index)

  def isBlockedByWall(wall: BlockadeWall, board: BlockadeBoard): Boolean =
    val validator = MovePlacementValidator(board)
    moves.exists(move => validator.isMoveBlockedByWall(move, wall))

  def isBlocked(board: BlockadeBoard): Boolean =
    val validator = MovePlacementValidator(board)
    moves.exists(move => validator.isMoveBlocked(move))

  private def addPathElements(node: DefaultMutableTreeNode): Unit =
    val ps = node.getUserObjectPath
    if ps.length > 1 then
      for k <- 1 until ps.length do
        add(ps(k).asInstanceOf[BlockadeMove])

  def getLength: Int = moves.size

  def getPathLength: Int =
    moves.foldLeft(0)((total, move) => 
      total + move.getToLocation.getDistanceFrom(move.getFromLocation).toInt
    )

  override def equals(obj: Any): Boolean = obj match
    case path: Path =>
      if path.getLength != this.getLength then
        return false
      moves.zipWithIndex.forall { case (move, i) =>
        move.equals(path.get(i))
      }
    case _ => false

  override def hashCode(): Int =
    val hash = this.getLength * 100000
    hash + moves.foldLeft(0)((acc, move) => acc + move.hashCode() / 20)

  override def toString: String =
    if moves.isEmpty then "Path has 0 magnitude"
    else
      val bldr = new StringBuilder(32)
      for move <- moves do
        bldr.append('[').append(move.toString).append("],")
      bldr.deleteCharAt(bldr.length - 1)
      bldr.append("\n")
      bldr.toString()
