/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.update;


/**
 * Things to verify after moving or removing stones from the board
 * @author Barry Becker
 */
public class UpdateStats {


    public int expCaptures;
    public int expStringLiberties;
    public int expNumStonesInString;
    public int expNumStringsInGroup;
    public int expNumEyesInGroup;
    public int expGroupsOnBoard;
    public boolean expValid;

    public UpdateStats(int expCaptures,
                     int expStringLiberties, int expNumStonesInString,
                     int expNumStringsInGroup, int expNumEyesInGroup,
                     int expGroupsOnBoard, boolean expValid) {

        this.expCaptures = expCaptures;
        this.expStringLiberties = expStringLiberties;
        this.expNumStonesInString = expNumStonesInString;
        this.expNumStringsInGroup = expNumStringsInGroup;
        this.expNumEyesInGroup = expNumEyesInGroup;
        this.expGroupsOnBoard = expGroupsOnBoard;
        this.expValid = expValid;

    }

}
