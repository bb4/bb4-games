package com.barrybecker4.game.twoplayer.go.board.move

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.twoplayer.common.TwoPlayerPlayerOptions
import com.barrybecker4.game.twoplayer.common.cache.ScoreCache
import com.barrybecker4.game.twoplayer.common.search.strategy.NegaScoutStrategy
import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.analysis.CandidateMoveAnalyzer
import com.barrybecker4.game.twoplayer.go.board.{GoBoard, GoSearchable}
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoStone}
import com.barrybecker4.game.twoplayer.go.options.{GoOptions, GoWeights}
import org.junit.Assert.{assertEquals, assertFalse, assertTrue}
import org.junit.Test
import scala.jdk.CollectionConverters.*

/**
 * Atari escapes and captures stay in the move list, including a first-line liberty.
 */
class TestTacticalMoves extends GoTestCase:

  override protected def getBoardSize: Int = 9

  /** White stone with one liberty. Black can capture; white can escape. */
  @Test
  def oneStoneAtariHasEscapeAndCapture(): Unit =
    keepEveryMove()
    val board = boardFromDiagram(Array(
      ".X...",
      "XO...",
      ".X...",
      ".....",
      "....."
    ))
    val searchable = newSearchable(board)
    val weights = new GoWeights().getDefaultWeights
    val blackJustPlayed = new GoMove(new ByteLocation(3, 2), 0, new GoStone(true))
    val whiteReplies = searchable.generateUrgentMoves(blackJustPlayed, weights)
    assertTrue("white's escape is urgent", containsUrgent(whiteReplies, 2, 3))

    val whiteJustPlayed = new GoMove(new ByteLocation(2, 2), 0, new GoStone(false))
    val blackReplies = searchable.generateUrgentMoves(whiteJustPlayed, weights)
    assertTrue("black's capture is urgent", containsUrgent(blackReplies, 2, 3))

  @Test
  def prunedEscapeIsAddedBack(): Unit =
    keepOnlyTheSingleBestMove()
    val board = edgeAtari()
    // White at (1,5) is in atari; the only liberty is (1,6), on the first line.
    val searchable = newSearchable(board)
    val blackJustPlayed = new GoMove(new ByteLocation(1, 4), 0, new GoStone(true))
    val moves = searchable.generateMoves(blackJustPlayed, new GoWeights().getDefaultWeights)
    assertTrue("the first-line escape survives the prune", contains(moves, 1, 6))
    assertTrue(moves.size > 1)

  @Test
  def firstLineLibertyOfAtariIsCandidate(): Unit =
    val candidates = new CandidateMoveAnalyzer(edgeAtari())
    assertTrue("the liberty on the first line is a candidate", candidates.isCandidateMove(1, 6))
    assertFalse("an empty first-line point far from the atari is not a candidate", candidates.isCandidateMove(1, 1))

  @Test
  def quiescenceSearchesThreeStoneEscapeAndRestoresTheBoard(): Unit =
    val board = new GoBoard(7, 0)
    val searchable = newSearchable(board)
    val stones = Seq(
      (2, 2, false), (2, 3, false), (2, 4, false),
      (1, 2, true), (1, 3, true), (1, 4, true),
      (2, 1, true), (2, 5, true),
      (3, 2, true), (3, 4, true)
    )
    stones.foreach { case (r, c, black) =>
      searchable.makeInternalMove(new GoMove(new ByteLocation(r, c), 0, new GoStone(black)))
    }
    val before = snapshot(searchable)
    val brute = controller.getPlayers.getPlayer1.getOptions
      .asInstanceOf[TwoPlayerPlayerOptions[GoMove, GoBoard]].getSearchOptions.getBruteSearchOptions
    brute.setLookAhead(0)
    brute.setQuiescence(true)
    brute.setMaxQuiescentDepth(1)
    val whiteSearch = controller.getPlayers.getPlayer2.getOptions
      .asInstanceOf[TwoPlayerPlayerOptions[GoMove, GoBoard]].getSearchOptions.getBruteSearchOptions
    whiteSearch.setLookAhead(0)
    whiteSearch.setQuiescence(true)
    whiteSearch.setMaxQuiescentDepth(1)
    val last = stones.last match
      case (r, c, black) => new GoMove(new ByteLocation(r, c), 0, new GoStone(black))
    val weights = new GoWeights().getDefaultWeights
    val urgent = searchable.generateUrgentMoves(last, weights)
    assertTrue("quiescence includes the escape from the three-stone atari", contains(urgent, 3, 3))
    val strategy = new NegaScoutStrategy[GoMove, GoBoard](searchable, weights)
    strategy.search(last, null)
    assertEquals("search restores the board", before, snapshot(searchable))

  private def keepEveryMove(): Unit = setPrune(100, 1)

  private def keepOnlyTheSingleBestMove(): Unit = setPrune(1, 1)

  private def setPrune(percent: Int, minMoves: Int): Unit =
    controller.getPlayers.asScala.foreach { player =>
      val options = player.getOptions.asInstanceOf[TwoPlayerPlayerOptions[GoMove, GoBoard]].getSearchOptions
      val best = options.getBestMovesSearchOptions
      best.setPercentageBestMoves(percent)
      best.setMinBestMoves(minMoves)
      best.setPercentLessThanBestThresh(0)
    }

  private def newSearchable(board: GoBoard): GoSearchable =
    new GoSearchable(board, controller.getPlayers, new ScoreCache(), new GoOptions())

  private def contains(moves: MoveList[GoMove], row: Int, col: Int): Boolean =
    moves.asScala.exists(m => !m.isPassingMove && m.getToRow == row && m.getToCol == col)

  private def containsUrgent(moves: MoveList[GoMove], row: Int, col: Int): Boolean =
    moves.asScala.exists(m => m.isUrgent && m.getToRow == row && m.getToCol == col)

  private def snapshot(searchable: GoSearchable): String =
    val board = searchable.getBoard
    val stones = new StringBuilder
    for r <- 1 to board.getNumRows; c <- 1 to board.getNumCols do
      val pos = board.getPosition(r, c).asInstanceOf[GoBoardPosition]
      if pos.isOccupied then stones.append(s"$r,$c,${pos.getPiece.isOwnedByPlayer1};")
    s"moves=${searchable.getNumMoves()}|stones=$stones|hash=${searchable.getHashKey().getKey}"

  /** White (1,5) in atari, liberty at (1,6). */
  private def edgeAtari(): GoBoard =
    boardFromDiagram(Array(
      "...XO....",
      "....X....",
      ".........",
      ".........",
      ".........",
      ".........",
      ".........",
      ".........",
      "........."
    ))

  private def boardFromDiagram(lines: Array[String]): GoBoard =
    val size = lines.length
    val seed = new GoBoard(size, 0)
    for r <- 1 to size; c <- 1 to size do
      val stone = lines(r - 1).charAt(c - 1) match
        case 'X' => new GoStone(true)
        case 'O' => new GoStone(false)
        case '.' => null
        case other => throw new IllegalArgumentException(s"bad char '$other'")
      if stone != null then seed.setPosition(new GoBoardPosition(r, c, null, stone))
    new GoBoard(seed)
