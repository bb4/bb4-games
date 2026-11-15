// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common

import org.scalatest.funsuite.AnyFunSuite

/** MoveList edge cases and getRandomMoveForThresh (expects values sorted descending). */
class MoveListSuite extends AnyFunSuite:

  private def move(value: Int): Move =
    val m = new Move()
    m.setValue(value)
    m

  test("getLastMove is empty when list is empty") {
    val list = new MoveList[Move]()
    assert(list.getLastMove.isEmpty)
  }

  test("getLastMove returns last added move") {
    val list = new MoveList[Move]()
    val a = move(1)
    val b = move(2)
    list.add(a)
    list.add(b)
    assert(list.getLastMove.contains(b))
  }

  test("getRandomMoveForThresh with single move returns that move") {
    val list = new MoveList[Move]()
    val only = move(100)
    list.add(only)
    assert(list.getRandomMoveForThresh(50) eq only)
  }

  test("getRandomMoveForThresh includes first move at or below threshold") {
    // Sorted descending by value (precondition for UCT / BestMoveFinder)
    val list = new MoveList[Move]()
    val best = move(100)
    val near = move(90)
    val far = move(10)
    list.add(best)
    list.add(near)
    list.add(far)

    // thresh = 100 * (1 - 0.10) = 90 → walk while value > 90; stop on near(90) with ct=2
    val nearBand = (1 to 40).map(_ => list.getRandomMoveForThresh(10)).toSet
    assert(nearBand.subsetOf(Set(best, near)))
    assert(!nearBand.contains(far))

    // percent 0 → thresh = best; never enter loop; only index 0
    assert((1 to 10).forall(_ => list.getRandomMoveForThresh(0) eq best))
  }

  test("getRandomMoveForThresh with 100 percent includes all moves") {
    val list = new MoveList[Move]()
    val a = move(50)
    val b = move(40)
    val c = move(30)
    list.add(a)
    list.add(b)
    list.add(c)
    // thresh = 50 * (1 - 1.0) = 0 → walk until value ≤ 0 or end; all > 0 so ct becomes 3
    val picks = (1 to 60).map(_ => list.getRandomMoveForThresh(100)).toSet
    assert(picks == Set(a, b, c))
  }

  test("copy deep-copies moves") {
    val list = new MoveList[Move]()
    list.add(move(7))
    val copied = list.copy()
    assert(copied.getNumMoves == 1)
    assert(copied.getLastMove.get.getValue == 7)
    assert(!(copied.get(0) eq list.get(0)))
  }
