// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.ui;

import com.barrybecker4.common.format.FormatUtil;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchProgress;
import com.barrybecker4.ui.themes.BarryTheme;

import javax.swing.JProgressBar;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Shows progress while the computer move is being determined.
 *
 * @author Barry Becker
 */
public class ComputerMoveProgressBar extends JProgressBar {

    private static final Color PROGRESS_BAR_COLOR = new Color(20, 80, 230, 130);
    private static final int PROGRESS_UPDATE_DELAY = 700;
    private static final int PROGRESS_STEP_DELAY = 100;

    private SearchProgress searchProgress;

    /** Periodically updates the progress bar.  */
    private Timer timer_;

    /** becomes true when stepping through the search.   */
    private boolean stepping_ = false;

    /**
     * Constructor.
     */
    public ComputerMoveProgressBar() {
        setOpaque(false);
        setMinimum(0);
        setMaximum(100);
        setBackground(BarryTheme.UI_COLOR_SECONDARY2);
        setForeground(PROGRESS_BAR_COLOR);
        setStringPainted(true);
        setBorderPainted(false);
        setString(" ");
    }

    /**
     * Make the computer move and show it on the screen.
     * Since this can take a very long time we will show the user a progress bar
     * to give feedback.
     *   The computer needs to search through vast numbers of moves to find the best one.
     * This will happen asynchronously in a separate thread so that the event dispatch
     * thread can return immediately and not lock up the user interface (UI).
     *   Some moves can be complex (like multiple jumps in checkers). For these
     * We animate these types of moves so the human player does not get disoriented.
     *
     * @param moveRequester thing requesting the next computer move.
     */
    public void doComputerMove(SearchProgress moveRequester) {

        assert moveRequester != null;
        searchProgress = moveRequester;

        // initialize the progress bar if there is one.
        setValue(0);
        setVisible(true);

        // start a thread to update the progress bar at fixed time intervals
        // The timer gets killed when the worker thread is done searching.
        timer_ = new Timer(PROGRESS_UPDATE_DELAY, new TimerListener());

        timer_.start();
    }

    /**
     * Currently this does not actually step forward just one search step, but instead
     * stops after PROGRESS_STEP_DELAY more milliseconds.
     */
    public final void step() {
        if (timer_ != null) {
            timer_.setDelay(PROGRESS_STEP_DELAY);
            timer_.restart();
            stepping_ = true;
            searchProgress.continueProcessing();
        }
        else {
            GameContext.log(0,  "step error : timer is null" );
        }
    }

    public void cleanup() {
        timer_.stop();
        setValue(0);
        setString("");
    }

    /**
     * The actionPerformed method in this class
     * is called each time the Timer "goes off".
     */
    private class TimerListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent evt) {

            int percentDone = searchProgress.getPercentDone();
            setValue(percentDone);
            String numMoves = FormatUtil.formatNumber(searchProgress.getNumMovesConsidered());
            String note = GameContext.getLabel("MOVES_CONSIDERED") + ' '
                   + numMoves + "  ("+ percentDone +"%)";

            setToolTipText(note);
            setString(note);

            if (stepping_) {
                stepping_ = false;
                searchProgress.pause();
            }
        }
    }
}