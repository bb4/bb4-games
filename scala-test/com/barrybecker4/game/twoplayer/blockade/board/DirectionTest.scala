// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board

import com.barrybecker4.common.geometry.ByteLocation
import org.junit.Assert.assertEquals
import org.junit.Test

/** @author Barry Becker */
class DirectionTest:

  @Test def testGetEastOffset(): Unit =
    assertEquals(new ByteLocation(0, 1), Direction.EAST.getOffset)

  @Test def testGetEastDir(): Unit =
    assertEquals(Direction.EAST, Direction.getDirection(0, 1))

  @Test def testGetNorhtNorthOffset(): Unit =
    assertEquals(new ByteLocation(-2, 0), Direction.NORTH_NORTH.getOffset)

  @Test def testGetNorthNorthDir(): Unit =
    assertEquals(Direction.NORTH_NORTH, Direction.getDirection(-2, 0))

  @Test def testGetSourthOffset(): Unit =
    assertEquals(new ByteLocation(1, 0), Direction.SOUTH.getOffset)

  @Test def testGetSouthDir(): Unit =
    assertEquals(Direction.SOUTH, Direction.getDirection(1, 0))

  @Test def testAllDirections(): Unit =
    for dir <- Direction.values do
      val offset = dir.getOffset
      assertEquals(dir, Direction.getDirection(offset.row, offset.col))
