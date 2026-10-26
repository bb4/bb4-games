/* Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.twoplayer.common.cache.ScoreCache
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.GoSearchable
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoStone}
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import com.barrybecker4.game.twoplayer.go.options.GoOptions
import org.junit.Assert.{assertEquals, assertFalse, assertNotSame, assertTrue}
import org.junit.Test

/**
 * Integration tests for endgame score formula, komi, winner selection, copy, and two-pass.
 */
class TestEndgameScoring extends GoTestCase:

  override protected def getBoardSize: Int = 7

  /** Own dead stones must not increase your score; opponent dead + komi go to white. */
  @Test
  def finalScoreCreditsOpponentDeadAndKomi(): Unit =
    val board = boardFromDiagram(Array(
      ".......",
      ".OOOOO.",
      ".OXXXO.",
      ".OX.XO.",
      ".OXXXO.",
      ".OOOOO.",
      "......."
    ))
    val options = new GoOptions()
    options.setKomi(5.5f)
    val searchable = new GoSearchable(board, controller.getPlayers, new ScoreCache(), options)
    searchable.done(GoMove.createResignationMove(true), true)

    val blackScore = searchable.getFinalScore(true)
    val whiteScore = searchable.getFinalScore(false)
    val deadBlack = searchable.getNumDeadStonesOnBoard(true)
    val deadWhite = searchable.getNumDeadStonesOnBoard(false)
    val blackTerritory = searchable.getFinalTerritory(true)
    val whiteTerritory = searchable.getFinalTerritory(false)
    val komi = options.getKomi.toDouble

    assertTrue(s"expected dead black, got $deadBlack", deadBlack >= 8)
    assertEquals(0, deadWhite)
    assertEquals(
      "black score = territory + white captures + dead white (no own dead)",
      blackTerritory + searchable.getNumCaptures(false) + deadWhite,
      blackScore,
      0.001
    )
    assertEquals(
      "white score = territory + black captures + dead black + komi",
      whiteTerritory + searchable.getNumCaptures(true) + deadBlack + komi,
      whiteScore,
      0.001
    )
    assertTrue("white should be ahead after removing dead black + komi", whiteScore > blackScore)

  /** Winner comes from post-bookkeeping score after two passes. */
  @Test
  def winnerUsesPostBookkeepingScore(): Unit =
    // Black encloses exclusive territory on the left; white has only a tiny corner.
    val board = boardFromDiagram(Array(
      "XXXXX..",
      "X...X..",
      "X...X..",
      "X...X..",
      "XXXXX..",
      ".......",
      "......O"
    ))
    val options = new GoOptions()
    options.setKomi(0.5f)
    val searchable = new GoSearchable(board, controller.getPlayers, new ScoreCache(), options)
    // Two passes on the move list
    board.makeMove(GoMove.createPassMove(0, true))
    board.makeMove(GoMove.createPassMove(0, false))
    assertTrue(searchable.done(board.getMoveList.getLastMove.asInstanceOf[GoMove], true))

    val blackScore = searchable.getFinalScore(true)
    val whiteScore = searchable.getFinalScore(false)
    assertTrue(s"black ($blackScore) should beat white ($whiteScore)", blackScore > whiteScore)
    assertTrue("black should be recorded as winner", searchable.players.getPlayer1.hasWon)
    assertFalse(searchable.players.getPlayer2.hasWon)

  @Test
  def copyDoesNotShareBoard(): Unit =
    controller.makeMove(new GoMove(new ByteLocation(3, 3), 0, new GoStone(true)))
    val original = controller.getSearchable().asInstanceOf[GoSearchable]
    val copy = original.copy()
    assertNotSame("copy must use a distinct board", original.getBoard, copy.getBoard)
    copy.getBoard.makeMove(new GoMove(new ByteLocation(1, 1), 0, new GoStone(false)))
    assertTrue(
      "original board must stay empty at 1,1",
      original.getBoard.getPosition(1, 1).isUnoccupied
    )
    assertEquals(
      "capture counts survive on the copy's board",
      original.getNumCaptures(true),
      copy.getNumCaptures(true)
    )

  @Test
  def twoOpeningPassesEndTheGame(): Unit =
    val pass1 = GoMove.createPassMove(0, true)
    controller.makeMove(pass1)
    val pass2 = GoMove.createPassMove(0, false)
    controller.makeMove(pass2)
    val over = controller.getSearchable().done(pass2, true)
    assertTrue("two consecutive passes must end the game even as moves 1 and 2", over)

  private def boardFromDiagram(lines: Array[String]): GoBoard =
    val size = lines.length
    require(lines.forall(_.length == size), "diagram must be square")
    val seed = new GoBoard(size, 0)
    for (r <- 1 to size; c <- 1 to size) {
      val ch = lines(r - 1).charAt(c - 1)
      val stone = ch match {
        case 'X' | 'B' => new GoStone(true)
        case 'O' | 'W' => new GoStone(false)
        case '.' | ' ' => null
        case other => throw new IllegalArgumentException(s"bad char '$other'")
      }
      if (stone != null) {
        seed.setPosition(new GoBoardPosition(r, c, null, stone))
      }
    }
    new GoBoard(seed)
