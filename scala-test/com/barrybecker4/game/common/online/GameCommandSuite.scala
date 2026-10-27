/* Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.common.online

import org.scalatest.funsuite.AnyFunSuite

/** Tests for online protocol command objects. */
class GameCommandSuite extends AnyFunSuite:

  test("require non-null argument") {
    val caught = intercept[NullPointerException] {
      new GameCommand(GameCommand.Name.ENTER_ROOM, null)
    }
    assert(caught.getMessage.contains("argument"))
  }

  test("require non-null name") {
    val caught = intercept[NullPointerException] {
      new GameCommand(null, "")
    }
    assert(caught.getMessage.contains("name"))
  }

  test("toString and change name delimiter") {
    val cmd = new GameCommand(GameCommand.Name.CHAT_MESSAGE, "hello")
    assert(cmd.toString.contains("CHAT_MESSAGE"))
    assert(cmd.toString.contains("hello"))

    val delim = GameCommand.CHANGE_TO
    val payload = s"old${delim}new"
    val rename = new GameCommand(GameCommand.Name.CHANGE_NAME, payload)
    val parts = rename.getArgument.asInstanceOf[String].split(java.util.regex.Pattern.quote(delim))
    assert(parts.length == 2)
    assert(parts(0) == "old")
    assert(parts(1) == "new")
  }
