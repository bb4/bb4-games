/* Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common

import com.barrybecker4.game.common.player.PlayerOptions
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions
import java.awt.Color

/**
 * Not a great name for this class. These are the options for a player in a two player game.
 * Since each player could be a computer player with different search options, the search options
 * are part of this class and not TwoPlayerOptions.
 *
 * @author Barry Becker
 */
class TwoPlayerPlayerOptions[M <: TwoPlayerMove, B <: TwoPlayerBoard[M]](name: String, color: Color)
  extends PlayerOptions(name, color):

  private var searchOptions_ : SearchOptions[M, B] = createDefaultSearchOptions()

  /**
   * Constructor
   * @param searchOptions search options to use.
   */
  def this(name: String, color: Color, searchOptions: SearchOptions[M, B]) =
    this(name, color)
    searchOptions_ = searchOptions

  protected def createDefaultSearchOptions(): SearchOptions[M, B] =
    new SearchOptions[M, B]()

  def getSearchOptions: SearchOptions[M, B] = searchOptions_

  /** Used by unit tests to set desired search options. */
  def setSearchOptions(options: SearchOptions[M, B]): Unit =
    searchOptions_ = options
