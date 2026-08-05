/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common

import com.barrybecker4.game.common.GameOptions
import com.barrybecker4.sound.Instruments

object TwoPlayerOptions:
  /** Sound played when move is made */
  private val DEFAULT_TONE = Instruments.TAIKO_DRUM

/**
 * Encapsulate two player options here to keep the TwoPlayerController class mush simpler.
 *
 * @author Barry Becker
 */
class TwoPlayerOptions(preferredTone: String) extends GameOptions:

  import TwoPlayerOptions.*

  /** if true, then try to show a dialog visualizing the game tree.  */
  private var showGameTree_ : Boolean = false

  private var showGameOverDialog_ : Boolean = true
  private var autoOptimize_ : Boolean = false
  private var autoOptimizeFile_ : String = _
  private var preferredTone_ : String = _

  /**
   * Default Constructor
   */
  def this() =
    this(TwoPlayerOptions.DEFAULT_TONE)

  preferredTone_ = if preferredTone == null then Instruments.TAIKO_DRUM else preferredTone
  showGameOverDialog_ = true
  showGameTree_ = false

  /**
   * Two player games can never have more than 2 players. Duh.
   * @return 2
   */
  override def getMaxNumPlayers: Int = 2

  /**
   * @return whether or not we are showing the game tree (primarily used for debugging)
   */
  final def getShowGameTree(): Boolean = showGameTree_

  final def setShowGameTree(show: Boolean): Unit =
    showGameTree_ = show

  /**
   * This is useful if running lots of games in a row and the user should not have to click to dismiss the dialogs.
   * @param value whether or not to show a popup when the game is completed
   */
  final def setShowGameOverDialog(value: Boolean): Unit =
    showGameOverDialog_ = value

  /**
   * @return if false, then no game over dialog pops up at the end of the game
   */
  def getShowGameOverDialog(): Boolean = showGameOverDialog_

  /**
   * Optimize the evaluation weights by running many games where the computer
   * plays against itself.
   * @param autoOptimize whether or not to do an optimization run when you press ok.
   */
  final def setAutoOptimize(autoOptimize: Boolean): Unit =
    autoOptimize_ = autoOptimize
    showGameOverDialog_ = !autoOptimize

  /**
   * @return true it the controller is set to auto optimize instead of play a regular game
   */
  def isAutoOptimize(): Boolean = autoOptimize_

  /**
   * @param autoOptimizeFile the log file to write to when autp optimizing.
   */
  final def setAutoOptimizeFile(autoOptimizeFile: String): Unit =
    autoOptimizeFile_ = autoOptimizeFile

  /**
   * @return the log file to write to when autp optimizing.
   */
  final def getAutoOptimizeFile(): String =
    assert(autoOptimizeFile_ != null, "There is no auto optimize file")
    autoOptimizeFile_

  def getPreferredTone(): String = preferredTone_

  protected def getDefaultTone(): String = DEFAULT_TONE
