package com.barrybecker4.game.twoplayer.hex.pathsearch

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.hex.HexBoard
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite

import scala.compiletime.uninitialized
import scala.jdk.CollectionConverters.*

class ShortestPathSearchSuite extends AnyFunSuite with BeforeAndAfterEach:

  private var board: HexBoard = uninitialized
  private var search: ShortestPathSearch = uninitialized

  override def beforeEach(): Unit =
    board = new HexBoard()

  /** When empty, p1 snakes down column 1 to the goal state */
  test("search for p1 when empty") {
    search = new ShortestPathSearch(board, true)
    val path = search.solve.get
    assert(
      path.mkString(", ") ==
        "[(row=0, column=3): 0], [(row=0, column=4): 0], " +
          "[(row=0, column=5): 0], [(row=0, column=6): 0], " +
          "[(row=0, column=7): 0], [(row=0, column=8): 0], " +
          "[(row=0, column=9): 0], [(row=0, column=10): 0], " +
          "[(row=0, column=11): 0], [(row=1, column=11): 10], " +
          "[(row=2, column=11): 10], [(row=3, column=11): 10], " +
          "[(row=4, column=11): 10], [(row=5, column=10): 10], " +
          "[(row=6, column=10): 10], [(row=7, column=10): 10], " +
          "[(row=8, column=9): 10], [(row=9, column=8): 10], " +
          "[(row=10, column=7): 10], [(row=11, column=6): 10], " +
          "[(row=12, column=5): 0]",
      "Unexpected path."
    )
  }

  /** When empty, p2 snakes across row 2 to the goal state */
  test("search for p2 when empty") {
    search = new ShortestPathSearch(board, false)
    val path = search.solve.get
    showSolvedPath(path, true)
    assert(
      path.mkString(", ") ==
        "[(row=3, column=0): 0], [(row=4, column=0): 0], [(row=5, column=0): 0], " +
          "[(row=6, column=0): 0], [(row=7, column=0): 0], [(row=7, column=1): 10], " +
          "[(row=7, column=2): 10], [(row=7, column=3): 10], [(row=6, column=4): 10], " +
          "[(row=5, column=5): 10], [(row=4, column=6): 10], [(row=4, column=7): 10], " +
          "[(row=3, column=8): 10], [(row=2, column=9): 10], [(row=2, column=10): 10], " +
          "[(row=2, column=11): 10], [(row=2, column=12): 0]",
      "Unexpected path."
    )
  }

  test("search for p1 when path almost there") {
    p1Move(3, 3)
    p2Move(3, 6)
    p1Move(3, 4)
    p2Move(3, 5)
    p1Move(4, 4)
    p2Move(4, 5)
    p1Move(5, 4)
    p2Move(5, 5)
    p1Move(6, 4)
    p2Move(6, 5)
    p1Move(7, 4)
    p2Move(7, 5)
    p1Move(8, 4)

    search = new ShortestPathSearch(board, true)
    val path = search.solve.get

    assert(
      path.mkString(", ") ==
        "[(row=0, column=3): 0], " +
          "[(row=0, column=4): 0], " +
          "[(row=1, column=4): 10], " +
          "[(row=2, column=4): 10], " +
          "[(row=3, column=4): 0], " +
          "[(row=4, column=4): 0], " +
          "[(row=5, column=4): 0], " +
          "[(row=6, column=4): 0], " +
          "[(row=7, column=4): 0], " +
          "[(row=8, column=4): 0], " +
          "[(row=9, column=3): 10], " +
          "[(row=10, column=2): 10], " +
          "[(row=11, column=1): 10], " +
          "[(row=12, column=1): 0]",
      "Unexpected path."
    )
  }

  test("search for p1 through keyhole") {
    p1Move(3, 3)
    p2Move(3, 6)
    p2Move(3, 5)
    p1Move(4, 4)
    p2Move(4, 5)
    p2Move(5, 5)
    p1Move(7, 4)
    p2Move(7, 1)
    p2Move(7, 2)
    p2Move(7, 3)
    p2Move(7, 4)
    p2Move(7, 5)
    p2Move(7, 6)
    p2Move(7, 7)
    p2Move(7, 8)
    p2Move(7, 9)
    p2Move(7, 11)

    println("board = " + board)

    search = new ShortestPathSearch(board, true)
    val path = search.solve.get

    assert(
      path.mkString(", ") ==
        "[(row=0, column=3): 0], [(row=0, column=4): 0], " +
          "[(row=0, column=5): 0], [(row=0, column=6): 0], " +
          "[(row=0, column=7): 0], [(row=0, column=8): 0], " +
          "[(row=0, column=9): 0], [(row=0, column=10): 0], " +
          "[(row=0, column=11): 0], [(row=1, column=11): 10], " +
          "[(row=2, column=11): 10], [(row=3, column=11): 10], " +
          "[(row=4, column=11): 10], [(row=5, column=11): 10], " +
          "[(row=6, column=10): 10], [(row=7, column=10): 10], " +
          "[(row=8, column=10): 10], [(row=9, column=10): 10], " +
          "[(row=10, column=10): 10], [(row=11, column=9): 10], " +
          "[(row=12, column=8): 0]",
      "Unexpected path."
    )
  }

  test("search for p1 through two keyholes") {
    p1Move(3, 3)
    p2Move(3, 6)
    p2Move(3, 5)
    p1Move(4, 4)
    p2Move(4, 5)
    p2Move(5, 5)
    p2Move(4, 2)
    p2Move(4, 3)
    p2Move(4, 4)
    p2Move(4, 5)
    p2Move(4, 6)
    p2Move(4, 7)
    p2Move(4, 8)
    p2Move(4, 9)
    p2Move(4, 10)
    p2Move(4, 11)
    p1Move(7, 4)
    p2Move(7, 1)
    p2Move(7, 2)
    p2Move(7, 3)
    p2Move(7, 4)
    p2Move(7, 5)
    p2Move(7, 6)
    p2Move(7, 7)
    p2Move(7, 8)
    p2Move(7, 9)
    p2Move(7, 11)

    println("board = " + board)

    search = new ShortestPathSearch(board, true)
    val path = search.solve.get

    assert(
      path.mkString(", ") ==
        "[(row=0, column=3): 0], [(row=1, column=3): 10], " +
          "[(row=2, column=2): 10], [(row=3, column=1): 10], " +
          "[(row=4, column=1): 10], [(row=5, column=1): 10], " +
          "[(row=6, column=1): 10], [(row=6, column=2): 10], " +
          "[(row=6, column=3): 10], [(row=6, column=4): 10], " +
          "[(row=6, column=5): 10], [(row=6, column=6): 10], " +
          "[(row=6, column=7): 10], [(row=6, column=8): 10], " +
          "[(row=6, column=9): 10], [(row=6, column=10): 10], " +
          "[(row=7, column=10): 10], [(row=8, column=9): 10], " +
          "[(row=9, column=9): 10], [(row=10, column=9): 10], " +
          "[(row=11, column=9): 10], [(row=12, column=8): 0]",
      "Unexpected path."
    )
    showSolvedPath(path, true)
  }

  test("search for p1 when column path shortcut on right") {
    p1Move(2, 10)
    p1Move(3, 10)
    p1Move(4, 10)
    p1Move(5, 10)
    p1Move(6, 10)
    p1Move(7, 10)
    p1Move(8, 10)
    p1Move(9, 10)
    p1Move(10, 10)
    println("board = " + board)

    search = new ShortestPathSearch(board, true)
    val path = search.solve.get
    assert(
      path.mkString(", ") ==
        "[(row=0, column=3): 0], [(row=0, column=4): 0], " +
          "[(row=0, column=5): 0], [(row=0, column=6): 0], " +
          "[(row=0, column=7): 0], [(row=0, column=8): 0], " +
          "[(row=0, column=9): 0], [(row=0, column=10): 0], " +
          "[(row=1, column=10): 10], [(row=2, column=10): 0], " +
          "[(row=3, column=10): 0], [(row=4, column=10): 0], " +
          "[(row=5, column=10): 0], [(row=6, column=10): 0], " +
          "[(row=7, column=10): 0], [(row=8, column=10): 0], " +
          "[(row=9, column=10): 0], [(row=10, column=10): 0], " +
          "[(row=11, column=9): 10], [(row=12, column=8): 0]",
      "Unexpected path."
    )
  }

  test("search for p1 when column path shortcut") {
    p1Move(3, 4)
    p2Move(3, 5)
    p1Move(4, 4)
    p2Move(4, 5)
    p1Move(5, 4)
    p2Move(5, 5)
    p1Move(6, 4)
    p2Move(6, 5)
    p1Move(7, 4)
    p2Move(7, 5)
    p1Move(8, 4)
    println("board = " + board)

    search = new ShortestPathSearch(board, true)
    val path = search.solve.get
    assert(
      path.mkString(", ") ==
        "[(row=0, column=3): 0], [(row=0, column=4): 0], " +
          "[(row=1, column=4): 10], [(row=2, column=4): 10], " +
          "[(row=3, column=4): 0], [(row=4, column=4): 0], " +
          "[(row=5, column=4): 0], [(row=6, column=4): 0], " +
          "[(row=7, column=4): 0], [(row=8, column=4): 0], " +
          "[(row=9, column=3): 10], [(row=10, column=2): 10], " +
          "[(row=11, column=1): 10], [(row=12, column=1): 0]",
      "Unexpected path."
    )
  }

  test("search for p2 when full") {
    p1Move(4, 3)
    p2Move(5, 4)
    p1Move(4, 4)
    p2Move(5, 4)
    p1Move(5, 4)
    p2Move(5, 5)
    p1Move(4, 5)
    p2Move(5, 6)
    p1Move(4, 7)
    p2Move(5, 7)
    p1Move(4, 8)

    search = new ShortestPathSearch(board, false)
    val path = search.solve.get
    assert(
      path.mkString(", ") ==
        "[(row=3, column=0): 0], [(row=4, column=0): 0], " +
          "[(row=5, column=0): 0], [(row=6, column=0): 0], " +
          "[(row=7, column=0): 0], [(row=7, column=1): 10], " +
          "[(row=7, column=2): 10], [(row=7, column=3): 10], " +
          "[(row=6, column=4): 10], [(row=5, column=5): 0], " +
          "[(row=5, column=6): 0], [(row=5, column=7): 0], " +
          "[(row=5, column=8): 10], [(row=5, column=9): 10], " +
          "[(row=5, column=10): 10], [(row=5, column=11): 10], " +
          "[(row=5, column=12): 0]",
      "Unexpected path."
    )
  }

  private def showSolvedPath(path: scala.collection.immutable.Seq[HexTransition], player1: Boolean): Unit =
    for tran <- path do
      val loc = tran.getLocation
      playerMove(loc.row, loc.col, player1)
    println("board = " + board)

  private def p1Move(row: Int, col: Int): Unit = playerMove(row, col, true)

  private def p2Move(row: Int, col: Int): Unit = playerMove(row, col, false)

  private def playerMove(row: Int, col: Int, p1: Boolean): Unit =
    if row > 0 && row <= board.getNumRows && col > 0 && col <= board.getNumCols then
      board.makeMove(TwoPlayerMove.createMove(row, col, 0, new GamePiece(p1)))
