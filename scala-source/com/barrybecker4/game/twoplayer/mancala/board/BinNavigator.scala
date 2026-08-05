package com.barrybecker4.game.twoplayer.mancala.board

import com.barrybecker4.common.geometry.{ByteLocation, Location}
import scala.collection.mutable

/**
  * Creates a mapping between the boards grid and the mancala bin positions for purposes of navigation.
  *
  * The grid positions are something like
  * 1,1  1,2  1,3  1,4  1,5  1,6  1,7  1,8
  *      2,2  2,3  2,4  2,5  2,6  2,7
  *
  * Where 1,1 and 1,8 are player 1 and 2 homes respectively.
  * For purposes of navigation, the order is
  *  7    6    5     4    3    2    1   0
  *       8    9    10   11   12   13
  *
  * and from 13 back to 0
  *
  * @author Barry Becker
  */
class BinNavigator(size: Int):

  private val nextMap = mutable.Map[Location, Location]()

  for i <- 0 until size - 1 do
    nextMap.put(ByteLocation(1, (size - i).toByte), ByteLocation(1, (size - i - 1).toByte))
  nextMap.put(ByteLocation(1, 1.toByte), ByteLocation(2, 2.toByte))

  for i <- 2 until size do
    nextMap.put(ByteLocation(2, i.toByte), ByteLocation(2, (i + 1).toByte))
  nextMap.put(ByteLocation(2, (size - 1).toByte), ByteLocation(1, size.toByte))

  def getNextLocation(loc: Location): Location =
    assert(loc != null)
    val next = nextMap(loc)
    assert(next != null, s"could not find $loc in $nextMap")
    next

  /**
    * @param loc the location to look opposite of
    * @return the location that is opposite the one specified
    */
  def getOppositeLocation(loc: Location): Location =
    val col = loc.col
    ByteLocation((if loc.row == 1 then 2 else 1).toByte, loc.col.toByte)

  def getHomeLocation(player1: Boolean): Location =
    if player1 then ByteLocation(1, 1) else ByteLocation(1, size.toByte)
