/* Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.common.online.server

import com.barrybecker4.game.common.online.{OnlineGameTable, TestOnlineGameTable}
import com.barrybecker4.game.common.player.Player
import com.barrybecker4.game.multiplayer.trivial.TrivialOptions
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite

import java.awt.Color

import scala.compiletime.uninitialized

/** Table list operations used by the online server. */
class GameTableManagerSuite extends AnyFunSuite with BeforeAndAfterEach:

  private var manager: GameTableManager = uninitialized

  override def beforeEach(): Unit =
    manager = new GameTableManager()

  test("add table assigns unique suffix") {
    val opts = new TrivialOptions(8, 0)
    val alice = new Player("alice", Color.RED, true)
    val t1: OnlineGameTable = new TestOnlineGameTable("lobby", alice, opts)
    manager.addTable(t1)
    assert(t1.getName == "lobby_0")

    val bob = new Player("bob", Color.BLUE, true)
    val t2: OnlineGameTable = new TestOnlineGameTable("lobby", bob, opts)
    manager.addTable(t2)
    assert(t2.getName == "lobby_1")
    assert(manager.getTables.size() == 2)
  }

  test("change name updates table players") {
    val opts = new TrivialOptions(8, 0)
    val alice = new Player("alice", Color.RED, true)
    val table: OnlineGameTable = new TestOnlineGameTable("t", alice, opts)
    manager.addTable(table)
    manager.changeName("alice", "alice2")
    assert(table.hasPlayer("alice2"))
    assert(!table.hasPlayer("alice"))
  }
