// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.chess

import com.barrybecker4.game.twoplayer.common.TwoPlayerPlayerOptions
import com.barrybecker4.game.twoplayer.common.search.options.{BestMovesSearchOptions, BruteSearchOptions, SearchOptions}

import java.awt.Color

class ChessPlayerOptions(name: String, color: Color) 
    extends TwoPlayerPlayerOptions[ChessMove, ChessBoard](name, color) {

  override def createDefaultSearchOptions(): SearchOptions[ChessMove, ChessBoard] = {
    new SearchOptions(new BruteSearchOptions(2), new BestMovesSearchOptions(80, 0, 10))
  }
}
