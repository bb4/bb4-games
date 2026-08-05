// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.checkers

import com.barrybecker4.game.twoplayer.common.TwoPlayerPlayerOptions
import com.barrybecker4.game.twoplayer.common.search.options.{BestMovesSearchOptions, BruteSearchOptions, SearchOptions}

import java.awt.Color

class CheckersPlayerOptions(name: String, color: Color) 
    extends TwoPlayerPlayerOptions[CheckersMove, CheckersBoard](name, color) {

  override def createDefaultSearchOptions(): SearchOptions[CheckersMove, CheckersBoard] = {
    new SearchOptions(new BruteSearchOptions(4), new BestMovesSearchOptions(100, 0, 10))
  }
}
