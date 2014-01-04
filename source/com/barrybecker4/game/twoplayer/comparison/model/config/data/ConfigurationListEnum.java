// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.model.config.data;

import com.barrybecker4.game.twoplayer.comparison.model.config.SearchOptionsConfigList;

/**
 * Different types of canned configurations
 * @author Barry Becker
 */
public enum ConfigurationListEnum {

    DEFAULT_CONFIGS("Default configurations", new DefaultSearchConfigurations()),
    NEGAMAX_CONFIGS("Negamax configurations", new NegaMaxConfigurations()),
    NEGAMAX_ALL_MOVE_CONFIGS("Negamax all move configurations", new NegaMaxAllMoveConfigurations()),
    NEGASCOUT_CONFIGS("Negascout configurations", new NegaScoutConfigurations()),
    UCT_CONFIGS("UCT Variations", new UCTSearchConfigurations()),
    EMPTY_CONFIGS("Empty", new EmptyConfigurations());


    private String name;
    private SearchOptionsConfigList configList;

    /**
     * Constructor
     */
    ConfigurationListEnum(String name, SearchOptionsConfigList configList) {
        this.name = name;
        this.configList = configList;
    }

    public SearchOptionsConfigList getConfigList() {
        return configList;
    }

    public String toString() {
        return name;
    }
}