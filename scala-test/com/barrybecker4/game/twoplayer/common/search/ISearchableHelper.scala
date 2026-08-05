/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search

import com.barrybecker4.game.twoplayer.common.{TwoPlayerBoard, TwoPlayerController, TwoPlayerMove}
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions

import java.io.InputStream

/** @author Barry Becker */
trait ISearchableHelper:

  /** Create the game options @return 2 player options for use when testing. */
  def createSearchOptions: SearchOptions[?, ?]

  /** @return the controller containing the searchable to test. */
  def createController: TwoPlayerController[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]]

  /** @return test file containing state of saved game to restore. */
  def getTestResource(problemFileBase: String): InputStream

  /** @return test file location containing state of saved game to restore. */
  def getTestResourceLocation(problemFileBase: String): String

  /**
   * @param progress how far into the game are we.
   * @param player1 true if player one has just played.
   * @return get the game file corresponding to the given amount of progress and the specified player.
   */
  def getTestResource(progress: Progress, player1: Boolean): InputStream
