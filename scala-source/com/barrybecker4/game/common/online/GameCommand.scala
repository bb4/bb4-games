/* Copyright by Barry G. Becker, 2000-2036. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.online

import com.barrybecker4.game.common.player.PlayerAction

import java.io.Serializable
import java.util.Objects

/**
 * These game commands are passed over the wire between client and server
 * as a means of communication between the two.
 * Implements command pattern.
 * Immutable.
 */
class GameCommand(name: GameCommand.Name, arg: Serializable) extends Serializable:

  Objects.requireNonNull(name, "name")
  private val name_ : GameCommand.Name = name
  private val argument_ : Serializable = Objects.requireNonNull(arg, "argument must not be null")

  def getName: GameCommand.Name = name_

  def getArgument: Object = argument_.asInstanceOf[Object]

  /** Typed access to the command argument; fails clearly on type mismatch. */
  def argumentAs[T](clazz: Class[T]): T =
    if clazz.isInstance(argument_) then clazz.cast(argument_)
    else throw new ClassCastException(
      s"GameCommand $name_ expected ${clazz.getSimpleName} but was ${argument_.getClass.getSimpleName}"
    )

  def asString: String = argumentAs(classOf[String])

  def asTable: OnlineGameTable = argumentAs(classOf[OnlineGameTable])

  def asAction: PlayerAction = argumentAs(classOf[PlayerAction])

  def asTables: OnlineGameTableList = argumentAs(classOf[OnlineGameTableList])

  override def toString: String =
    "Command: " + name_ + " " + argument_.toString

object GameCommand:

  val CHANGE_TO: String = "!:!"

  def apply(name: Name, arg: Serializable): GameCommand = new GameCommand(name, arg)

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
