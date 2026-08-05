/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial.player

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.player.PlayerAction
import com.barrybecker4.game.multiplayer.common.{MultiGameController, MultiGamePlayer, MultiPlayerMarker}
import com.barrybecker4.game.multiplayer.trivial.TrivialAction

import java.awt.Color
import java.text.MessageFormat

/**
 * Represents a trivial player in a game
 *
 * @author Barry Becker
 */
abstract class TrivialPlayer(name: String, color: Color, isHuman: Boolean) 
  extends MultiGamePlayer(name, color, isHuman):

  private var piece: MultiPlayerMarker = new MultiPlayerMarker(this)
  private val value: Int = (TrivialPlayer.MAX_VALUE * Math.random()).toInt + 1
  private var action: TrivialAction = scala.compiletime.uninitialized
  
  /** only becomes true when the player decides to reveal his value */
  private var revealed: Boolean = false

  /**
   * @return a number between 0 and MAX_VALUE.
   */
  def getValue: Int = value

  override def getAction(pc: MultiGameController[?, ?]): PlayerAction = action

  override def setAction(action: PlayerAction): Unit =
    this.action = action.asInstanceOf[TrivialAction]

  /**
   * You can only reveal your value, never hide it once revealed.
   */
  def revealValue(): Unit =
    revealed = true

  def isRevealed: Boolean = revealed

  override def getPiece: MultiPlayerMarker = piece

  /**
   * @param i index of player
   * @return  the default name for player i
   */
  def getDefaultName(i: Int): String =
    MessageFormat.format(GameContext.getLabel("TRIVIAL_DEFAULT_NAME"), Integer.toString(i))

  def setPiece(piece: MultiPlayerMarker): Unit =
    this.piece = piece

object TrivialPlayer:
  private val serialVersionUID = 1L
  private val MAX_VALUE = 100

  /**
   * Factory method for creating players of the appropriate type.
   * @param name name of the trivial player
   * @param color color of the player
   * @param isHuman true if human
   * @return the new player
   */
  def createTrivialPlayer(name: String, color: Color, isHuman: Boolean): TrivialPlayer =
    if isHuman then
      new TrivialHumanPlayer(name, color)
    else
      new TrivialRobotPlayer(name, color)
