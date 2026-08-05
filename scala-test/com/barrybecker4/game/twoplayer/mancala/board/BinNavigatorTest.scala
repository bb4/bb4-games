// Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.mancala.board

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.common.geometry.Location
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite

import scala.compiletime.uninitialized

/** @author Barry Becker */
class BinNavigatorTest extends AnyFunSuite with BeforeAndAfterEach:

  /** instance under test. */
  private var navigator: BinNavigator = uninitialized

  override def beforeEach(): Unit =
    navigator = new BinNavigator(8)

  /**
    * Next locations move counter clockwise
    * The grid positions are something like
    * 1,1  1,2  1,3  1,4  1,5  1,6  1,7  1,8
    *      2,2  2,3  2,4  2,5  2,6  2,7
    */
  test("testGetNextLocation8") {
    checkNext(new ByteLocation(1, 1), new ByteLocation(2, 2))
    checkNext(new ByteLocation(2, 4), new ByteLocation(2, 5))
    checkNext(new ByteLocation(1, 6), new ByteLocation(1, 5))
    checkNext(new ByteLocation(2, 7), new ByteLocation(1, 8))
  }

  test("testGetNextLocation3") {
    navigator = new BinNavigator(3)
    checkNext(new ByteLocation(1, 1), new ByteLocation(2, 2))
    checkNext(new ByteLocation(1, 2), new ByteLocation(1, 1))
    checkNext(new ByteLocation(2, 2), new ByteLocation(1, 3))
    checkNext(new ByteLocation(1, 3), new ByteLocation(1, 2))
  }

  test("testGetOppositeLocation") {
    checkOppositeLocation(new ByteLocation(1, 2), new ByteLocation(2, 2))
    checkOppositeLocation(new ByteLocation(2, 3), new ByteLocation(1, 3))
    checkOppositeLocation(new ByteLocation(1, 7), new ByteLocation(2, 7))
    checkOppositeLocation(new ByteLocation(1, 1), new ByteLocation(2, 1))
  }

  test("testGetOppositeLocation1") {
    checkOppositeLocation(new ByteLocation(2, 2), new ByteLocation(1, 2))
    checkOppositeLocation(new ByteLocation(1, 3), new ByteLocation(2, 3))
    checkOppositeLocation(new ByteLocation(2, 7), new ByteLocation(1, 7))
    checkOppositeLocation(new ByteLocation(2, 1), new ByteLocation(1, 1))
    checkOppositeLocation(new ByteLocation(2, 8), new ByteLocation(1, 8))
  }

  test("testGetHome") {
    assert(
      navigator.getHomeLocation(true) == new ByteLocation(1, 1),
      "Unexpected player 1 home"
    )
    assert(
      navigator.getHomeLocation(false) == new ByteLocation(1, 8),
      "Unexpected player 2 home"
    )
  }

  private def checkNext(current: Location, expNext: Location): Unit =
    assert(navigator.getNextLocation(current) == expNext, "Unexpected next location")

  private def checkOppositeLocation(current: Location, expOpposite: Location): Unit =
    assert(
      navigator.getOppositeLocation(current) == expOpposite,
      "Unexpected opposite location"
    )
