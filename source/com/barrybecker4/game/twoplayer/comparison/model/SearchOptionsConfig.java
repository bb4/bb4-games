// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.model;

import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions;

/**
 * Right now this just contains the name of the config and the
 * search options, but we may add the game weights too at some point.
 *
 * @author Barry Becker
 */
public class SearchOptionsConfig {

    private String name;
    private SearchOptions searchOptions;

    public SearchOptionsConfig(String name, SearchOptions options) {
        this.name = name;
        this.searchOptions = options;
    }

    public String getName() {
        return name;
    }

    public SearchOptions getSearchOptions() {
        return searchOptions;
    }
}
