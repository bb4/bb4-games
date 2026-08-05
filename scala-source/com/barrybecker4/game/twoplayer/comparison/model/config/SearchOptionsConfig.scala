// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.model.config

import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions

/**
 * Right now this just contains the name of the config and the
 * search options, but we may add the game weights too at some point.
 *
 * @author Barry Becker
 */
class SearchOptionsConfig(val name: String, val searchOptions: SearchOptions[?, ?]) {

  def getName: String = name

  def getSearchOptions: SearchOptions[?, ?] = searchOptions
}
