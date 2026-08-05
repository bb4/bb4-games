// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.go.board

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionList}

/**
 * Helps configure go boards for unit testing.
 * @author Barry Becker
 */
class GoBoardConfigurator(size: Int, numHandicap: Int = 0):

  private val board: GoBoard = new GoBoard(size, numHandicap)

  def getBoard: GoBoard = board

  /** Used in unit tests to initialize a board when we do not want to bother loading a file */
  def setPositions(positions: GoBoardPosition*): Unit =
    for position <- positions do
      board.setPosition(position)

  def setPositions(list: GoBoardPositionList): Unit =
    import scala.jdk.CollectionConverters._
    for position <- list.asScala do
      board.setPosition(position)


object GoBoardConfigurator:
  /**
   * @param positions location of positions to create.
   * @return a list of go board positions with no stones in them.
   */
  def createPositionList(positions: Array[Location]): GoBoardPositionList =
    val spaces = new GoBoardPositionList()
    for pos <- positions do
      spaces.add(new GoBoardPosition(pos.row, pos.col, null, null))
    spaces
