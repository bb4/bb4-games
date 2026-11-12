/* Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.twoplayer.common.cache.ScoreCache
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.GoSearchable
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoStone}
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import com.barrybecker4.game.twoplayer.go.options.GoOptions
import org.junit.jupiter.api.Assertions.{assertEquals, assertFalse, assertNotSame, assertTrue}
import org.junit.jupiter.api.Test

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

    assertTrue(deadBlack >= 8, s"expected dead black, got $deadBlack")
    assertEquals(0, deadWhite)
    assertEquals(blackTerritory + searchable.getNumCaptures(false) + deadWhite, blackScore, 0.001, "black score = territory + white captures + dead white (no own dead)")
    assertEquals(whiteTerritory + searchable.getNumCaptures(true) + deadBlack + komi, whiteScore, 0.001, "white score = territory + black captures + dead black + komi")
    assertTrue(whiteScore > blackScore, "white should be ahead after removing dead black + komi")

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
    assertTrue(blackScore > whiteScore, s"black ($blackScore) should beat white ($whiteScore)")
    assertTrue(searchable.players.getPlayer1.hasWon, "black should be recorded as winner")
    assertFalse(searchable.players.getPlayer2.hasWon)

  @Test
  def copyDoesNotShareBoard(): Unit =
    controller.makeMove(new GoMove(new ByteLocation(3, 3), 0, new GoStone(true)))
    val original = controller.getSearchable().asInstanceOf[GoSearchable]
    val copy = original.copy()
    assertNotSame(original.getBoard, copy.getBoard, "copy must use a distinct board")
    copy.getBoard.makeMove(new GoMove(new ByteLocation(1, 1), 0, new GoStone(false)))
    assertTrue(original.getBoard.getPosition(1, 1).isUnoccupied, "original board must stay empty at 1,1")
    assertEquals(original.getNumCaptures(true), copy.getNumCaptures(true), "capture counts survive on the copy's board")

  @Test
  def twoOpeningPassesEndTheGame(): Unit =
    val pass1 = GoMove.createPassMove(0, true)
    controller.makeMove(pass1)
    val pass2 = GoMove.createPassMove(0, false)
    controller.makeMove(pass2)
    val over = controller.getSearchable().done(pass2, true)
    assertTrue(over, "two consecutive passes must end the game even as moves 1 and 2")

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
