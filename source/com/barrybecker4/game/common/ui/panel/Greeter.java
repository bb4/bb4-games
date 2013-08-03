// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.ui.panel;

import com.barrybecker4.game.common.GameContext;

/**
 * Give a simple verbal greeting when the game is about to start
 *
 *  @author Barry Becker
 */
public class Greeter {

    /** A greeting specified using allophones. See SpeechSynthesizer.    */
    protected static final String[] GREETING = {"w|u|d", "y|ouu", "l|ii|k", "t|ouu", "p|l|ay", "aa", "gg|AY|M"};

    /**
     *  UIComponent initialization.
     */
    public static void doGreeting() {

        // Intro speech. Applets sometimes throw security exceptions for this.
        if ( GameContext.getUseSound() ) {
            // This works for arbitrary strings, but is not as nice sounding as the pre-generated wav file.
            /* npe in applet (why?) */
            //SpeechSynthesizer speech = new SpeechSynthesizer();
            //speech.sayPhoneWords( GREETING );

            // use when sound card available
            /* causing security exception in applet? */
            //URL url = FileUtil.getURL("com/barrybecker4/sound/play_game_voice.wav");
            //AudioClip clip = new AppletAudioClip(url);
            //clip.play();
        }
    }

    private Greeter() {}
}
