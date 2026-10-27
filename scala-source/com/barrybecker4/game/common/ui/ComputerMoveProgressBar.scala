// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.ui

import com.barrybecker4.common.format.FormatUtil
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchProgress
import com.barrybecker4.ui.themes.BarryTheme

import javax.swing.{JProgressBar, Timer}
import java.awt.Color
import java.awt.event.{ActionEvent, ActionListener}

/**
 * Shows progress while the computer move is being determined.
 *
 * @author Barry Becker
 */
class ComputerMoveProgressBar extends JProgressBar {

  private val PROGRESS_BAR_COLOR = new Color(20, 80, 230, 130)
  private val PROGRESS_UPDATE_DELAY = 700
  private val PROGRESS_STEP_DELAY = 100

  private var searchProgress: SearchProgress = null

  /** Periodically updates the progress bar.  */
  private var timer: Timer = null

  /** becomes true when stepping through the search.   */
  private var stepping = false

  setOpaque(false)
  setMinimum(0)
  setMaximum(100)
  setBackground(BarryTheme.UI_COLOR_SECONDARY2)
  setForeground(PROGRESS_BAR_COLOR)
  setStringPainted(true)
  setBorderPainted(false)
  setString(" ")

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
  def doComputerMove(moveRequester: SearchProgress): Unit = {
    assert(moveRequester != null)
    searchProgress = moveRequester

    // initialize the progress bar if there is one.
    setValue(0)
    setVisible(true)

    // start a thread to update the progress bar at fixed time intervals
    // The timer gets killed when the worker thread is done searching.
    timer = new Timer(PROGRESS_UPDATE_DELAY, new TimerListener())

    timer.start()
  }

  /**
   * Currently this does not actually step forward just one search step, but instead
   * stops after PROGRESS_STEP_DELAY more milliseconds.
   */
  final def step(): Unit = {
    if (timer != null) {
      timer.setDelay(PROGRESS_STEP_DELAY)
      timer.restart()
      stepping = true
      searchProgress.continueProcessing()
    } else {
      GameContext.log(0, "step error : timer is null")
    }
  }

  def cleanup(): Unit = {
    timer.stop()
    setValue(0)
    setString("")
  }

  /**
   * The actionPerformed method in this class
   * is called each time the Timer "goes off".
   */
  private class TimerListener extends ActionListener {

    override def actionPerformed(evt: ActionEvent): Unit = {
      val percentDone = searchProgress.getPercentDone
      setValue(percentDone)
      val numMoves = FormatUtil.formatNumber(searchProgress.getNumMovesConsidered)
      val note = GameContext.getLabel("MOVES_CONSIDERED") + ' ' +
        numMoves + "  (" + percentDone + "%)"

      setToolTipText(note)
      setString(note)

      if (stepping) {
        stepping = false
        searchProgress.pause()
      }
    }
  }
}
