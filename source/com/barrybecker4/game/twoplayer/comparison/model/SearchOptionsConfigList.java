// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.model;

import java.util.ArrayList;

/**
 * A list of search config options .
 *
 * @author Barry Becker
 */
public class SearchOptionsConfigList extends ArrayList<SearchOptionsConfig> {

    public SearchOptionsConfigList()  {
        super();
    }

    public SearchOptionsConfigList(int size)  {
        super(size);
    }

    /**
     * @throws IllegalStateException if we already have a configuration with that name.
     */
    @Override
    public boolean add(SearchOptionsConfig config) {

        for (SearchOptionsConfig element : this) {
            if (element.getName().equals(config.getName())) {
                throw new IllegalStateException(
                        "Cannot have two conigurations with the same name");
            }
        }
        return super.add(config);
    }
}
