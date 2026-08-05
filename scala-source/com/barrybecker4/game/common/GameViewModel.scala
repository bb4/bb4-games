/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common

/**
 * The GameController communicates with the viewer via this interface.
 * Alternatively we could use RMI or events, but for now the minimal interface is
 * defined here and called directly by the controller.
 *
 * Kept non-generic to match the original Java API (raw GameViewModel) so Java and
 * Scala viewers can implement it without type-bound conflicts under joint compilation.
 *
 * @author Barry Becker
 */
trait GameViewModel {

  def reset(): Unit

  def getController: GameController[?, ?]
}
