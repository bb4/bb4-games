package com.barrybecker4.game.common.ui.viewer.animation

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.common.board.{BoardPosition, CaptureList, GamePiece}
import org.scalatest.funsuite.AnyFunSuite

class MoveAnimationStateTest extends AnyFunSuite {

  test("create builds state for single jump and hides captures as hops complete") {
    val captures = new CaptureList()
    captures.add(new BoardPosition(5, 3, new GamePiece(false)))
    val state = MoveAnimationState.create(
      new ByteLocation(4, 2),
      new ByteLocation(6, 4),
      captures,
      new GamePiece(true)
    ).get

    assert(state.segmentCount == 1)
    assert(state.visibleCaptures.size == 1)
    state.progress = 1.0
    state.completeCurrentHop()
    assert(state.isComplete)
    assert(state.visibleCaptures.isEmpty)
  }

  test("create returns None for null piece") {
    assert(MoveAnimationState.create(
      new ByteLocation(1, 1),
      new ByteLocation(2, 2),
      null,
      null
    ).isEmpty)
  }
}
