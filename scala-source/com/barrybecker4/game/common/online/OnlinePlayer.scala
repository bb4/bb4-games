// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.online

/**
 * @author Barry Becker
 */
trait OnlinePlayer {

  def getActualPlayer: OnlinePlayer

  def isSurrogate: Boolean
}
