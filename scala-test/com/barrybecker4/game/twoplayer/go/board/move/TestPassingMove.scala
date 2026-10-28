package com.barrybecker4.game.twoplayer.go.board.move

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.twoplayer.common.cache.ScoreCache
import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.{GoBoard, GoSearchable}
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoStone}
import com.barrybecker4.game.twoplayer.go.options.{GoOptions, GoWeights}
import org.junit.Assert.{assertFalse, assertTrue}
import org.junit.Test
import scala.jdk.CollectionConverters.*

/**
 * A pass is legal late in the game, but only when every stone move gives up a point.
 * A first-line endgame point that does not change the score must still be playable.
 */
class TestPassingMove extends GoTestCase:

  override protected def getBoardSize: Int = 9

  /** The empty point in the middle of the left edge, from the finished-game screenshot. */
  @Test
  def edgePointIsKeptWhenItDoesNotLoseAPoint(): Unit =
    val board = boardFromDiagram(Array(
      ".XXXX.X..",
      "XXO.OX.O.",
      "XOOOOX...",
      "XOO.OXOX.",
      ".O.OX.XO.",
      ".XOOXO.X.",
      ".XXXXXX..",
      "..OXO..O.",
      "........."
    ))
    val moves = generateLateMoves(board, whiteToPlay = true)
    assertFalse("pass replaced a move that does not lose a point", hasPass(moves))
    assertTrue("the middle of the left edge is still a move", hasStone(moves, 5, 1))

  /**
   * The corner white is dead and the left black is alive, but only once that
   * white is removed. Until a stone is played, the position is still a fight.
   */
  @Test
  def noPassWhileCornerLifeIsUnsettled(): Unit =
    val board = boardFromDiagram(Array(
      "..O.X.X.X",
      "OO.XXX.X.",
      "X.OOOX..X",
      "XOO.OXX..",
      ".XXOXX.XX",
      "X.XXX.X.X",
      ".OOX.XXX.",
      "OOX.XOOXO",
      "...X..O.."
    ))
    assertFalse("white must play the fight, not pass", hasPass(generateLateMoves(board, whiteToPlay = true)))
    assertFalse("black must play the fight, not pass", hasPass(generateLateMoves(board, whiteToPlay = false)))

  /** A group with only a large shared region is not settled, so passing is refused. */
  @Test
  def noPassWhileAGroupOnlyHasRoomToLive(): Unit =
    val board = boardFromDiagram(Array(
      "...OOX...",
      "...OXXX..",
      "OOOOOXX.O",
      "OXOOOOXX.",
      "XXXOXOX.X",
      ".X.XXX..X",
      "..X.OOXX.",
      "O.X.OXX.X",
      "........."
    ))
    assertFalse("white still has to settle the bottom", hasPass(generateLateMoves(board, whiteToPlay = true)))
    assertFalse("black still has to settle the bottom", hasPass(generateLateMoves(board, whiteToPlay = false)))

  @Test
  def passWhenEveryStoneMoveIsSuicide(): Unit =
    val board = boardFromDiagram(Array(
      "XXXXX",
      "XXXXX",
      "XX.XX",
      "XXX.X",
      "XXXXX"
    ))
    val moves = generateLateMoves(board, whiteToPlay = true)
    assertTrue("white has no stone move, so pass is the move", hasPass(moves))
    assertFalse("filling either eye is suicide", hasStone(moves, 3, 3) || hasStone(moves, 4, 4))

  private def generateLateMoves(board: GoBoard, whiteToPlay: Boolean): MoveList[GoMove] =
    val searchable = new GoSearchable(board, controller.getPlayers, new ScoreCache(), new GoOptions())
    val needed = board.getNumRows + board.getNumCols + 1
    var last = GoMove.createPassMove(0, player1 = whiteToPlay)
    (1 to needed).foreach { _ =>
      last = GoMove.createPassMove(0, player1 = whiteToPlay)
      searchable.makeInternalMove(last)
    }
    searchable.generateMoves(last, new GoWeights().getDefaultWeights)

  private def hasPass(moves: MoveList[GoMove]): Boolean =
    moves.asScala.exists(_.isPassingMove)

  private def hasStone(moves: MoveList[GoMove], row: Int, col: Int): Boolean =
    moves.asScala.exists(m => !m.isPassingMove && m.getToRow == row && m.getToCol == col)

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
