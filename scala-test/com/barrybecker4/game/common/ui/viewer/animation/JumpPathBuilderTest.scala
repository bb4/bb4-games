package com.barrybecker4.game.common.ui.viewer.animation

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.common.board.{BoardPosition, CaptureList, GamePiece}
import org.scalatest.funsuite.AnyFunSuite

class JumpPathBuilderTest extends AnyFunSuite {

  private def loc(row: Int, col: Int) = new ByteLocation(row, col)

  private def captureAt(row: Int, col: Int): BoardPosition =
    new BoardPosition(row, col, new GamePiece(false))

  test("non-capture returns from and to") {
    val path = JumpPathBuilder.buildPath(loc(3, 1), loc(4, 2), null)
    assert(path == List(loc(3, 1), loc(4, 2)))
  }

  test("empty capture list returns from and to") {
    val path = JumpPathBuilder.buildPath(loc(3, 1), loc(4, 2), new CaptureList())
    assert(path == List(loc(3, 1), loc(4, 2)))
  }

  test("single jump hops over capture to landing") {
    // from (4,2) over (5,3) lands at (6,4)
    val captures = new CaptureList()
    captures.add(captureAt(5, 3))
    val path = JumpPathBuilder.buildPath(loc(4, 2), loc(6, 4), captures)
    assert(path == List(loc(4, 2), loc(6, 4)))
  }

  test("double jump builds intermediate landing then final") {
    // (3,1) over (4,2)->(5,3); then over (6,4)->(7,5)
    val captures = new CaptureList()
    captures.add(captureAt(4, 2))
    captures.add(captureAt(6, 4))
    val path = JumpPathBuilder.buildPath(loc(3, 1), loc(7, 5), captures)
    assert(path == List(loc(3, 1), loc(5, 3), loc(7, 5)))
  }

  test("mismatched final landing falls back to from-to") {
    val captures = new CaptureList()
    captures.add(captureAt(5, 3))
    val path = JumpPathBuilder.buildPath(loc(4, 2), loc(8, 8), captures)
    assert(path == List(loc(4, 2), loc(8, 8)))
  }
}
