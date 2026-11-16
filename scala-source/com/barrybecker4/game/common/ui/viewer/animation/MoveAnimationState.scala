/* Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui.viewer.animation

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.board.{BoardPosition, CaptureList, GamePiece}

import scala.jdk.CollectionConverters.*

/**
 * Mutable paint-time state for animating a piece along a path.
 * Board coordinates only — convert to pixels each paint using current cell size.
 */
class MoveAnimationState(
    val path: List[Location],
    val movingPiece: GamePiece,
    val captures: List[BoardPosition]
) {
  /** Index of the segment currently being traversed (from path(i) to path(i+1)). */
  var segmentIndex: Int = 0

  /** Progress within the current segment in [0, 1]. */
  var progress: Double = 0.0

  /**
   * Number of captures still drawn. Captures with index &lt; completedHops are hidden.
   * After hop i completes, completedHops becomes i+1.
   */
  var completedHops: Int = 0

  def toLocation: Location = path.last

  def fromLocation: Location = path.head

  def segmentCount: Int = Math.max(0, path.length - 1)

  def isComplete: Boolean = segmentIndex >= segmentCount

  def segmentStart: Location = path(segmentIndex)

  def segmentEnd: Location = path(segmentIndex + 1)

  def visibleCaptures: List[BoardPosition] =
    captures.drop(completedHops)

  def completeCurrentHop(): Unit = {
    if (completedHops < captures.size) {
      completedHops += 1
    }
    segmentIndex += 1
    progress = 0.0
  }
}

object MoveAnimationState {

  def create(
      from: Location,
      to: Location,
      captureList: CaptureList,
      piece: GamePiece
  ): Option[MoveAnimationState] = {
    if (from == null || to == null || piece == null) return None
    val path = JumpPathBuilder.buildPath(from, to, captureList)
    if (path.length < 2) return None
    val captures =
      if (captureList == null) List.empty
      else captureList.asScala.map(_.copy()).toList
    Some(new MoveAnimationState(path, piece.copy(), captures))
  }
}
