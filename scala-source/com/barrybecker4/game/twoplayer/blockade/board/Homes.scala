// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.common.board.{BoardPosition, GamePiece}

object Homes:
  val NUM_HOMES = 2
  private val HOME_BASE_POSITION_PERCENT = 0.3f

class Homes:
  private var p1Homes: Array[BoardPosition] = _
  private var p2Homes: Array[BoardPosition] = _

  def this(b: Homes) =
    this()
    p1Homes = b.p1Homes.clone()
    p2Homes = b.p2Homes.clone()

  def getPlayerHomes(player1: Boolean): Array[BoardPosition] =
    if player1 then p1Homes else p2Homes

  def init(numRows: Int, numCols: Int): Unit =
    p1Homes = new Array[BoardPosition](Homes.NUM_HOMES)
    p2Homes = new Array[BoardPosition](Homes.NUM_HOMES)

    val homeRow1 = numRows - (Homes.HOME_BASE_POSITION_PERCENT * numRows).toInt + 1
    val homeRow2 = (Homes.HOME_BASE_POSITION_PERCENT * numRows).toInt
    val increment = numCols.toFloat / (Homes.NUM_HOMES + 1)
    val baseOffset = Math.round(increment)

    for i <- 0 until Homes.NUM_HOMES do
      val c = baseOffset + Math.round(i * increment)
      
      p1Homes(i) = new BlockadeBoardPosition(ByteLocation(homeRow1.toByte, c.toByte), null, null, null, true, false)
      p1Homes(i).setPiece(GamePiece(true))
      p2Homes(i) = new BlockadeBoardPosition(ByteLocation(homeRow2.toByte, c.toByte), null, null, null, false, true)
      p2Homes(i).setPiece(GamePiece(false))
