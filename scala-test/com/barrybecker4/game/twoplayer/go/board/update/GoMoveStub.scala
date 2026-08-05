/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.update

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone
import com.barrybecker4.game.twoplayer.go.board.move.GoMove

/**
 * Stub implementation of GoMove to help test without a full game setup.
 * @author Barry Becker
 */
class GoMoveStub(stone: GoStone) extends GoMove(new ByteLocation(1, 1), 1, stone):

  private var numCaptures: Int = 0

  override def isSuicidal(board: GoBoard): Boolean = false

  def setNumCaptures(numCaptures: Int): Unit =
    this.numCaptures = numCaptures

  override def getNumCaptures: Int = numCaptures
