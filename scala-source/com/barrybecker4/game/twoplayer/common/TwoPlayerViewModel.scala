/* Copyright by Barry G. Becker, 2000-2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common

import com.barrybecker4.game.common.GameViewModel

/**
 * The TwoPlayerController communicates with the viewer via this interface.
 * Alternatively, we could use RMI or events, but for now the minimal interface is
 * defined here and called directly by the controller.
 *
 * @author Barry Becker
 */
trait TwoPlayerViewModel[M <: TwoPlayerMove, B <: TwoPlayerBoard[M]] extends GameViewModel:

  /**
   * Called when the controller has found the next computer move and needs to make the viewer aware of it.
   * @param move the computers move
   */
  def computerMoved(move: M): Unit

  /**
   * Used when the computer is playing against itself, and you want the game to show up in the viewer and
   * be synchronous (block and not in separate thread).
   */
  def doComputerVsComputerGame(): Unit

  /**
   * Currently this does not actually step forward just one search step, but instead
   * stops after PROGRESS_STEP_DELAY more milliseconds of searching.
   */
  def step(): Unit

  /**
   * resume searching for the next move at full speed.
   */
  def continueProcessing(): Unit
