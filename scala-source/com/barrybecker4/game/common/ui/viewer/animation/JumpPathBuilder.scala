/* Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui.viewer.animation

import com.barrybecker4.common.geometry.{ByteLocation, Location}
import com.barrybecker4.game.common.board.CaptureList

import scala.jdk.CollectionConverters.*

/**
 * Builds ordered board locations for animating a piece move.
 * Non-captures are a single from→to segment. Jump captures hop over each
 * captured square in list order.
 */
object JumpPathBuilder {

  /**
   * @param from     starting square
   * @param to       final destination
   * @param captures captured positions in jump order, or null if none
   * @return path starting at `from` and ending at `to` (at least two points)
   */
  def buildPath(from: Location, to: Location, captures: CaptureList): List[Location] = {
    if (from == null || to == null) {
      return List.empty
    }
    if (captures == null || captures.isEmpty) {
      return List(from, to)
    }

    val landings = scala.collection.mutable.ListBuffer[Location]()
    var previous: Location = from
    for (capture <- captures.asScala) {
      val captureLoc = capture.getLocation
      val rowDelta = captureLoc.row - previous.row
      val colDelta = captureLoc.col - previous.col
      val landing: Location = new ByteLocation(captureLoc.row + rowDelta, captureLoc.col + colDelta)
      landings += landing
      previous = landing
    }

    if (landings.isEmpty || landings.last != to) {
      List(from, to)
    } else {
      from :: landings.toList
    }
  }
}
