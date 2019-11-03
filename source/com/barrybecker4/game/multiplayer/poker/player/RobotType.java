/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.player;

import java.io.Serializable;

/**
 * Available robot player types.
 *
 * @author Barry Becker
 */
public enum RobotType implements Serializable {

    CRAZY_ROBOT("Crazy"),
    METHODICAL_ROBOT("Methodical"),
    TIMID_ROBOT("Timid");

    private static final long serialVersionUID = 1;

    private String name_;

    RobotType(String name) {
        name_ = name;
    }

    public String getName() {
        return name_;
    }
}
