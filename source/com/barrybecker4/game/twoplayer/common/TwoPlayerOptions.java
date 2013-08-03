/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common;

import com.barrybecker4.game.common.GameOptions;
import com.barrybecker4.sound.Instruments;
import com.barrybecker4.sound.MusicMaker;

/**
 * Encapsulate two player options here to keep the TwoPlayerController class mush simpler.
 *
 * @author Barry Becker
 */
public class TwoPlayerOptions extends GameOptions {

    /** Sound played when move is made */
    private static final String DEFAULT_TONE = Instruments.TAIKO_DRUM;

    /** if true, then try to show a dialog visualizing the game tree.  */
    private boolean showGameTree_ = false;

    private boolean showGameOverDialog_ = true;
    private boolean autoOptimize_;
    private String autoOptimizeFile_ = null;

    private String preferredTone_ = null;


    /**
     * Default Constructor
     */
    public TwoPlayerOptions() {
        preferredTone_ = getDefaultTone();
    }

    /**
     * Constructor
     * @param preferredTone sound to make on each move.
     */
    protected TwoPlayerOptions(String preferredTone) {

        preferredTone_ = preferredTone;
        if (preferredTone == null) {
           preferredTone_ = Instruments.TAIKO_DRUM;
        }
    }

    /**
     * Two player games can never have more than 2 players. Duh.
     * @return 2
     */
    @Override
    public int getMaxNumPlayers() {
        return 2;
    }

    /**
     * @return whether or not we are showing the game tree (primarily used for debugging)
     */
    public final boolean getShowGameTree() {
        return showGameTree_;
    }

    public final void setShowGameTree( boolean show ) {
        showGameTree_ = show;
    }

    public final void setShowGameOverDialog(boolean value) {
        showGameOverDialog_ = value;
    }

    public boolean getShowGameOverDialog() {
        return showGameOverDialog_;
    }

    /**
     * Optimize the evaluation weights by running many games where the computer
     * plays against itself.
     * @param autoOptimize whether or not to do an optimization run when you press ok.
     */
    public final void setAutoOptimize(boolean autoOptimize) {
        autoOptimize_ = autoOptimize;
        showGameOverDialog_ = false;
    }

    /**
     * @return true it the controller is set to auto optimize instead of play a regular game
     */
    public boolean isAutoOptimize() {
        return autoOptimize_;
    }

    /**
     * @param autoOptimizeFile the log file to write to when autp optimizing.
     */
    public final void setAutoOptimizeFile(String autoOptimizeFile) {
        autoOptimizeFile_ = autoOptimizeFile;
    }

    /**
     * @return the log file to write to when autp optimizing.
     */
    public final String getAutoOptimizeFile() {
        assert (autoOptimizeFile_!=null) : "There is no auto optimize file";
        return autoOptimizeFile_;
    }


    public String getPreferredTone() {
        return preferredTone_;
    }

    protected String getDefaultTone() {
        return DEFAULT_TONE;
    }
}
