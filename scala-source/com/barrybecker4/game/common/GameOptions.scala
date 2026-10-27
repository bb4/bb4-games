/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common

import java.io.Serializable

/**
 * Options specified on a per game basis.
 *
 * @author Barry Becker
 */
abstract class GameOptions extends Serializable {

  def getMaxNumPlayers: Int

  def testValidity(): String = null
}
