// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.online

import com.barrybecker4.game.common.player.Player
import com.barrybecker4.game.multiplayer.trivial.TrivialOptions
import org.scalatest.funsuite.AnyFunSuite

import java.awt.Color

class OnlineGameTableListSuite extends AnyFunSuite:

  private val opts = new TrivialOptions(2, 0)

  private def human(name: String): Player = new Player(name, Color.RED, true)

  private def computer(name: String): Player = new Player(name, Color.BLUE, false)

  private def table(name: String, owner: Player): OnlineGameTable =
    new TestOnlineGameTable(name, owner, opts)

  test("removePlayer by Player keeps table when another human remains") {
    val alice = human("alice")
    val bob = human("bob")
    val t = table("t1", alice)
    t.addPlayer(bob)
    val tables = new OnlineGameTableList()
    tables.add(t)

    tables.removePlayer(alice)

    assert(tables.size() == 1)
    assert(!t.hasPlayer("alice"))
    assert(t.hasPlayer("bob"))
  }

  test("removePlayer by Player removes table when last human leaves") {
    val alice = human("alice")
    val robot = computer("bot")
    val t = table("t1", alice)
    t.addPlayer(robot)
    val tables = new OnlineGameTableList()
    tables.add(t)

    tables.removePlayer(alice)

    assert(tables.isEmpty)
  }

  test("removePlayer by name keeps table when another human remains") {
    val alice = human("alice")
    val bob = human("bob")
    val t = table("t1", alice)
    t.addPlayer(bob)
    val tables = new OnlineGameTableList()
    tables.add(t)

    tables.removePlayer("alice")

    assert(tables.size() == 1)
    assert(!t.hasPlayer("alice"))
    assert(t.hasPlayer("bob"))
  }

  test("removePlayer by name removes table when last human leaves") {
    val alice = human("alice")
    val t = table("t1", alice)
    val tables = new OnlineGameTableList()
    tables.add(t)

    tables.removePlayer("alice")

    assert(tables.isEmpty)
  }

  test("removePlayer only affects the table containing the player") {
    val alice = human("alice")
    val bob = human("bob")
    val t1 = table("t1", alice)
    val t2 = table("t2", bob)
    val tables = new OnlineGameTableList()
    tables.add(t1)
    tables.add(t2)

    tables.removePlayer("alice")

    assert(tables.size() == 1)
    assert(tables.get(0).getName == "t2")
    assert(t2.hasPlayer("bob"))
  }

  test("getTableReadyToPlay returns table when ready and player seated") {
    val alice = human("alice")
    val bob = human("bob")
    val t = table("ready", alice)
    t.addPlayer(bob) // max players = 2 → ready
    val tables = new OnlineGameTableList()
    tables.add(t)

    assert(tables.getTableReadyToPlay("alice").contains(t))
    assert(tables.getTableReadyToPlay("carol").isEmpty)
  }

  test("getTableReadyToPlay empty when table not full") {
    val alice = human("alice")
    val tables = new OnlineGameTableList()
    tables.add(table("partial", alice))
    assert(tables.getTableReadyToPlay("alice").isEmpty)
  }
