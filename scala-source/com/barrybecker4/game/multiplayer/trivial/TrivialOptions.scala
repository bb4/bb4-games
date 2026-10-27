/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial

import com.barrybecker4.game.multiplayer.common.MultiGameOptions

/**
 * @author Barry Becker
 */
class TrivialOptions() extends MultiGameOptions:

  /**
   * User specified values for options.
   */
  def this(maxNumPlayers: Int, numRobotPlayers: Int) =
    this()
    setMaxNumPlayers(maxNumPlayers)
    setNumRobotPlayers(numRobotPlayers)

  /**
   * Verify trivial option constraints satisfied.
   * @return error messages to show in a dlg.
   */
  override def testValidity(): String =
    val superMsgs = super.testValidity()
    val msgs = if superMsgs != null then superMsgs else ""

    if msgs.length > 0 then msgs else null
