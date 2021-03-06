/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information;

/**
 * Two space eye - **
 *
 * @author Barry Becker
 */
public class E2Information extends AbstractEyeSubtypeInformation {

    public E2Information() {
        initialize(false, 2);
    }

    @Override
    public String getTypeName() {
       return "E11";
    }
}
