package com.barrybecker4.game.twoplayer.hex.pathsearch

import com.barrybecker4.common.geometry.IntLocation
import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.hex.HexBoard
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite

import scala.compiletime.uninitialized

/** @author Barry Becker */
class HexSearchSpaceSuite extends AnyFunSuite with BeforeAndAfterEach:

  private var board: HexBoard = uninitialized
  private var space: HexSearchSpace = uninitialized

  override def beforeEach(): Unit =
    board = new HexBoard()
    space = new HexSearchSpace(board, true)

  test("construction") {
    val distance = space.distanceFromGoal(new HexState(board, new IntLocation(5, 5)))
    assert(distance == 63, "Unexpected distance.")

    val expInitialState = new HexState(board, new IntLocation(0, 2))
    assert(space.initialState == expInitialState, "Unexpected initial state")
  }

  test("transition cost") {
    assert(space.getCost(new HexTransition(new IntLocation(6, 6), 3)) == 3, "Unexpected cost")
  }

  test("is player1 goal") {
    val state = new HexState(board, new IntLocation(12, 4))
    assert(space.isGoal(state), "Unexpectedly not goal")
  }

  test("is not player1 goal") {
    val notGoal = new HexState(board, new IntLocation(4, 1))
    assert(!space.isGoal(notGoal), "Unexpectedly goal")
  }

  test("is player2 goal") {
    space = new HexSearchSpace(board, false)
    val state = new HexState(board, new IntLocation(4, 12))
    assert(space.isGoal(state), "Unexpectedly not goal")
  }

  test("is not player2 goal") {
    space = new HexSearchSpace(board, false)
    val notGoal = new HexState(board, new IntLocation(4, 1))
    assert(!space.isGoal(notGoal), "Unexpectedly goal")
  }

  test("legal transitions from middle for p1") {
    val state = new HexState(board, new IntLocation(4, 1))
    val transitions = space.legalTransitions(state)

    assert(transitions.size == 4, "Unexpected number of transitions.")

    assert(
      transitions.mkString(", ") ==
        "[(row=4, column=2): 10], " +
          "[(row=3, column=2): 10], " +
          "[(row=3, column=1): 10], " +
          "[(row=5, column=1): 10]",
      "Unexpected transitions."
    )
  }

  test("legal transitions from middle for p2") {
    space = new HexSearchSpace(board, false)

    val state = new HexState(board, new IntLocation(4, 1))
    val transitions = space.legalTransitions(state)

    assert(
      transitions.mkString(", ") ==
        "[(row=4, column=0): 0], [(row=5, column=0): 0], [(row=4, column=2): 10], " +
          "[(row=3, column=2): 10], [(row=3, column=1): 10], [(row=5, column=1): 10]",
      "Unexpected transitions."
    )
  }

  test("legal transitions from top edge for p1") {
    val state = new HexState(board, new IntLocation(0, 6))
    val transitions = space.legalTransitions(state)

    assert(
      transitions.mkString(", ") ==
        "[(row=0, column=7): 0], " +
          "[(row=0, column=5): 0], " +
          "[(row=1, column=5): 10], " +
          "[(row=1, column=6): 10]",
      "Unexpected transitions."
    )
  }

  test("legal transitions from left edge for p1") {
    val state = new HexState(board, new IntLocation(4, 0))
    val transitions = space.legalTransitions(state)

    assert(
      transitions.mkString(", ") ==
        "[(row=4, column=1): 10], " +
          "[(row=3, column=1): 10]",
      "Unexpected transitions."
    )
  }

  test("legal transitions from left edge for p2") {
    space = new HexSearchSpace(board, false)

    val state = new HexState(board, new IntLocation(4, 0))
    val transitions = space.legalTransitions(state)

    assert(
      transitions.mkString(", ") ==
        "[(row=3, column=0): 0], [(row=5, column=0): 0], " +
          "[(row=4, column=1): 10], [(row=3, column=1): 10]",
      "Unexpected transitions."
    )
  }

  test("legal transitions from middle for p1 with surrounding") {
    board.makeMove(TwoPlayerMove.createMove(3, 1, 0, new GamePiece(true)))
    board.makeMove(TwoPlayerMove.createMove(4, 2, 0, new GamePiece(false)))

    val state = new HexState(board, new IntLocation(4, 1))
    val transitions = space.legalTransitions(state)

    assert(
      transitions.mkString(", ") ==
        "[(row=3, column=1): 0], [(row=3, column=2): 10], [(row=5, column=1): 10]",
      "Unexpected transitions."
    )
  }

  test("legal transitions from middle for p2 with surrounding") {
    board.makeMove(TwoPlayerMove.createMove(3, 1, 0, new GamePiece(true)))
    board.makeMove(TwoPlayerMove.createMove(4, 2, 0, new GamePiece(false)))

    space = new HexSearchSpace(board, false)

    val state = new HexState(board, new IntLocation(4, 1))
    val transitions = space.legalTransitions(state)

    assert(
      transitions.mkString(", ") ==
        "[(row=4, column=2): 0], [(row=4, column=0): 0], [(row=5, column=0): 0], " +
          "[(row=3, column=2): 10], [(row=5, column=1): 10]",
      "Unexpected transitions."
    )
  }

  test("legal transitions from top edge for p1 with surrounding") {
    board.makeMove(TwoPlayerMove.createMove(3, 1, 5, new GamePiece(true)))
    board.makeMove(TwoPlayerMove.createMove(4, 1, 6, new GamePiece(false)))

    val state = new HexState(board, new IntLocation(0, 6))
    val transitions = space.legalTransitions(state)

    assert(
      transitions.mkString(", ") ==
        "[(row=0, column=7): 0], " +
          "[(row=0, column=5): 0], " +
          "[(row=1, column=5): 10], " +
          "[(row=1, column=6): 10]",
      "Unexpected transitions."
    )
  }

  test("legal transitions from left edge for p1 with surrounding") {
    board.makeMove(TwoPlayerMove.createMove(3, 1, 0, new GamePiece(true)))
    board.makeMove(TwoPlayerMove.createMove(4, 2, 0, new GamePiece(false)))

    val state = new HexState(board, new IntLocation(4, 0))
    val transitions = space.legalTransitions(state)

    assert(
      transitions.mkString(", ") ==
        "[(row=3, column=1): 0], [(row=4, column=1): 10]",
      "Unexpected transitions."
    )
  }

  test("legal transitions from left edge for p2 with surrounding") {
    board.makeMove(TwoPlayerMove.createMove(3, 1, 0, new GamePiece(true)))
    board.makeMove(TwoPlayerMove.createMove(4, 2, 0, new GamePiece(false)))

    space = new HexSearchSpace(board, false)

    val state = new HexState(board, new IntLocation(4, 0))
    val transitions = space.legalTransitions(state)

    assert(
      transitions.mkString(", ") ==
        "[(row=3, column=0): 0], [(row=5, column=0): 0], [(row=4, column=1): 10]",
      "Unexpected transitions."
    )
  }
