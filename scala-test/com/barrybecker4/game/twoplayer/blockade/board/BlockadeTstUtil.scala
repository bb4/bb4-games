// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board

import com.barrybecker4.common.geometry.{ByteLocation, Location}
import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove
import com.barrybecker4.game.twoplayer.blockade.board.move.wall.BlockadeWall
import com.barrybecker4.game.twoplayer.blockade.board.path.{Path, PathList}

import scala.jdk.CollectionConverters.*

/** Static methods to help with Blockade testing. @author Barry Becker */
object BlockadeTstUtil:

  def createMove(r1: Int, c1: Int, r2: Int, c2: Int, value: Int, piece: GamePiece, wall: BlockadeWall): BlockadeMove =
    new BlockadeMove(new ByteLocation(r1, c1), new ByteLocation(r2, c2), value, piece, wall)

  def createMove(r1: Int, c1: Int, r2: Int, c2: Int): BlockadeMove =
    new BlockadeMove(new ByteLocation(r1, c1), new ByteLocation(r2, c2), 0, new GamePiece(true), null)

  @scala.annotation.varargs
  def createPath(locations: Location*): Path =
    val moves = new Array[BlockadeMove](locations.length - 1)
    var lastMove: Location = null
    var ct = 0
    for loc <- locations do
      if lastMove != null then
        moves(ct) = new BlockadeMove(lastMove, loc, 0, null, null)
        ct += 1
      lastMove = loc
    new Path(moves)

  def getConstructorString(paths: PathList): String =
    val bldr = new StringBuilder("new PathList(\n    new Path[] {\n")
    for path <- paths.asScala do
      bldr.append("    ").append(getConstructorString(path))
    bldr.append("}),\n")
    bldr.toString

  def getConstructorString(path: Path): String =
    val bldr = new StringBuilder("    new Path(new BlockadeMove[] {\n")
    val iter = path.iterator
    while iter.hasNext do
      val move = iter.next()
      bldr.append("            ").append(move.getConstructorString).append("\n")
    bldr.append("        }),\n")
    bldr.toString
