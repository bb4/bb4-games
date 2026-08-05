/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.update

/**
 * Things to verify after moving or removing stones from the board
 * @author Barry Becker
 */
class UpdateStats(
  var expCaptures: Int,
  var expStringLiberties: Int,
  var expNumStonesInString: Int,
  var expNumStringsInGroup: Int,
  var expNumEyesInGroup: Int,
  var expGroupsOnBoard: Int,
  var expValid: Boolean
)
