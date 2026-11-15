// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.board

import org.scalatest.funsuite.AnyFunSuite

class BoardPositionsSuite extends AnyFunSuite:

  test("clear assigns distinct locations to each cell without sharing proto location") {
    val positions = new BoardPositions(3, 3)
    val proto = new BoardPosition(1, 1, null)
    positions.clear(proto)

    val seen = scala.collection.mutable.Set.empty[(Int, Int)]
    for i <- 1 to 3; j <- 1 to 3 do
      val pos = positions.getPosition(i, j)
      assert(pos != null)
      assert(pos.getRow == i && pos.getCol == j)
      assert(seen.add((pos.getRow, pos.getCol)), s"duplicate location at ($i,$j)")
      assert(!(pos.getLocation eq proto.getLocation), "cell must not share proto Location ref")
  }

  test("getPosition out of bounds returns null") {
    val positions = new BoardPositions(2, 2)
    assert(positions.getPosition(0, 1) == null)
    assert(positions.getPosition(1, 0) == null)
    assert(positions.getPosition(3, 1) == null)
    assert(positions.getPosition(1, 3) == null)
  }

  test("equals compares occupancy and piece ownership") {
    val a = new BoardPositions(2, 2)
    val b = new BoardPositions(2, 2)
    a.clear(new BoardPosition(1, 1, null))
    b.clear(new BoardPosition(1, 1, null))
    assert(a == b)

    a.getPosition(1, 1).setPiece(new GamePiece(true))
    assert(a != b)

    b.getPosition(1, 1).setPiece(new GamePiece(true))
    assert(a == b)

    b.getPosition(1, 1).setPiece(new GamePiece(false))
    assert(a != b)
  }

  test("hashCode changes when occupancy changes") {
    val a = new BoardPositions(2, 2)
    a.clear(new BoardPosition(1, 1, null))
    val emptyHash = a.hashCode()
    a.getPosition(2, 2).setPiece(new GamePiece(true))
    assert(a.hashCode() != emptyHash)
  }
