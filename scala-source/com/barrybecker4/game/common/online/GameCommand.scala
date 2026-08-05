/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.online

import java.io.Serializable
import java.util.Objects

/**
 * These game commands are passed over the wire between client and server
 * as a means of communication between the two.
 * Implements command pattern.
 * Immutable.
 *
 * @author Barry Becker
 */
class GameCommand(name: GameCommand.Name, arg: Serializable) extends Serializable:

  Objects.requireNonNull(name, "name")
  private val name_ : GameCommand.Name = name
  private val argument_ : Object = Objects.requireNonNull(arg, "argument must not be null").asInstanceOf[Object]

  def getName: GameCommand.Name = name_

  def getArgument: Object = argument_

  override def toString: String =
    "Command: " + name_ + " " + argument_.toString

object GameCommand:

  val CHANGE_TO: String = "!:!"

  /**
   * list of possible commands that the player can issue.
   * Nested in companion; Java uses GameCommand.Name via static forwarder.
   */
  enum Name extends java.lang.Enum[Name]:
    case ENTER_ROOM,
      LEAVE_ROOM,
      ADD_TABLE,
      JOIN_TABLE,
      CHANGE_NAME,
      UPDATE_TABLES,
      CHAT_MESSAGE,
      START_GAME,
      DO_ACTION
