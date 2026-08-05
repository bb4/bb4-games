/* Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.common.online.server

import com.barrybecker4.game.common.online.{GameCommand, OnlineGameTable, TestOnlineGameTable}
import com.barrybecker4.game.common.player.Player
import com.barrybecker4.game.multiplayer.trivial.{TrivialController, TrivialOptions}
import org.scalatest.funsuite.AnyFunSuite

import java.awt.Color

/** Command dispatch and response ordering for the online game server. */
class ServerCommandProcessorSuite extends AnyFunSuite:

  test("enter room prepends update tables") {
    val controller = new TrivialController()
    val tables = new GameTableManager()
    val proc = ServerCommandProcessor(controller, tables)

    val out = proc.processCommand(new GameCommand(GameCommand.Name.ENTER_ROOM, ""))

    assert(out.size() == 1)
    assert(out.get(0).getName == GameCommand.Name.UPDATE_TABLES)
  }

  test("chat message does not prepend update tables") {
    val controller = new TrivialController()
    val tables = new GameTableManager()
    val proc = ServerCommandProcessor(controller, tables)

    val chat = new GameCommand(GameCommand.Name.CHAT_MESSAGE, "hi")
    val out = proc.processCommand(chat)

    assert(out.size() == 1)
    assert(out.get(0) == chat)
  }

  test("add table returns tables update") {
    val controller = new TrivialController()
    val tables = new GameTableManager()
    val proc = ServerCommandProcessor(controller, tables)

    val opts = new TrivialOptions(8, 0)
    val owner = new Player("owner", Color.GREEN, true)
    val table: OnlineGameTable = new TestOnlineGameTable("alpha", owner, opts)

    val out = proc.processCommand(new GameCommand(GameCommand.Name.ADD_TABLE, table))

    assert(out.size() == 1)
    assert(out.get(0).getName == GameCommand.Name.UPDATE_TABLES)
    assert(tables.getTables.size() == 1)
  }
